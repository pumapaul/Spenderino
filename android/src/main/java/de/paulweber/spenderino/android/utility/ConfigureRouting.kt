package de.paulweber.spenderino.android.utility

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.navigation.NavHostController
import de.paulweber.spenderino.android.destinations.DirectionDestination
import de.paulweber.spenderino.viewmodel.ViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@Composable
fun <Route, A, S> ConfigureRouting(
    viewModel: ViewModel<A, Route, S>,
    navigator: NavHostController,
    destination: DirectionDestination,
    handleBackButton: Boolean = true,
    routing: (route: Route) -> Unit
) {
    val lifecycleOwner = LocalLifecycleOwner.current

    val routeFlow = remember(viewModel.route, lifecycleOwner) {
        viewModel.route.flowWithLifecycle(lifecycleOwner.lifecycle, Lifecycle.State.STARTED)
    }

    LaunchedEffect(Unit) {
        routeFlow.onEach {
            it?.let(routing) ?: run {
                navigator.popBackStack(
                    destination.route,
                    inclusive = false,
                    saveState = false
                )
            }
        }.launchIn(this)
    }

    val activity = LocalContext.current as Activity
    BackHandler {
        if (handleBackButton) {
            viewModel.onBackButton()
            navigator.popBackStack(destination.route, inclusive = true)
        } else {
            activity.finish()
        }
    }
}
