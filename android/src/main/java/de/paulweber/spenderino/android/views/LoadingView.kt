package de.paulweber.spenderino.android.views

import androidx.compose.foundation.layout.Arrangement.Absolute.spacedBy
import androidx.compose.foundation.layout.Column
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.ContentAlpha
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import de.paulweber.spenderino.android.R
import de.paulweber.spenderino.android.theme.SpenderinoTheme

@Composable
fun LoadingView() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = spacedBy(8.dp)
    ) {
        CircularProgressIndicator(color = MaterialTheme.colors.onSurface.copy(alpha = 0.5f))
        CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
            Text(
                stringResource(R.string.loading),
                style = MaterialTheme.typography.body1
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun Preview() {
    SpenderinoTheme(darkTheme = true) {
        LoadingView()
    }
}
