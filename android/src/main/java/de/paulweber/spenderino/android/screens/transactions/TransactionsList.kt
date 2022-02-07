package de.paulweber.spenderino.android.screens.transactions

import androidx.compose.foundation.layout.Arrangement.Absolute.spacedBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Icon
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import de.paulweber.spenderino.android.R
import de.paulweber.spenderino.viewmodel.TransactionAction
import de.paulweber.spenderino.viewmodel.TransactionState
import de.paulweber.spenderino.viewmodel.TransactionsViewModel

@Composable
fun TransactionList(
    list: List<TransactionState.TransactionItem>,
    isLoading: Boolean,
    viewModel: TransactionsViewModel
) {
    val isRefreshing = rememberSwipeRefreshState(isRefreshing = isLoading)
    Box(
        contentAlignment = Alignment.Center
    ) {
        SwipeRefresh(
            state = isRefreshing,
            onRefresh = { viewModel.perform(TransactionAction.RELOAD) },
            modifier = Modifier
                .fillMaxSize()
        ) {
            LazyColumn(verticalArrangement = spacedBy(4.dp)) {
                items(list) {
                    when (it) {
                        is TransactionState.TransactionItem.DonationItem -> DonationItemView(it)
                        is TransactionState.TransactionItem.WithdrawalItem -> WithdrawalItemView(it)
                    }
                }
            }
        }

        if (list.isEmpty()) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = spacedBy(32.dp)
            ) {
                CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                    Text(
                        stringResource(R.string.transactions_empty_label),
                        style = MaterialTheme.typography.body2,
                        textAlign = TextAlign.Center
                    )
                }
                Button(
                    onClick = { viewModel.perform(TransactionAction.RELOAD) },
                    enabled = !isRefreshing.isRefreshing,
                    modifier = Modifier.size(150.dp, 50.dp)
                ) {
                    if (isRefreshing.isRefreshing) {
                        CircularProgressIndicator(
                            color = MaterialTheme.colors.onPrimary.copy(alpha = 0.5f)
                        )
                    } else {
                        Icon(Icons.Outlined.Refresh, contentDescription = "refresh button")
                        Text(stringResource(id = R.string.transactions_empty_button))
                    }
                }
            }
        }
    }
}
