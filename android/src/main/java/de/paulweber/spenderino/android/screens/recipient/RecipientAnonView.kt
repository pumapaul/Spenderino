package de.paulweber.spenderino.android.screens.recipient

import androidx.compose.foundation.layout.Arrangement.Absolute.spacedBy
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
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
import de.paulweber.spenderino.android.views.PrimaryButton
import de.paulweber.spenderino.viewmodel.RecipientAction
import de.paulweber.spenderino.viewmodel.RecipientViewModel

@Composable
fun RecipientAnonView(viewModel: RecipientViewModel) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = spacedBy(16.dp)
    ) {
        Text(
            stringResource(R.string.recipient_anon_title),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.h6
        )
        CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
            Text(
                stringResource(R.string.recipient_anon_label),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.body1,
            )
            Spacer(Modifier.height(16.dp))
            PrimaryButton(
                onClick = { viewModel.perform(RecipientAction.Login) },
            ) { Text(stringResource(R.string.recipient_anon_button)) }
            Spacer(Modifier.height(16.dp))
            Text(
                stringResource(R.string.recipient_anon_disclaimer),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.body2,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun Preview() {
    RecipientAnonView(
        viewModel = RecipientViewModel()
    )
}
