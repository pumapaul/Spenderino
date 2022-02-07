package de.paulweber.spenderino.android.screens.recipient

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.navigateTo
import de.paulweber.spenderino.android.R
import de.paulweber.spenderino.android.destinations.AlertDestination
import de.paulweber.spenderino.android.destinations.CreateAccountScreenDestination
import de.paulweber.spenderino.android.destinations.PayoutScreenDestination
import de.paulweber.spenderino.android.destinations.RecipientScreenDestination
import de.paulweber.spenderino.android.utility.CenterBoxed
import de.paulweber.spenderino.android.utility.ConfigureRouting
import de.paulweber.spenderino.android.utility.Screen
import de.paulweber.spenderino.android.views.ErrorView
import de.paulweber.spenderino.android.views.LoadingView
import de.paulweber.spenderino.viewmodel.AccountViewModel
import de.paulweber.spenderino.viewmodel.AlertViewModel
import de.paulweber.spenderino.viewmodel.PayoutViewModel
import de.paulweber.spenderino.viewmodel.RecipientAction
import de.paulweber.spenderino.viewmodel.RecipientRoute
import de.paulweber.spenderino.viewmodel.RecipientState
import de.paulweber.spenderino.viewmodel.RecipientViewModel

@Destination(start = true)
@Composable
fun RecipientScreen(
    navigator: NavHostController,
    viewModel: RecipientViewModel,
    createAccountRoute: (AccountViewModel) -> Unit,
    payoutRoute: (PayoutViewModel) -> Unit,
    alertRoute: (AlertViewModel) -> Unit
) {
    ConfigureRouting(
        viewModel,
        navigator,
        destination = RecipientScreenDestination,
        handleBackButton = false,
        routing = {
            when (it) {
                is RecipientRoute.Account -> {
                    createAccountRoute(it.viewModel)
                    navigator.navigateTo(CreateAccountScreenDestination)
                }
                is RecipientRoute.Payout -> {
                    payoutRoute(it.viewModel)
                    navigator.navigateTo(PayoutScreenDestination)
                }
                is RecipientRoute.Alert -> {
                    alertRoute(it.alert)
                    navigator.navigateTo(AlertDestination)
                }
            }
        }
    )
    Screen(
        title = stringResource(R.string.recipient_title),
        viewModel = viewModel,
        hasBackButton = false,
        destination = RecipientScreenDestination,
        navigator = navigator
    ) {
        when (it) {
            RecipientState.Loading -> CenterBoxed(32.dp) { LoadingView() }
            RecipientState.Anonymous -> CenterBoxed(32.dp) { RecipientAnonView(viewModel) }
            RecipientState.Error -> CenterBoxed(32.dp) {
                ErrorView {
                    viewModel.perform(
                        RecipientAction.ReloadBalance
                    )
                }
            }
            is RecipientState.Pager -> RecipientPagerView(
                viewModel,
                it
            )
            is RecipientState.Registered -> CenterBoxed(32.dp) {
                RecipientRegisteredView(
                    viewModel
                )
            }
        }
    }
}
