package de.paulweber.spenderino.android.screens.payout

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import de.paulweber.spenderino.android.views.QRCodeView
import de.paulweber.spenderino.viewmodel.PayoutState

@Composable
fun PayoutView(state: PayoutState.QRCode) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Absolute.spacedBy(12.dp)
    ) {
        Text(
            state.text,
            style = MaterialTheme.typography.h6,
            textAlign = TextAlign.Center
        )
        QRCodeView(state.code)
    }
}
