package de.paulweber.spenderino.android.screens.transactions

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import com.ramcosta.composedestinations.annotation.Destination
import de.paulweber.spenderino.android.R
import de.paulweber.spenderino.android.destinations.TransactionsScreenDestination
import de.paulweber.spenderino.android.utility.ConfigureRouting
import de.paulweber.spenderino.android.utility.Screen
import de.paulweber.spenderino.android.views.ErrorView
import de.paulweber.spenderino.android.views.LoadingView
import de.paulweber.spenderino.viewmodel.TransactionAction
import de.paulweber.spenderino.viewmodel.TransactionState
import de.paulweber.spenderino.viewmodel.TransactionsViewModel

@Destination
@Composable
fun TransactionsScreen(
    navigator: NavHostController,
    viewModel: TransactionsViewModel
) {
    ConfigureRouting(viewModel, navigator, TransactionsScreenDestination) {}

    Screen(
        title = stringResource(R.string.transactions_title),
        viewModel = viewModel,
        hasBackButton = true,
        destination = TransactionsScreenDestination,
        navigator = navigator
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth()
        ) {
            when (it) {
                is TransactionState.Error -> ErrorView { viewModel.perform(TransactionAction.RELOAD) }
                is TransactionState.Base -> TransactionList(
                    list = it.transactions,
                    isLoading = false,
                    viewModel = viewModel
                )
                TransactionState.Loading -> LoadingView()
                is TransactionState.Reloading -> TransactionList(
                    list = it.transactions,
                    isLoading = true,
                    viewModel = viewModel
                )
            }
        }
    }
}
