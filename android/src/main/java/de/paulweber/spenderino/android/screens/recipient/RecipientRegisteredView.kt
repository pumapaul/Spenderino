package de.paulweber.spenderino.android.screens.recipient

import androidx.compose.foundation.layout.Arrangement.Absolute.spacedBy
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.Button
import androidx.compose.material.ContentAlpha
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import de.paulweber.spenderino.android.R
import de.paulweber.spenderino.viewmodel.RecipientAction
import de.paulweber.spenderino.viewmodel.RecipientViewModel

@Composable
fun RecipientRegisteredView(viewModel: RecipientViewModel) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = spacedBy(16.dp)
    ) {
        Text(
            stringResource(R.string.recipient_registered_title),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.h6
        )
        CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
            Text(
                stringResource(R.string.recipient_registered_label),
                style = MaterialTheme.typography.body1,
                textAlign = TextAlign.Center,
            )
        }

        Spacer(Modifier.height(16.dp))

        Button(
            onClick = { viewModel.perform(RecipientAction.CreateProfile) },
            Modifier.size(200.dp, 50.dp)
        ) {
            Text(stringResource(R.string.recipient_registered_button))
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun Preview() {
    RecipientRegisteredView(
        viewModel = RecipientViewModel()
    )
}
