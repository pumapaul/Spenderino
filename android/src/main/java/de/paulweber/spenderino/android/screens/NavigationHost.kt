package de.paulweber.spenderino.android.screens

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.dialog
import com.ramcosta.composedestinations.utils.composable
import de.paulweber.spenderino.android.destinations.AccountScreenDestination
import de.paulweber.spenderino.android.destinations.AlertDestination
import de.paulweber.spenderino.android.destinations.CreateAccountScreenDestination
import de.paulweber.spenderino.android.destinations.DonationScannerScreenDestination
import de.paulweber.spenderino.android.destinations.DonationScreenDestination
import de.paulweber.spenderino.android.destinations.LogoutAlertDestination
import de.paulweber.spenderino.android.destinations.PayoutScreenDestination
import de.paulweber.spenderino.android.destinations.PreferencesScreenDestination
import de.paulweber.spenderino.android.destinations.RecipientScreenDestination
import de.paulweber.spenderino.android.destinations.TransactionsScreenDestination
import de.paulweber.spenderino.android.screens.account.AccountScreen
import de.paulweber.spenderino.android.screens.account.CreateAccountScreen
import de.paulweber.spenderino.android.screens.account.LogoutAlert
import de.paulweber.spenderino.android.screens.donation.DonationScreen
import de.paulweber.spenderino.android.screens.donation.StripeViewModel
import de.paulweber.spenderino.android.screens.payout.PayoutScreen
import de.paulweber.spenderino.android.screens.preferences.PreferencesScreen
import de.paulweber.spenderino.android.screens.recipient.RecipientScreen
import de.paulweber.spenderino.android.screens.scanner.DonationScannerScreen
import de.paulweber.spenderino.android.screens.transactions.TransactionsScreen
import de.paulweber.spenderino.android.views.Alert
import de.paulweber.spenderino.viewmodel.AccountViewModel
import de.paulweber.spenderino.viewmodel.AlertViewModel
import de.paulweber.spenderino.viewmodel.DonationViewModel
import de.paulweber.spenderino.viewmodel.PayoutViewModel
import de.paulweber.spenderino.viewmodel.TabViewModel
import de.paulweber.spenderino.viewmodel.TransactionsViewModel

@Suppress("LongMethod")
@Composable
fun NavigationHost(
    navController: NavHostController,
    tabViewModel: TabViewModel,
    stripeViewModel: StripeViewModel,
    openSettingsClosure: () -> Unit
) {

    // General
    var alertViewModel: AlertViewModel? = null

    // DonationTab
    var donationViewModel: DonationViewModel? = null

    // RecipientTab
    var createAccountViewModel: AccountViewModel? = null
    var payoutViewModel: PayoutViewModel? = null

    // PreferencesTab
    var accountViewModel: AccountViewModel? = null
    var transactionsViewModel: TransactionsViewModel? = null

    NavHost(navController, startDestination = RecipientScreenDestination.route) {
        // General
        dialog(AlertDestination.route) {
            Alert(navigator = navController, viewModel = alertViewModel!!)
        }

        // DonationTab
        composable(DonationScannerScreenDestination) {
            DonationScannerScreen(
                navigator = navController,
                viewModel = tabViewModel.donationScannerViewModel,
                donationRoute = { donationViewModel = it },
                alertRoute = { alertViewModel = it },
                openSettingsClosure = openSettingsClosure
            )
        }

        composable(DonationScreenDestination) {
            DonationScreen(
                navigator = navController,
                viewModel = donationViewModel!!,
                stripeViewModel = stripeViewModel,
                alertRoute = { alertViewModel = it }
            )
        }

        // RecipientTab
        composable(RecipientScreenDestination) {
            RecipientScreen(
                navigator = navController,
                viewModel = tabViewModel.recipientViewModel,
                createAccountRoute = { createAccountViewModel = it },
                payoutRoute = { payoutViewModel = it },
                alertRoute = { alertViewModel = it }
            )
        }

        composable(CreateAccountScreenDestination) {
            CreateAccountScreen(
                navigator = navController,
                viewModel = createAccountViewModel!!,
                alertRoute = { alertViewModel = it }
            )
        }

        composable(PayoutScreenDestination) {
            PayoutScreen(navigator = navController, viewModel = payoutViewModel!!)
        }

        // PreferencesTab
        composable(PreferencesScreenDestination) {
            PreferencesScreen(
                navigator = navController,
                viewModel = tabViewModel.preferencesViewModel,
                accountRoute = { accountViewModel = it },
                transactionsRoute = { transactionsViewModel = it }
            )
        }

        composable(AccountScreenDestination) {
            AccountScreen(
                navigator = navController,
                viewModel = accountViewModel!!,
                alertRoute = { alertViewModel = it },
            )
        }

        dialog(LogoutAlertDestination.route) {
            LogoutAlert(navigator = navController, viewModel = accountViewModel!!)
        }

        composable(TransactionsScreenDestination) {
            TransactionsScreen(navigator = navController, viewModel = transactionsViewModel!!)
        }
    }
}
