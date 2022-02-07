package de.paulweber.spenderino.android.screens.donation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.navigateTo
import de.paulweber.spenderino.android.R
import de.paulweber.spenderino.android.destinations.AlertDestination
import de.paulweber.spenderino.android.destinations.DonationScreenDestination
import de.paulweber.spenderino.android.utility.ConfigureRouting
import de.paulweber.spenderino.android.utility.Screen
import de.paulweber.spenderino.android.views.ErrorView
import de.paulweber.spenderino.android.views.LoadingView
import de.paulweber.spenderino.viewmodel.AlertViewModel
import de.paulweber.spenderino.viewmodel.DonationAction
import de.paulweber.spenderino.viewmodel.DonationRoute
import de.paulweber.spenderino.viewmodel.DonationState
import de.paulweber.spenderino.viewmodel.DonationViewModel

@Destination
@Composable
fun DonationScreen(
    navigator: NavHostController,
    stripeViewModel: StripeViewModel,
    viewModel: DonationViewModel,
    alertRoute: (AlertViewModel) -> Unit
) {
    ConfigureRouting(
        viewModel = viewModel,
        navigator = navigator,
        destination = DonationScreenDestination
    ) {
        when (it) {
            is DonationRoute.Alert -> {
                alertRoute(it.alert)
                navigator.navigateTo(AlertDestination)
            }
        }
    }
    Screen(
        title = stringResource(R.string.donation_title),
        viewModel = viewModel,
        hasBackButton = true,
        destination = DonationScreenDestination,
        navigator = navigator
    ) {
        when (it) {
            is DonationState.Loading -> Centered { LoadingView() }
            is DonationState.Base -> DonationView(it, viewModel, stripeViewModel)
            DonationState.Error.NetworkError -> Centered {
                ErrorView {
                    viewModel.perform(
                        DonationAction.Reload
                    )
                }
            }
            DonationState.Error.UnknownCode -> Centered { UnknownCode() }
            is DonationState.Success -> Centered { DonationSuccessView(it.message) }
        }
    }
}

@Composable
private fun Centered(content: @Composable () -> Unit) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp)
    ) {
        content()
    }
}

@Composable
private fun UnknownCode() {
    CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
        Text(
            stringResource(R.string.donation_unknown_code),
            style = MaterialTheme.typography.h6,
            textAlign = TextAlign.Center,
        )
    }
}
