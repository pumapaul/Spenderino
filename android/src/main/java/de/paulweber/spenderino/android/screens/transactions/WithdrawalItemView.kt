package de.paulweber.spenderino.android.screens.transactions

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowCircleDown
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import de.paulweber.spenderino.utility.toEuroString
import de.paulweber.spenderino.viewmodel.TransactionState

@Composable
fun WithdrawalItemView(withdrawalItem: TransactionState.TransactionItem.WithdrawalItem) {
    Surface(elevation = 1.dp) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp).fillMaxWidth()
        ) {
            Icon(Icons.Outlined.ArrowCircleDown, contentDescription = null)
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                withdrawalItem.amount.toEuroString(),
                style = MaterialTheme.typography.body1,
            )
            Text(
                withdrawalItem.text,
                style = MaterialTheme.typography.body2,
            )
        }
    }
}
