package de.paulweber.spenderino.android.screens.preferences

import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Receipt
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.navigateTo
import de.paulweber.spenderino.android.R
import de.paulweber.spenderino.android.destinations.AccountScreenDestination
import de.paulweber.spenderino.android.destinations.PreferencesScreenDestination
import de.paulweber.spenderino.android.destinations.TransactionsScreenDestination
import de.paulweber.spenderino.android.utility.ConfigureRouting
import de.paulweber.spenderino.android.utility.Screen
import de.paulweber.spenderino.android.views.SettingsIcon
import de.paulweber.spenderino.android.views.SettingsRow
import de.paulweber.spenderino.viewmodel.AccountViewModel
import de.paulweber.spenderino.viewmodel.PreferencesAction
import de.paulweber.spenderino.viewmodel.PreferencesRoute
import de.paulweber.spenderino.viewmodel.PreferencesViewModel
import de.paulweber.spenderino.viewmodel.TransactionsViewModel

@Destination
@Composable
fun PreferencesScreen(
    navigator: NavHostController,
    viewModel: PreferencesViewModel,
    accountRoute: (AccountViewModel) -> Unit,
    transactionsRoute: (TransactionsViewModel) -> Unit
) {

    ConfigureRouting(
        viewModel = viewModel,
        navigator = navigator,
        destination = PreferencesScreenDestination,
        handleBackButton = false,
        routing = {
            when (it) {
                is PreferencesRoute.Account -> {
                    accountRoute(it.viewModel)
                    navigator.navigateTo(AccountScreenDestination)
                }
                is PreferencesRoute.Transactions -> {
                    transactionsRoute(it.viewModel)
                    navigator.navigateTo(TransactionsScreenDestination)
                }
                else -> Unit
            }
        }
    )
    Screen(
        title = stringResource(R.string.preferences_title),
        viewModel = viewModel,
        hasBackButton = false,
        destination = PreferencesScreenDestination,
        navigator = navigator
    ) {
        Column {
            AccountRow(it, viewModel)
            SettingsRow(
                icon = {
                    SettingsIcon(
                        icon = Icons.Outlined.Receipt,
                        contentDescription = "donation history icon",
                        backgroundColor = Color.Cyan
                    )
                },
                title = stringResource(R.string.preferences_transactions_row),
                subtitle = stringResource(R.string.preferences_transactions_row_info),
                action = { viewModel.perform(PreferencesAction.TRANSACTIONS) }
            )
        }
    }
}
