package de.paulweber.spenderino.android.screens.preferences

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountBox
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import de.paulweber.spenderino.android.R
import de.paulweber.spenderino.android.views.SettingsIcon
import de.paulweber.spenderino.android.views.SettingsRow
import de.paulweber.spenderino.viewmodel.PreferencesAction
import de.paulweber.spenderino.viewmodel.PreferencesState
import de.paulweber.spenderino.viewmodel.PreferencesViewModel

@Composable
fun AccountRow(
    state: PreferencesState,
    viewModel: PreferencesViewModel
) {
    val foundName = when (state) {
        is PreferencesState.Anonymous -> null
        is PreferencesState.Registered -> state.user.email
        is PreferencesState.SetupComplete -> state.profile.username
        PreferencesState.Error -> null
        PreferencesState.Loading -> null
    }
    val name = foundName ?: stringResource(R.string.prefs_account_row_anonymous)

    SettingsRow(
        icon = {
            SettingsIcon(
                Icons.Outlined.AccountBox,
                "account icon",
                Color.Blue
            )
        },
        title = name,
        subtitle = stringResource(R.string.prefs_account_row_info),
        action = { viewModel.perform(PreferencesAction.ACCOUNT) }
    )
}

@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
private fun Preview() {
    AccountRow(PreferencesState.Anonymous, PreferencesViewModel())
}
