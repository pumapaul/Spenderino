package de.paulweber.spenderino.android.screens.transactions

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Icon
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowLeft
import androidx.compose.material.icons.outlined.ArrowRight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import de.paulweber.spenderino.android.R
import de.paulweber.spenderino.utility.toEuroString
import de.paulweber.spenderino.viewmodel.TransactionState

private val outgoingDonationColor = Color.Cyan
private val incomingDonationColor = Color.Green

@Composable
fun DonationItemView(item: TransactionState.TransactionItem.DonationItem) {
    Surface(elevation = 1.dp) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            DirectionIcon(item.direction, modifier = Modifier.size(44.dp, 44.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    item.title,
                    style = MaterialTheme.typography.body1,
                )
                CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                    Subtitle(item)
                }
            }
            Spacer(Modifier.width(12.dp))
            StateBadge(item.state)
        }
    }
}

@Composable
private fun Subtitle(item: TransactionState.TransactionItem.DonationItem) {
    Row {
        Text(
            item.timestampString,
            style = MaterialTheme.typography.caption,
        )

        when (item.direction) {
            TransactionState.TransactionItem.DonationItem.Direction.OUTGOING -> {
                Row {
                    Spacer(modifier = Modifier.weight(1f))
                    Text(
                        stringResource(R.string.transactions_item_fee, item.fees.toEuroString()),
                        style = MaterialTheme.typography.caption,
                    )
                }
            }
            else -> Unit
        }
    }
}

@Composable
private fun DirectionIcon(
    direction: TransactionState.TransactionItem.DonationItem.Direction,
    modifier: Modifier
) {
    when (direction) {
        TransactionState.TransactionItem.DonationItem.Direction.OUTGOING -> Icon(
            Icons.Outlined.ArrowRight,
            contentDescription = "outgoing donation icon",
            tint = outgoingDonationColor,
            modifier = modifier
        )
        TransactionState.TransactionItem.DonationItem.Direction.INCOMING -> Icon(
            Icons.Outlined.ArrowLeft,
            contentDescription = "incoming donation icon",
            tint = incomingDonationColor,
            modifier = modifier
        )
    }
}
