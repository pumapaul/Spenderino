package de.paulweber.spenderino.android.screens.account

import androidx.compose.foundation.layout.Arrangement.Absolute.spacedBy
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import de.paulweber.spenderino.android.R
import de.paulweber.spenderino.android.views.PrimaryLoadingButton
import de.paulweber.spenderino.android.views.SecondaryLoadingButton
import de.paulweber.spenderino.viewmodel.AccountAction
import de.paulweber.spenderino.viewmodel.AccountState
import de.paulweber.spenderino.viewmodel.AccountViewModel

@Composable
fun LoginView(state: AccountState.Anonymous, viewModel: AccountViewModel) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = spacedBy(12.dp)
    ) {
        val focusManager = LocalFocusManager.current
        val clearFocus = { focusManager.clearFocus() }
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = spacedBy(8.dp)
        ) {
            EmailTextField(
                state,
                viewModel,
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                )
            )
            PasswordTextField(
                state,
                viewModel,
                keyboardActions = KeyboardActions(
                    onGo = {
                        clearFocus()
                        viewModel.perform(AccountAction.Login)
                    }
                )
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        LoginButton(state, viewModel, clearFocus)

        Text(
            stringResource(R.string.or),
            style = MaterialTheme.typography.body2,
        )

        RegisterButton(state, viewModel, clearFocus)
    }
}

@Composable
private fun LoginButton(
    state: AccountState.Anonymous,
    viewModel: AccountViewModel,
    clearFocus: () -> Unit
) {
    PrimaryLoadingButton(
        isLoading = state.isLoginLoading,
        onClick = {
            clearFocus()
            viewModel.perform(AccountAction.Login)
        },
        enabled = !(state.isLoginLoading || state.isRegisterLoading),
    ) { Text(stringResource(R.string.account_button_login)) }
}

@Composable
private fun RegisterButton(
    state: AccountState.Anonymous,
    viewModel: AccountViewModel,
    clearFocus: () -> Unit
) {
    SecondaryLoadingButton(
        enabled = !(state.isLoginLoading || state.isRegisterLoading),
        isLoading = state.isRegisterLoading,
        onClick = {
            clearFocus()
            viewModel.perform(AccountAction.Register)
        }
    ) { Text(stringResource(R.string.account_button_register)) }
}

@Composable
private fun EmailTextField(
    state: AccountState.Anonymous,
    viewModel: AccountViewModel,
    keyboardActions: KeyboardActions
) {
    OutlinedTextField(
        value = state.email,
        onValueChange = { viewModel.perform(AccountAction.ChangeEmailText(it)) },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Email,
            imeAction = ImeAction.Next
        ),
        keyboardActions = keyboardActions,
        label = { Text(stringResource(R.string.account_email_label)) },
        placeholder = { Text(stringResource(R.string.account_email_hint)) },
        enabled = !(state.isLoginLoading || state.isRegisterLoading),
        maxLines = 1,
    )
}

@Composable
private fun PasswordTextField(
    state: AccountState.Anonymous,
    viewModel: AccountViewModel,
    keyboardActions: KeyboardActions
) {
    OutlinedTextField(
        value = state.password,
        onValueChange = { viewModel.perform(AccountAction.ChangePasswordText(it)) },
        visualTransformation = PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Password,
            imeAction = ImeAction.Go
        ),
        keyboardActions = keyboardActions,
        label = { Text(stringResource(R.string.account_password_label)) },
        placeholder = { Text(stringResource(R.string.account_password_hint)) },
        enabled = !(state.isLoginLoading || state.isRegisterLoading),
        maxLines = 1,
    )
}

@Preview(showBackground = true)
@Composable
private fun Preview() {
    LoginView(
        state = AccountState.Anonymous(),
        viewModel = AccountViewModel()
    )
}
