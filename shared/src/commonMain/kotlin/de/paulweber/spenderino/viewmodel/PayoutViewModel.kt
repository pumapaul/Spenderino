package de.paulweber.spenderino.viewmodel

import de.paulweber.spenderino.model.repositories.balance.BalanceRepository
import de.paulweber.spenderino.model.repositories.balance.Withdrawal
import de.paulweber.spenderino.utility.L10n
import de.paulweber.spenderino.utility.toEuroString
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.core.component.inject

sealed class PayoutState {
    object Loading : PayoutState()
    object Error : PayoutState()

    @Suppress("ArrayInDataClass")
    data class QRCode(val text: String, val code: ByteArray) : PayoutState()
    object Success : PayoutState()
}

enum class PayoutAction {
    ON_DISMISS
}

class PayoutViewModel(
    withdrawal: Withdrawal,
    state: PayoutState = PayoutState.Loading,
    onBack: () -> Unit = {}
) : ViewModel<PayoutAction, Unit, PayoutState>(state, Unit, onBack) {
    private val balanceRepo: BalanceRepository by inject()
    private val withdrawalText = L10n.format("balance_withdrawal", withdrawal.amount.toEuroString())
    private val pollingJob: Job

    init {
        fetchQrCode()
        pollingJob = scope.launch { pollWithdrawal() }
    }

    private fun fetchQrCode() = scope.launch {
        balanceRepo.getQrCode().fold(
            onSuccess = { setState(PayoutState.QRCode(withdrawalText, it)) },
            onFailure = { setState(PayoutState.Error) }
        )
    }

    private suspend fun pollWithdrawal() {
        balanceRepo.pollIsWithdrawalActive().fold(
            onSuccess = {
                if (it) {
                    delay(2000)
                    pollWithdrawal()
                } else {
                    setState(PayoutState.Success)
                }
            },
            onFailure = {
                delay(2000)
                pollWithdrawal()
            }
        )
    }

    override fun perform(action: PayoutAction) {
        when (action) {
            PayoutAction.ON_DISMISS -> pollingJob.cancel()
        }
    }

    override fun onBackButton() {
        super.onBackButton()
        pollingJob.cancel()
    }
}
