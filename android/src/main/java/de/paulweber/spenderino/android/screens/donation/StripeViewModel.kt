package de.paulweber.spenderino.android.screens.donation

import androidx.activity.ComponentActivity
import com.stripe.android.paymentsheet.PaymentSheet
import com.stripe.android.paymentsheet.PaymentSheetResult
import com.stripe.android.paymentsheet.model.PaymentOption
import de.paulweber.spenderino.model.repositories.donation.DonationInformation
import de.paulweber.spenderino.model.repositories.donation.StripeResult
import de.paulweber.spenderino.viewmodel.DonationAction
import de.paulweber.spenderino.viewmodel.DonationViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class StripeViewModel(
    activity: ComponentActivity,
) {
    private lateinit var donationViewModel: DonationViewModel

    val paymentOptions: StateFlow<PaymentOption?>
        get() = mutablePaymentOptions
    val isConfirming: StateFlow<Boolean>
        get() = mutableIsConfirming
    private val mutablePaymentOptions = MutableStateFlow<PaymentOption?>(null)
    private val mutableIsConfirming = MutableStateFlow(false)

    private val flowController = PaymentSheet.FlowController.create(
        activity,
        { onPaymentOptions(it) },
        { onPaymentResult(it) }
    )

    fun configure(donationInformation: DonationInformation, viewModel: DonationViewModel) {
        donationViewModel = viewModel
        mutablePaymentOptions.value = null

        flowController.configureWithPaymentIntent(
            donationInformation.paymentSecret,
            PaymentSheet.Configuration(
                merchantDisplayName = "Spenderino",
                customer = PaymentSheet.CustomerConfiguration(
                    donationInformation.customerId,
                    donationInformation.customerSecret
                )
            )
        ) { isReady, _ -> onConfigured(isReady) }
    }

    fun presentPaymentOptions() {
        flowController.presentPaymentOptions()
    }

    fun confirmPayment() {
        mutableIsConfirming.value = true
        flowController.confirm()
    }

    private fun onPaymentOptions(paymentOption: PaymentOption?) {
        mutablePaymentOptions.value = paymentOption
    }

    private fun onPaymentResult(paymentSheetResult: PaymentSheetResult) {
        val result = when (paymentSheetResult) {
            is PaymentSheetResult.Canceled -> {
                StripeResult.Canceled
            }
            is PaymentSheetResult.Failed -> {
                StripeResult.Failed(paymentSheetResult.error.localizedMessage ?: "")
            }
            is PaymentSheetResult.Completed -> {
                StripeResult.Completed
            }
        }
        donationViewModel.perform(DonationAction.TransactionResult(result))
        mutableIsConfirming.value = false
    }

    private fun onConfigured(success: Boolean) {
        if (success) {
            mutablePaymentOptions.value = flowController.getPaymentOption()
        } else {
            donationViewModel.perform(DonationAction.Reload)
        }
    }
}
