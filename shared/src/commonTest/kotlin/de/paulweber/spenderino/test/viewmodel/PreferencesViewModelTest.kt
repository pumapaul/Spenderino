@file:Suppress("IllegalIdentifier")

package de.paulweber.spenderino.test.viewmodel

import de.paulweber.spenderino.model.repositories.user.Profile
import de.paulweber.spenderino.model.repositories.user.ProfileState
import de.paulweber.spenderino.model.repositories.user.User
import de.paulweber.spenderino.model.repositories.user.UserRepository
import de.paulweber.spenderino.model.repositories.user.UserState
import de.paulweber.spenderino.test.BaseTest
import de.paulweber.spenderino.viewmodel.AccountState
import de.paulweber.spenderino.viewmodel.PreferencesAction
import de.paulweber.spenderino.viewmodel.PreferencesRoute
import de.paulweber.spenderino.viewmodel.PreferencesState
import de.paulweber.spenderino.viewmodel.PreferencesViewModel
import io.mockk.every
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.runBlocking
import org.koin.test.mock.declareMock
import kotlin.test.Test
import kotlin.test.assertEquals

class PreferencesViewModelTest : BaseTest() {
    private val user = User("", User.UserType.ANONYMOUS, null)
    private val profile = Profile("", "", 100L)

    @Test
    fun `userState loading results in loading state`() = runBlocking {
        declareMock<UserRepository> {
            every { userState } returns MutableStateFlow(UserState.Loading)
        }

        val viewModel = PreferencesViewModel()
        delay(200)

        assertEquals(PreferencesState.Loading, viewModel.state.value)
    }

    @Test
    fun `userState anon results in anon state`() = runBlocking {
        declareMock<UserRepository> {
            every { userState } returns MutableStateFlow(UserState.Anonymous(user))
        }

        val viewModel = PreferencesViewModel()
        delay(200)

        assertEquals(PreferencesState.Anonymous, viewModel.state.value)
    }

    @Test
    fun `userState error results in error state`() = runBlocking {
        declareMock<UserRepository> {
            every { userState } returns MutableStateFlow(UserState.Error)
        }

        val viewModel = PreferencesViewModel()
        delay(200)

        assertEquals(PreferencesState.Error, viewModel.state.value)
    }

    @Test
    fun `userState registered without profile results in registered state`() = runBlocking {
        declareMock<UserRepository> {
            every { userState } returns MutableStateFlow(
                UserState.Registered(
                    user,
                    ProfileState.None
                )
            )
        }

        val viewModel = PreferencesViewModel()
        delay(200)

        assertEquals(PreferencesState.Registered(user), viewModel.state.value)
    }

    @Test
    fun `userState registered with profile error results in error state`() = runBlocking {
        declareMock<UserRepository> {
            every { userState } returns MutableStateFlow(
                UserState.Registered(
                    user,
                    ProfileState.Error
                )
            )
        }

        val viewModel = PreferencesViewModel()
        delay(200)

        assertEquals(PreferencesState.Error, viewModel.state.value)
    }

    @Test
    fun `userState registered with loading profile results in loading state`() = runBlocking {
        declareMock<UserRepository> {
            every { userState } returns MutableStateFlow(
                UserState.Registered(
                    user,
                    ProfileState.Loading
                )
            )
        }

        val viewModel = PreferencesViewModel()
        delay(200)

        assertEquals(PreferencesState.Loading, viewModel.state.value)
    }

    @Test
    fun `userState registered with profile results in setup complete state`() = runBlocking {
        declareMock<UserRepository> {
            every { userState } returns MutableStateFlow(
                UserState.Registered(
                    user,
                    ProfileState.Present(profile)
                )
            )
        }

        val viewModel = PreferencesViewModel()
        delay(200)

        assertEquals(PreferencesState.SetupComplete(user, profile), viewModel.state.value)
    }

    @Test
    fun `account action on SetupComplete routes to AccountViewModel with SetupComplete`() =
        runBlocking {
            declareMock<UserRepository> {
                every { userState } returns MutableStateFlow(
                    UserState.Registered(
                        user,
                        ProfileState.Present(profile)
                    )
                )
            }
            val viewModel = PreferencesViewModel()
            delay(200)
            viewModel.perform(PreferencesAction.ACCOUNT)

            assertEquals(true, viewModel.route.value is PreferencesRoute.Account)

            val accountViewModel = (viewModel.route.value as PreferencesRoute.Account).viewModel
            val expectedState = AccountState.SetupComplete(user, profile)
            assertEquals(expectedState, accountViewModel.state.value)

            accountViewModel.onBackButton()
            assertEquals(null, viewModel.route.value)
        }

    @Test
    fun `account action on Anonymous routes to AccountViewModel with Anonymous`() = runBlocking {
        declareMock<UserRepository> {
            every { userState } returns MutableStateFlow(UserState.Anonymous(user))
        }
        val viewModel = PreferencesViewModel()
        delay(200)
        viewModel.perform(PreferencesAction.ACCOUNT)

        assertEquals(true, viewModel.route.value is PreferencesRoute.Account)

        val accountViewModel = (viewModel.route.value as PreferencesRoute.Account).viewModel
        val expectedState = AccountState.Anonymous()
        assertEquals(expectedState, accountViewModel.state.value)

        accountViewModel.onBackButton()
        assertEquals(null, viewModel.route.value)
    }

    @Test
    fun `account action on Registered routes to AccountViewModel with registered`() = runBlocking {
        declareMock<UserRepository> {
            every { userState } returns MutableStateFlow(
                UserState.Registered(
                    user,
                    ProfileState.None
                )
            )
        }
        val viewModel = PreferencesViewModel()
        delay(200)
        viewModel.perform(PreferencesAction.ACCOUNT)

        assertEquals(true, viewModel.route.value is PreferencesRoute.Account)

        val accountViewModel = (viewModel.route.value as PreferencesRoute.Account).viewModel
        val expectedState = AccountState.Registered(user)
        assertEquals(expectedState, accountViewModel.state.value)

        accountViewModel.onBackButton()
        assertEquals(null, viewModel.route.value)
    }

    @Test
    fun `account action on Loading routes to AccountViewModel with loading`() = runBlocking {
        declareMock<UserRepository> {
            every { userState } coAnswers {
                delay(200)
                MutableStateFlow(UserState.Loading)
            }
        }
        val viewModel = PreferencesViewModel()
        delay(300)
        viewModel.perform(PreferencesAction.ACCOUNT)

        assertEquals(true, viewModel.route.value is PreferencesRoute.Account)

        val accountViewModel = (viewModel.route.value as PreferencesRoute.Account).viewModel
        val expectedState = AccountState.Anonymous()
        assertEquals(expectedState, accountViewModel.state.value)

        accountViewModel.onBackButton()
        assertEquals(null, viewModel.route.value)
    }

    @Test
    fun `account action on Error routes to AccountViewModel with error`() = runBlocking {
        declareMock<UserRepository> {
            every { userState } coAnswers {
                delay(200)
                MutableStateFlow(UserState.Error)
            }
        }
        val viewModel = PreferencesViewModel()
        delay(300)
        viewModel.perform(PreferencesAction.ACCOUNT)

        assertEquals(true, viewModel.route.value is PreferencesRoute.Account)

        val accountViewModel = (viewModel.route.value as PreferencesRoute.Account).viewModel
        val expectedState = AccountState.Anonymous()
        assertEquals(expectedState, accountViewModel.state.value)

        accountViewModel.onBackButton()
        assertEquals(null, viewModel.route.value)
    }

    @Test
    fun `transactions action routes to TransactionsViewModel`() {
        declareMock<UserRepository> {
            every { userState } returns MutableStateFlow(UserState.Loading)
        }
        val viewModel = PreferencesViewModel()

        viewModel.perform(PreferencesAction.TRANSACTIONS)
        assertEquals(true, viewModel.route.value is PreferencesRoute.Transactions)
        val transactionsViewModel = (viewModel.route.value as PreferencesRoute.Transactions)
            .viewModel

        transactionsViewModel.onBackButton()
        assertEquals(null, viewModel.route.value)
    }
}
