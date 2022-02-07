package de.paulweber.spenderino.android.screens.account

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Logout
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.navigateTo
import de.paulweber.spenderino.android.R
import de.paulweber.spenderino.android.destinations.AccountScreenDestination
import de.paulweber.spenderino.android.destinations.AlertDestination
import de.paulweber.spenderino.android.destinations.LogoutAlertDestination
import de.paulweber.spenderino.android.utility.ConfigureRouting
import de.paulweber.spenderino.android.utility.Screen
import de.paulweber.spenderino.android.views.SettingsIcon
import de.paulweber.spenderino.android.views.SettingsRow
import de.paulweber.spenderino.viewmodel.AccountAction
import de.paulweber.spenderino.viewmodel.AccountRoute
import de.paulweber.spenderino.viewmodel.AccountState
import de.paulweber.spenderino.viewmodel.AccountViewModel
import de.paulweber.spenderino.viewmodel.AlertViewModel

@Destination
@Composable
fun AccountScreen(
    navigator: NavHostController,
    viewModel: AccountViewModel,
    alertRoute: (AlertViewModel) -> Unit,
) {
    AccountScreenRouting(navigator, viewModel, alertRoute)

    Screen(
        title = stringResource(R.string.account_title),
        viewModel = viewModel,
        hasBackButton = true,
        destination = AccountScreenDestination,
        navigator = navigator
    ) {
        val scrollState = rememberScrollState()

        Column(Modifier.verticalScroll(scrollState)) {
            AccountView(it, viewModel)

            Spacer(Modifier.height(16.dp))

            if (it is AccountState.Registered || it is AccountState.SetupComplete) {
                Spacer(Modifier.height(32.dp))
                SettingsRow(
                    icon = {
                        SettingsIcon(
                            Icons.Outlined.Logout,
                            "logout icon",
                            Color.Red
                        )
                    },
                    title = stringResource(R.string.account_logout_button),
                    action = { viewModel.perform(AccountAction.Logout) }
                )
            }
            Spacer(Modifier.height(100.dp))
        }
    }
}

@Composable
private fun AccountScreenRouting(
    navigator: NavHostController,
    viewModel: AccountViewModel,
    alertRoute: (AlertViewModel) -> Unit,
) {
    ConfigureRouting(
        viewModel = viewModel,
        navigator = navigator,
        destination = AccountScreenDestination,
        routing = {
            when (it) {
                is AccountRoute.Alert -> {
                    alertRoute(it.alert)
                    navigator.navigateTo(AlertDestination)
                }
                is AccountRoute.LogoutAlert -> navigator.navigateTo(LogoutAlertDestination)
            }
        }
    )
}
