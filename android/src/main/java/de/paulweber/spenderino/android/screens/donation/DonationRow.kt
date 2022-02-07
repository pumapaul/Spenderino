package de.paulweber.spenderino.android.screens.donation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun DonationRow(title: String, rightSide: @Composable () -> Unit) {
    Column {
        Divider()
        Row(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
            Text(
                title,
                style = MaterialTheme.typography.body2,
            )
            Spacer(modifier = Modifier.weight(1f))
            rightSide()
        }
    }
}
