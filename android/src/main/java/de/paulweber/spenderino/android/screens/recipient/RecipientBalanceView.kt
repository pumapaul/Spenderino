package de.paulweber.spenderino.android.screens.recipient

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import de.paulweber.spenderino.android.views.ErrorView
import de.paulweber.spenderino.android.views.LoadingView
import de.paulweber.spenderino.viewmodel.RecipientAction
import de.paulweber.spenderino.viewmodel.RecipientState
import de.paulweber.spenderino.viewmodel.RecipientViewModel

@Composable
fun RecipientBalanceView(
    state: RecipientState.BalanceState,
    viewModel: RecipientViewModel
) {
    Box(
        modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth()
    ) {
        when (state) {
            is RecipientState.BalanceState.Loading -> LoadingView()
            is RecipientState.BalanceState.Error -> ErrorView { viewModel.perform(RecipientAction.ReloadBalance) }
            is RecipientState.BalanceState.Base -> BalanceView(
                state.stateHolder,
                false,
                viewModel
            )
            is RecipientState.BalanceState.Reloading -> BalanceView(
                state.stateHolder,
                true,
                viewModel
            )
        }
    }
}

@Composable
private fun BalanceView(
    state: RecipientState.BalanceState.StateHolder,
    isLoading: Boolean,
    viewModel: RecipientViewModel
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Absolute.spacedBy(32.dp),
        modifier = Modifier
            .padding(32.dp)
    ) {
        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.size(48.dp))
        } else {
            IconButton(onClick = { viewModel.perform(RecipientAction.ReloadBalance) }) {
                Icon(
                    Icons.Outlined.Refresh,
                    contentDescription = "Refresh button",
                    tint = MaterialTheme.colors.primary
                )
            }
        }

        CurrentBalanceView(state.currentBalance)
        CreateWithdrawalView(state, viewModel)
    }
}
