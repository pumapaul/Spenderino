package de.paulweber.spenderino.viewmodel

import de.paulweber.spenderino.model.networking.BASE_URL
import de.paulweber.spenderino.model.repositories.RemoteException
import de.paulweber.spenderino.model.repositories.donation.DonationInformation
import de.paulweber.spenderino.model.repositories.donation.DonationRepository
import de.paulweber.spenderino.model.repositories.donation.StripeResult
import de.paulweber.spenderino.model.repositories.toRemoteException
import de.paulweber.spenderino.utility.BuildKonfig
import de.paulweber.spenderino.utility.L10n
import de.paulweber.spenderino.utility.toEuroString
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.launch
import org.koin.core.component.inject

sealed class DonationRoute {
    data class Alert(override val alert: AlertViewModel) : DonationRoute(), AlertRoute
}

sealed class DonationState {
    object Loading : DonationState()
    sealed class Error : DonationState() {
        object NetworkError : Error()
        object UnknownCode : Error()
    }

    data class Base(
        val isTransactionInProgress: Boolean,
        val isUpdatingSum: Boolean,
        val donationValue: Long,
        val transactionFee: Long,
        val totalValue: Long,
        val donationInfo: DonationInformation
    ) : DonationState()

    data class Success(val message: String) : DonationState()
}

sealed class DonationAction {
    object Reload : DonationAction()
    data class ChangeDonationValue(val newValue: Long) : DonationAction()
    data class ChangeTransactionInProgress(val inProgress: Boolean) : DonationAction()
    data class TransactionResult(val result: StripeResult) : DonationAction()
}

class DonationViewModel(
    url: String,
    state: DonationState = DonationState.Loading,
    route: DonationRoute? = null,
    onBack: () -> Unit = {}
) : ViewModel<DonationAction, DonationRoute, DonationState>(state, route, onBack) {
    private val donationRepo: DonationRepository by inject()
    val stripeKey = BuildKonfig.STRIPE_KEY
    private val code = url.removePrefix("$BASE_URL/r/")

    init {
        fetchPaymentData()
    }

    private fun fetchPaymentData() = scope.launch {
        setState(DonationState.Loading)
        donationRepo.getPaymentIntentClientSecret(code).fold(
            onSuccess = { createInitialSuccessState(it) },
            onFailure = { createErrorState(it) }
        )
    }

    private fun createInitialSuccessState(donationInfo: DonationInformation) {
        val startingValue = 100L
        val fee = calculateFee(startingValue)
        val total = startingValue + fee
        val newState = DonationState.Base(
            false,
            isUpdatingSum = false,
            donationValue = startingValue,
            transactionFee = fee,
            totalValue = total,
            donationInfo = donationInfo
        )
        setState(newState)
    }

    private fun createErrorState(throwable: Throwable) {
        val error = when (val remoteException = throwable.toRemoteException()) {
            is RemoteException.Client -> {
                if (remoteException.cause.response.status == HttpStatusCode.NotFound) {
                    DonationState.Error.UnknownCode
                } else DonationState.Error.NetworkError
            }
            else -> DonationState.Error.NetworkError
        }
        setState(error)
    }

    private fun calculateFee(donation: Long): Long {
        val relativeFee = donation * 3 / 100
        return relativeFee + 26
    }

    override fun perform(action: DonationAction) {
        when (action) {
            is DonationAction.ChangeDonationValue -> onDonationValueChanged(action.newValue)
            is DonationAction.ChangeTransactionInProgress -> onTransactionInProgressChanged(action.inProgress)
            is DonationAction.TransactionResult -> onTransactionResult(action.result)
            DonationAction.Reload -> fetchPaymentData()
        }
    }

    private fun onTransactionResult(stripeResult: StripeResult) {
        onTransactionInProgressChanged(false)

        when (stripeResult) {
            is StripeResult.Completed -> setSuccessState()
            is StripeResult.Canceled -> Unit
            is StripeResult.Failed -> {
                val alert = AlertViewModel(
                    L10n.get("donation_alert_payment_failed_title"),
                    stripeResult.localizedErrorMessage,
                    listOf(),
                    this::routeToNull
                )
                setRoute(DonationRoute.Alert(alert))
            }
        }
    }

    private fun setSuccessState() {
        val currentState = state.value as DonationState.Base
        val recipientName = currentState.donationInfo.recipient.name
        val amount = currentState.donationValue
        val successMessage = L10n.format(
            "donation_success",
            amount.toEuroString(),
            recipientName
        )
        setState(DonationState.Success(successMessage))
    }

    private fun onTransactionInProgressChanged(inProgress: Boolean) {
        if (state.value is DonationState.Base) {
            val oldState = state.value as DonationState.Base
            val newState = oldState.copy(isTransactionInProgress = inProgress)
            setState(newState)
        }
    }

    private fun onDonationValueChanged(newValue: Long) {
        if (state.value is DonationState.Base) {
            val fee = calculateFee(newValue)
            val total = newValue + fee
            val newState = (state.value as DonationState.Base).copy(
                donationValue = newValue,
                transactionFee = fee,
                totalValue = total
            )
            setState(newState)
            updateDonationSum(total, newValue)
        }
    }

    private fun updateDonationSum(newValue: Long, withoutFees: Long) = scope.launch {
        if (state.value is DonationState.Base) {
            val currentState = state.value as DonationState.Base
            setState(currentState.copy(isUpdatingSum = true))
            val paymentIntentId = currentState.donationInfo.paymentIntentId
            donationRepo.updateDonationSum(paymentIntentId, newValue, withoutFees)
            setState(currentState.copy(isUpdatingSum = false))
        }
    }
}
