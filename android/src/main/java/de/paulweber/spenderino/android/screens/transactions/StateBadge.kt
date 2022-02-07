package de.paulweber.spenderino.android.screens.transactions

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import de.paulweber.spenderino.android.R
import de.paulweber.spenderino.model.repositories.transaction.Transaction

@Composable
fun StateBadge(state: Transaction.State) {
    val color = when (state) {
        Transaction.State.PENDING -> Color.Yellow
        Transaction.State.FAILED -> Color.Red
        Transaction.State.COMPLETE -> Color.Green
    }
    val text = when (state) {
        Transaction.State.PENDING -> stringResource(R.string.transactions_item_pending)
        Transaction.State.FAILED -> stringResource(R.string.transactions_item_failed)
        Transaction.State.COMPLETE -> stringResource(R.string.transactions_item_complete)
    }

    Surface(
        color = color,
        contentColor = contentColorFor(color),
        elevation = 4.dp,
        shape = RoundedCornerShape(8)
    ) {
        Box(
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text,
                style = MaterialTheme.typography.caption,
                modifier = Modifier.padding(6.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun BadgePreview() {
    StateBadge(state = Transaction.State.COMPLETE)
}
