package de.paulweber.spenderino.android.screens.donation

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CreditCard
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.stripe.android.paymentsheet.model.PaymentOption
import de.paulweber.spenderino.android.R

@Composable
fun PaymentOptionsButton(chosenOption: PaymentOption?, stripeViewModel: StripeViewModel) {
    Row(
        horizontalArrangement = Arrangement.Absolute.spacedBy(8.dp),
        modifier = Modifier.clickable { stripeViewModel.presentPaymentOptions() }
    ) {
        if (chosenOption == null) {
            Icon(
                Icons.Outlined.CreditCard,
                contentDescription = "credit card icon",
                tint = MaterialTheme.colors.onBackground
            )
            Text(
                stringResource(R.string.donation_checkout_payment_choose_button),
                color = MaterialTheme.colors.primary,
                style = MaterialTheme.typography.body1
            )
        } else {
            Image(painterResource(chosenOption.drawableResourceId), null)
            Text(
                chosenOption.label,
                color = MaterialTheme.colors.primary,
                style = MaterialTheme.typography.body1
            )
        }
    }
}
