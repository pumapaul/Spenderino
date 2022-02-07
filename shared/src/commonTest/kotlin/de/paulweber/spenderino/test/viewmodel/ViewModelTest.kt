@file:Suppress("IllegalIdentifier")

package de.paulweber.spenderino.test.viewmodel

import de.paulweber.spenderino.viewmodel.ViewModel
import de.paulweber.spenderino.test.BaseTest
import kotlin.test.Test
import kotlin.test.assertEquals

enum class TestState {
    FIRST, SECOND, THIRD
}

sealed class TestRoute {
    object Dummy : TestRoute()
}

class TestViewModel(
    state: TestState = TestState.FIRST,
    route: TestRoute? = null,
    onBack: () -> Unit = {}
) :
    ViewModel<Unit, TestRoute, TestState>(state, route, onBack) {
    override fun perform(action: Unit) {
    }

    fun setStateFacade(state: TestState) {
        setState(state)
    }

    fun setRouteFacade(route: TestRoute?) {
        setRoute(route)
    }
}

class ViewModelTest : BaseTest() {
    @Test
    fun `ViewModel initialization sets correct State and Route`() {
        val firstState = TestState.FIRST
        val firstRoute = TestRoute.Dummy
        val firstViewModel = TestViewModel(firstState, firstRoute)

        assertEquals(firstState, firstViewModel.state.value)
        assertEquals(firstRoute, firstViewModel.route.value)

        val secondState = TestState.THIRD
        val secondRoute = null
        val secondViewModel = TestViewModel(secondState, secondRoute)

        assertEquals(secondState, secondViewModel.state.value)
        assertEquals(secondRoute, secondViewModel.route.value)
    }

    @Test
    fun `ViewModel setState pipes state correctly to wrappedState`() {
        val initialState = TestState.FIRST
        val viewModel = TestViewModel(initialState)

        assertEquals(initialState, viewModel.wrappedState.currentValue)

        val nextState = TestState.SECOND
        viewModel.setStateFacade(nextState)

        assertEquals(nextState, viewModel.wrappedState.currentValue)
    }

    @Test
    fun `ViewModel setState pipes state correctly to state`() {
        val initialState = TestState.FIRST
        val viewModel = TestViewModel(initialState)

        assertEquals(initialState, viewModel.state.value)

        val nextState = TestState.SECOND
        viewModel.setStateFacade(nextState)

        assertEquals(nextState, viewModel.state.value)
    }

    @Test
    fun `ViewModel setRoute pipes route correctly to wrappedRoute`() {
        val initialRoute = null
        val viewModel = TestViewModel(route = initialRoute)

        assertEquals(initialRoute, viewModel.wrappedRoute.currentValue)

        val nextRoute = TestRoute.Dummy
        viewModel.setRouteFacade(nextRoute)

        assertEquals(nextRoute, viewModel.wrappedRoute.currentValue)
    }

    @Test
    fun `ViewModel setRoute pipes route correctly to route`() {
        val initialRoute = null
        val viewModel = TestViewModel(route = initialRoute)

        assertEquals(initialRoute, viewModel.route.value)

        val nextRoute = TestRoute.Dummy
        viewModel.setRouteFacade(nextRoute)

        assertEquals(nextRoute, viewModel.route.value)
    }

    @Test
    fun `ViewModel routeToNull routes to null`() {
        val initialRoute = TestRoute.Dummy
        val viewModel = TestViewModel(route = initialRoute)

        assertEquals(initialRoute, viewModel.route.value)

        viewModel.routeToNull()

        assertEquals(null, viewModel.route.value)
    }

    @Test
    fun `ViewModel onBack gets called on onBackButton call`() {
        var wasCalled = false
        val viewModel = TestViewModel(onBack = { wasCalled = true })

        assertEquals(false, wasCalled)

        viewModel.onBackButton()
        assertEquals(true, wasCalled)
    }
}
