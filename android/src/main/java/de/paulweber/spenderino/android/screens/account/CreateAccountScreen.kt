package de.paulweber.spenderino.android.screens.account

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.navigateTo
import de.paulweber.spenderino.android.R
import de.paulweber.spenderino.android.destinations.AlertDestination
import de.paulweber.spenderino.android.destinations.CreateAccountScreenDestination
import de.paulweber.spenderino.android.utility.ConfigureRouting
import de.paulweber.spenderino.android.utility.Screen
import de.paulweber.spenderino.viewmodel.AccountRoute
import de.paulweber.spenderino.viewmodel.AccountState
import de.paulweber.spenderino.viewmodel.AccountViewModel
import de.paulweber.spenderino.viewmodel.AlertViewModel

@Destination
@Composable
fun CreateAccountScreen(
    navigator: NavHostController,
    viewModel: AccountViewModel,
    alertRoute: (AlertViewModel) -> Unit
) {
    ConfigureRouting(
        viewModel = viewModel,
        navigator = navigator,
        destination = CreateAccountScreenDestination,
        routing = {
            when (it) {
                is AccountRoute.Alert -> {
                    alertRoute(it.alert)
                    navigator.navigateTo(AlertDestination)
                }
                else -> Unit
            }
        },
    )

    Screen(
        title = stringResource(R.string.account_title),
        viewModel = viewModel,
        hasBackButton = true,
        destination = CreateAccountScreenDestination,
        navigator = navigator
    ) {
        // Box(
        //     contentAlignment = Alignment.Center,
        // ) {

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
                when (it) {
                    is AccountState.Anonymous -> LoginView(it, viewModel)
                    is AccountState.Registered -> CreateProfileView(it, viewModel)
                    is AccountState.SetupComplete -> {
                        viewModel.onSetupComplete()
                        navigator.popBackStack(
                            CreateAccountScreenDestination.route,
                            inclusive = true
                        )
                    }
                }
            }
        }
        // }
    }
}
