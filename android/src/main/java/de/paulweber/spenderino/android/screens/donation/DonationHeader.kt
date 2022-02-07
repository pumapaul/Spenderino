package de.paulweber.spenderino.android.screens.donation

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context.CLIPBOARD_SERVICE
import androidx.compose.foundation.layout.Arrangement.Absolute.spacedBy
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import de.paulweber.spenderino.android.R
import de.paulweber.spenderino.viewmodel.DonationState

@Composable
fun DonationHeader(state: DonationState.Base) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = spacedBy(12.dp),
        modifier = Modifier.padding(24.dp)
    ) {
        Text(
            stringResource(R.string.donation_headline),
            style = MaterialTheme.typography.h5,
            textAlign = TextAlign.Center
        )
        Text(
            state.donationInfo.recipient.name,
            style = MaterialTheme.typography.h2,
            textAlign = TextAlign.Center
        )
        CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
            Text(
                stringResource(R.string.donation_text),
                style = MaterialTheme.typography.body1,
                textAlign = TextAlign.Center
            )
            Column(
                verticalArrangement = spacedBy(4.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CardView(
                    label = stringResource(R.string.donation_card_succeeds_label),
                    card = stringResource(R.string.donation_card_succeeds_card),
                    toPaste = stringResource(R.string.donation_card_succeeds_paste)
                )
                CardView(
                    label = stringResource(R.string.donation_card_auth_label),
                    card = stringResource(R.string.donation_card_auth_card),
                    toPaste = stringResource(R.string.donation_card_auth_paste)
                )
                CardView(
                    label = stringResource(R.string.donation_card_fail_label),
                    card = stringResource(R.string.donation_card_fail_card),
                    toPaste = stringResource(R.string.donation_card_fail_paste)
                )
            }
        }
    }
}

@Composable
private fun CardView(label: String, card: String, toPaste: String) {
    val clipboardManager =
        LocalContext.current.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            label,
            style = MaterialTheme.typography.caption
        )
        Spacer(Modifier.weight(1f))
        Text(
            card,
            style = MaterialTheme.typography.caption
        )
        CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.high) {
            IconButton(
                onClick = {
                    clipboardManager.setPrimaryClip(
                        ClipData.newPlainText(
                            toPaste,
                            toPaste
                        )
                    )
                },
            ) {
                Icon(
                    Icons.Outlined.ContentCopy,
                    contentDescription = "copy card information",
                )
            }
        }
    }
}
