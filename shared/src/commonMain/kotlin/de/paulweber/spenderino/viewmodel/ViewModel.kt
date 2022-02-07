package de.paulweber.spenderino.viewmodel

import co.touchlab.kermit.Logger
import de.paulweber.spenderino.utility.CFlow
import de.paulweber.spenderino.utility.wrap
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.koin.core.component.KoinComponent

interface AlertRoute {
    val alert: AlertViewModel
}

interface Navigating {
    val viewModel: HasOnBack
}
interface HasOnBack {
    fun onBackButton()
}
interface ViewModeling<Action, Route, State> : HasOnBack {
    val wrappedState: CFlow<State>
    val wrappedRoute: CFlow<Route?>
    val state: StateFlow<State>
    val route: StateFlow<Route?>
    fun perform(action: Action)
    fun routeToNull()
}

abstract class ViewModel<Action, Route, State>(
    state: State,
    route: Route? = null,
    protected val onBack: () -> Unit = {}
) : ViewModeling<Action, Route, State>, KoinComponent {
    override val wrappedState: CFlow<State>
        get() = mutableState.wrap()
    override val wrappedRoute: CFlow<Route?>
        get() = mutableRoute.wrap()
    override val state: StateFlow<State>
        get() = mutableState
    override val route: StateFlow<Route?>
        get() = mutableRoute

    private val mutableState = MutableStateFlow(state)
    private val mutableRoute = MutableStateFlow(route)

    protected val scope = CoroutineScope(Dispatchers.Default)

    protected fun setState(state: State) {
        if (state != this.state.value) {
            Logger.d("${this::class.simpleName}-STATE: $state")
            mutableState.value = state
        }
    }

    protected fun setRoute(route: Route?) {
        if (route != this.route.value) {
            Logger.d("${this::class.simpleName}-ROUTE: $route")
            mutableRoute.value = route
        }
    }

    override fun onBackButton() {
        Logger.d("${this::class.simpleName} onBack()")
        onBack()
    }

    override fun routeToNull() {
        Logger.d("${this::class.simpleName} routeToNull()")
        setRoute(null)
    }
}
