package de.paulweber.spenderino.android.views

import androidx.compose.foundation.layout.Arrangement.Absolute.spacedBy
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import de.paulweber.spenderino.android.R

@Composable
fun ErrorView(reloadClosure: (() -> Unit)? = null) {
    val message = if (reloadClosure == null) {
        stringResource(R.string.error_message)
    } else stringResource(R.string.error_message_reload)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = spacedBy(8.dp)
    ) {
        Text(
            text = stringResource(R.string.error_title),
            style = MaterialTheme.typography.h6,
            textAlign = TextAlign.Center,
        )
        CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
            Text(
                text = message,
                style = MaterialTheme.typography.body1,
                textAlign = TextAlign.Center,
            )
        }

        reloadClosure?.let {
            Button(onClick = it, Modifier.padding(top = 12.dp)) {
                Icon(Icons.Outlined.Refresh, contentDescription = "refresh button",)
                Text(stringResource(id = R.string.error_reload))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun Without_Closure() {
    ErrorView()
}

@Preview(showBackground = true)
@Composable
private fun With_Closure() {
    ErrorView {}
}
