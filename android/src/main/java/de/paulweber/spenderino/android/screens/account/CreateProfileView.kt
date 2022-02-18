package de.paulweber.spenderino.android.screens.account

import androidx.compose.foundation.layout.Arrangement.Absolute.spacedBy
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.ContentAlpha
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import de.paulweber.spenderino.android.R
import de.paulweber.spenderino.android.views.PrimaryLoadingButton
import de.paulweber.spenderino.model.repositories.user.User
import de.paulweber.spenderino.viewmodel.AccountAction
import de.paulweber.spenderino.viewmodel.AccountState
import de.paulweber.spenderino.viewmodel.AccountViewModel

@Composable
fun CreateProfileView(state: AccountState.Registered, viewModel: AccountViewModel) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = spacedBy(16.dp)
    ) {
        val focusManager = LocalFocusManager.current
        val clearFocus = { focusManager.clearFocus() }
        CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
            Text(
                stringResource(R.string.account_profile_instructions),
                style = MaterialTheme.typography.body2,
                textAlign = TextAlign.Center
            )
            UserNameTextField(state, viewModel, clearFocus)

            Text(
                stringResource(R.string.account_profile_username_hint),
                style = MaterialTheme.typography.body2,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(16.dp)
            )
            CreateButton(state, viewModel, clearFocus)
        }
    }
}

@Composable
private fun CreateButton(
    state: AccountState.Registered,
    viewModel: AccountViewModel,
    clearFocus: () -> Unit
) {
    PrimaryLoadingButton(
        isLoading = state.isLoading,
        onClick = {
            clearFocus()
            viewModel.perform(AccountAction.CreateProfile)
        },
        enabled = !state.isLoading,
    ) { Text(stringResource(R.string.account_profile_button)) }
}

@Composable
private fun UserNameTextField(
    state: AccountState.Registered,
    viewModel: AccountViewModel,
    clearFocus: () -> Unit
) {
    OutlinedTextField(
        value = state.username,
        onValueChange = { viewModel.perform(AccountAction.ChangeUsernameText(it)) },
        label = { Text(stringResource(R.string.account_profile_username_label)) },
        placeholder = { Text(stringResource(R.string.account_profile_username_placeholder)) },
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Go),
        keyboardActions = KeyboardActions(
            onGo = {
                clearFocus()
                viewModel.perform(AccountAction.CreateProfile)
            }
        ),
        maxLines = 1,
        enabled = !state.isLoading,
    )
}

@Preview(showBackground = true)
@Composable
private fun Preview() {
    CreateProfileView(
        AccountState.Registered(User("", User.UserType.REGISTERED, null), isLoading = true),
        AccountViewModel()
    )
}
