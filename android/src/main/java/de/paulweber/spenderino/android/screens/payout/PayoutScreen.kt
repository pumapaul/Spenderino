package de.paulweber.spenderino.android.screens.payout

import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.ramcosta.composedestinations.annotation.Destination
import de.paulweber.spenderino.android.R
import de.paulweber.spenderino.android.destinations.PayoutScreenDestination
import de.paulweber.spenderino.android.utility.CenterBoxed
import de.paulweber.spenderino.android.utility.ConfigureRouting
import de.paulweber.spenderino.android.utility.Screen
import de.paulweber.spenderino.android.views.ErrorView
import de.paulweber.spenderino.android.views.LoadingView
import de.paulweber.spenderino.viewmodel.PayoutState
import de.paulweber.spenderino.viewmodel.PayoutViewModel

@Destination
@Composable
fun PayoutScreen(
    navigator: NavHostController,
    viewModel: PayoutViewModel
) {
    ConfigureRouting(viewModel, navigator, PayoutScreenDestination) {}

    Screen(
        title = stringResource(R.string.payout_title),
        viewModel = viewModel,
        hasBackButton = true,
        destination = PayoutScreenDestination,
        navigator = navigator
    ) {
        CenterBoxed(padding = 32.dp) {
            when (it) {
                PayoutState.Loading -> LoadingView()
                PayoutState.Error -> ErrorView()
                is PayoutState.QRCode -> PayoutView(it)
                PayoutState.Success -> Text(
                    stringResource(R.string.payout_success),
                    style = MaterialTheme.typography.body1,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
