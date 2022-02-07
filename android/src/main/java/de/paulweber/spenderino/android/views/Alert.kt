package de.paulweber.spenderino.android.views

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.ramcosta.composedestinations.annotation.Destination
import de.paulweber.spenderino.android.R
import de.paulweber.spenderino.android.destinations.AlertDestination
import de.paulweber.spenderino.viewmodel.AlertAction
import de.paulweber.spenderino.viewmodel.AlertViewModel

@Destination
@Composable
fun Alert(
    navigator: NavHostController,
    viewModel: AlertViewModel
) {
    AlertDialog(
        onDismissRequest = {
            viewModel.onDestroy()
            navigator.popBackStack(AlertDestination.route, inclusive = true)
        },
        title = {
            Text(
                viewModel.title,
                style = MaterialTheme.typography.h6,
                textAlign = TextAlign.Center,
            )
        },
        text = {
            Text(
                viewModel.message,
                style = MaterialTheme.typography.body1,
                textAlign = TextAlign.Center,
            )
        },
        backgroundColor = MaterialTheme.colors.surface,
        buttons = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (viewModel.actions.isEmpty()) {
                    OkayButton(navigator = navigator, viewModel)
                } else {
                    viewModel.actions.forEach {
                        ActionButton(navigator, it, viewModel)
                    }
                }
            }
        }
    )
}

@Composable
private fun OkayButton(navigator: NavHostController, viewModel: AlertViewModel) {
    Button(
        onClick = {
            viewModel.onDestroy()
            navigator.popBackStack(AlertDestination.route, inclusive = true)
        },
        Modifier.size(100.dp, 50.dp)
    ) {
        Text(stringResource(R.string.ok))
    }
}

@Composable
private fun ActionButton(
    navigator: NavHostController,
    action: AlertAction,
    viewModel: AlertViewModel
) {
    Button(
        onClick = {
            action.block()
            viewModel.onDestroy
            navigator.popBackStack(AlertDestination.route, inclusive = true)
        },
        Modifier.size(100.dp, 50.dp)
    ) {
        Text(action.title)
    }
}
