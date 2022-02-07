package de.paulweber.spenderino.android.screens.recipient

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import de.paulweber.spenderino.android.R
import de.paulweber.spenderino.utility.toEuroString

@Composable
fun CurrentBalanceView(balance: Long) {
    val border = if (MaterialTheme.colors.isLight) {
        BorderStroke(0.5.dp, MaterialTheme.colors.onSurface.copy(alpha = 0.2f))
    } else null

    Surface(
        elevation = 2.dp,
        color = MaterialTheme.colors.surface,
        shape = RoundedCornerShape(8),
        border = border,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .padding(32.dp)
        ) {
            Text(
                stringResource(R.string.balance_current_headline),
                style = MaterialTheme.typography.h5,
                textAlign = TextAlign.Center
            )
            Text(
                balance.toEuroString(),
                style = MaterialTheme.typography.h1,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
private fun Preview() {
    CurrentBalanceView(balance = 200)
}
