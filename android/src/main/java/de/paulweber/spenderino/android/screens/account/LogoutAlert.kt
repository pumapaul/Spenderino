package de.paulweber.spenderino.android.screens.account

import androidx.compose.foundation.layout.Arrangement.Absolute.spacedBy
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.ramcosta.composedestinations.annotation.Destination
import de.paulweber.spenderino.android.R
import de.paulweber.spenderino.android.destinations.LogoutAlertDestination
import de.paulweber.spenderino.viewmodel.AccountAction
import de.paulweber.spenderino.viewmodel.AccountViewModel

@Destination
@Composable
fun LogoutAlert(
    navigator: NavHostController,
    viewModel: AccountViewModel
) {
    AlertDialog(
        onDismissRequest = {
            viewModel.routeToNull()
            navigator.popBackStack(LogoutAlertDestination.route, inclusive = true)
        },
        title = {
            Text(
                stringResource(R.string.account_alert_logout_title),
                style = MaterialTheme.typography.h6,
                textAlign = TextAlign.Center,
            )
        },
        text = {
            Text(
                stringResource(R.string.account_alert_logout_message),
                style = MaterialTheme.typography.body1,
                textAlign = TextAlign.Center,
            )
        },
        buttons = {
            Row(horizontalArrangement = spacedBy(16.dp)) {
                Button(
                    onClick = {
                        viewModel.routeToNull()
                        navigator.popBackStack(LogoutAlertDestination.route, inclusive = true)
                        viewModel.perform(AccountAction.ConfirmLogout)
                    },
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = Color.Red
                    ),
                    modifier = Modifier
                        .size(100.dp, 50.dp)
                        .weight(1f)
                ) {
                    Text(stringResource(R.string.account_alert_logout_confirm))
                }

                Button(
                    onClick = {
                        viewModel.routeToNull()
                        navigator.popBackStack(LogoutAlertDestination.route, inclusive = true)
                    },
                    Modifier.size(100.dp, 50.dp)
                        .weight(1f)
                ) {
                    Text(stringResource(R.string.cancel))
                }
            }
        }
    )
}
