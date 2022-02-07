package de.paulweber.spenderino.android.screens.account

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ContentAlpha
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import de.paulweber.spenderino.android.R
import de.paulweber.spenderino.viewmodel.AccountState
import de.paulweber.spenderino.viewmodel.AccountViewModel

@Composable
fun AccountView(state: AccountState, viewModel: AccountViewModel) {
    val border = if (MaterialTheme.colors.isLight) {
        BorderStroke(0.5.dp, MaterialTheme.colors.onSurface.copy(alpha = 0.2f))
    } else null

    Surface(
        shape = RoundedCornerShape(8),
        elevation = 2.dp,
        border = border,
        color = MaterialTheme.colors.surface,
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .padding(32.dp)
        ) {
            when (state) {
                is AccountState.Anonymous -> LoginView(state, viewModel)
                is AccountState.Registered -> CreateProfileView(state, viewModel)
                is AccountState.SetupComplete -> {
                    CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                        Text(
                            stringResource(R.string.account_setup_complete),
                            style = MaterialTheme.typography.body2,
                            textAlign = TextAlign.Center,
                        )
                    }
                }
            }
        }
    }
}
