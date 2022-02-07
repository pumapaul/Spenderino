package de.paulweber.spenderino.android.utility

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import de.paulweber.spenderino.viewmodel.ViewModel

@Composable
fun <State, A, R> ObserveState(
    viewModel: ViewModel<A, R, State>,
    content: @Composable (state: State) -> Unit
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val stateFlow = remember(viewModel.state, lifecycleOwner) {
        viewModel.state.flowWithLifecycle(lifecycleOwner.lifecycle, Lifecycle.State.STARTED)
    }
    val state by stateFlow.collectAsState(viewModel.state.value)

    content(state)
}
