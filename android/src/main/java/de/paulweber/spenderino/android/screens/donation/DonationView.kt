package de.paulweber.spenderino.android.screens.donation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import com.stripe.android.paymentsheet.model.PaymentOption
import de.paulweber.spenderino.android.R
import de.paulweber.spenderino.android.views.ButtonLoadingIndicator
import de.paulweber.spenderino.android.views.PrimaryButton
import de.paulweber.spenderino.utility.toEuroString
import de.paulweber.spenderino.viewmodel.DonationState
import de.paulweber.spenderino.viewmodel.DonationViewModel

@Composable
fun DonationView(
    state: DonationState.Base,
    viewModel: DonationViewModel,
    stripeViewModel: StripeViewModel
) {
    val paymentViewModel =
        remember {
            stripeViewModel.apply {
                configure(state.donationInfo, viewModel)
            }
        }

    WithOptionsAndConfirming(paymentViewModel) { paymentOptions, isConfirming ->
        val isInteractionBlocked =
            state.isTransactionInProgress || state.isUpdatingSum || isConfirming
        val isInteractionEnabled = !isInteractionBlocked && paymentOptions != null
        val scrollState = rememberScrollState()

        Column(modifier = Modifier.verticalScroll(scrollState)) {
            DonationHeader(state)

            DonationRow(stringResource(R.string.donation_row_donation)) {
                DonationValuePicker(!isInteractionBlocked, state, viewModel)
            }
            DonationRow(stringResource(R.string.donation_row_fee)) {
                Text(
                    state.transactionFee.toEuroString(),
                    style = MaterialTheme.typography.body2,
                )
            }
            DonationRow(stringResource(R.string.donation_row_total)) {
                Text(
                    state.totalValue.toEuroString(),
                    style = MaterialTheme.typography.body1,
                )
            }
            DonationRow(stringResource(R.string.donation_checkout_payment_row)) {
                PaymentOptionsButton(paymentOptions, stripeViewModel)
            }

            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxWidth()) {
                PrimaryButton(
                    enabled = isInteractionEnabled,
                    onClick = { stripeViewModel.confirmPayment() }
                ) {
                    when {
                        paymentOptions == null -> Text(stringResource(R.string.donation_checkout_payment_payment_missing))
                        !isInteractionBlocked -> Text(stringResource(R.string.donation_checkout_payment_donate_button))
                        else -> ButtonLoadingIndicator()
                    }
                }
            }
            Spacer(modifier = Modifier.height(60.dp))
        }
    }
}

@Composable
private fun WithOptionsAndConfirming(
    stripeViewModel: StripeViewModel,
    content: @Composable (PaymentOption?, Boolean) -> Unit
) {
    val lifecycleOwner = LocalLifecycleOwner.current

    val paymentOptionsFlow = remember(stripeViewModel.paymentOptions, lifecycleOwner) {
        stripeViewModel.paymentOptions.flowWithLifecycle(
            lifecycleOwner.lifecycle,
            Lifecycle.State.STARTED
        )
    }
    val paymentOptions by paymentOptionsFlow.collectAsState(stripeViewModel.paymentOptions.value)
    val isConfirmingFlow = remember(stripeViewModel.isConfirming, lifecycleOwner) {
        stripeViewModel.isConfirming.flowWithLifecycle(
            lifecycleOwner.lifecycle,
            Lifecycle.State.STARTED
        )
    }
    val isConfirming by isConfirmingFlow.collectAsState(stripeViewModel.isConfirming.value)

    content(paymentOptions, isConfirming)
}
