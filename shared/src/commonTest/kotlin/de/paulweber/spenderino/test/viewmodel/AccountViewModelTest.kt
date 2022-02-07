@file:Suppress("IllegalIdentifier")

package de.paulweber.spenderino.test.viewmodel

import de.paulweber.spenderino.model.repositories.user.ProfileState
import de.paulweber.spenderino.model.repositories.user.User
import de.paulweber.spenderino.model.repositories.user.UserRepository
import de.paulweber.spenderino.model.repositories.user.UserState
import de.paulweber.spenderino.test.BaseTest
import de.paulweber.spenderino.viewmodel.AccountAction
import de.paulweber.spenderino.viewmodel.AccountRoute
import de.paulweber.spenderino.viewmodel.AccountState
import de.paulweber.spenderino.viewmodel.AccountViewModel
import io.ktor.client.features.ClientRequestException
import io.ktor.http.HttpStatusCode
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.flow.collect
import org.koin.test.mock.declareMock
import kotlin.test.Test
import kotlin.test.assertEquals

class AccountViewModelTest : BaseTest() {
    private val user = User("", User.UserType.ANONYMOUS, null)

    @Test
    fun `userState loading results in loading state`() = runBlocking {
        declareMock<UserRepository> {
            every { userState } returns MutableStateFlow(UserState.Loading)
        }

        val viewModel = AccountViewModel()
        delay(200)

        assertEquals(AccountState.Loading, viewModel.state.value)
    }

    @Test
    fun `userState error results in error state`() = runBlocking {
        declareMock<UserRepository> {
            every { userState } returns MutableStateFlow(UserState.Error)
        }

        val viewModel = AccountViewModel()
        delay(200)

        assertEquals(AccountState.Error, viewModel.state.value)
    }

    @Test
    fun `ChangeEmailText sets email state field`() = runBlocking {
        declareMock<UserRepository> {
            every { userState } returns MutableStateFlow(UserState.Anonymous(user))
        }

        val viewModel = AccountViewModel()
        delay(200)
        val expected = "yeah"

        viewModel.perform(AccountAction.ChangeEmailText(expected))
        val result = (viewModel.state.value as AccountState.Anonymous).email

        assertEquals(expected, result)
    }

    @Test
    fun `ChangePasswordText sets password state field`() = runBlocking {
        declareMock<UserRepository> {
            every { userState } returns MutableStateFlow(UserState.Anonymous(user))
        }

        val viewModel = AccountViewModel()
        delay(200)
        val expected = "yeah"

        viewModel.perform(AccountAction.ChangePasswordText(expected))
        val result = (viewModel.state.value as AccountState.Anonymous).password

        assertEquals(expected, result)
    }

    @Test
    fun `login action uses email and password fields`() = runBlocking {
        val expectedEmail = "email"
        val expectedPassword = "password"
        var emailResult = ""
        var passwordResult = ""
        declareMock<UserRepository> {
            every { userState } returns MutableStateFlow(UserState.Anonymous(user))
            coEvery { login(any(), any()) } answers {
                emailResult = it.invocation.args[0] as String
                passwordResult = it.invocation.args[1] as String

                Result.failure(Exception())
            }
        }

        val viewModel = AccountViewModel()
        delay(200)
        viewModel.perform(AccountAction.ChangeEmailText(expectedEmail))
        viewModel.perform(AccountAction.ChangePasswordText(expectedPassword))
        viewModel.perform(AccountAction.Login)
        delay(200)

        assertEquals(expectedEmail, emailResult)
        assertEquals(expectedPassword, passwordResult)
    }

    @Test
    fun `login action sets loading state correctly on success`() = runBlocking {
        declareMock<UserRepository> {
            every { userState } returns MutableStateFlow(UserState.Anonymous(user))
            coEvery { login(any(), any()) } returns Result.success(user)
        }

        val viewModel = AccountViewModel()
        delay(200)
        val results = mutableListOf<AccountState>()
        scope.launch { viewModel.state.collect { results.add(it) } }
        viewModel.perform(AccountAction.Login)
        delay(200)

        results.removeFirst()
        assertEquals(true, (results[0] as AccountState.Anonymous).isLoginLoading)
        assertEquals(false, (results[1] as AccountState.Anonymous).isLoginLoading)
    }

    @Test
    fun `login action sets loading state correctly on failure`() = runBlocking {
        declareMock<UserRepository> {
            every { userState } returns MutableStateFlow(UserState.Anonymous(user))
            coEvery { login(any(), any()) } returns Result.failure(Exception())
        }

        val viewModel = AccountViewModel()
        delay(200)
        val results = mutableListOf<AccountState>()
        scope.launch { viewModel.state.collect { results.add(it) } }
        viewModel.perform(AccountAction.Login)
        delay(200)

        results.removeFirst()
        assertEquals(true, (results[0] as AccountState.Anonymous).isLoginLoading)
        assertEquals(false, (results[1] as AccountState.Anonymous).isLoginLoading)
    }

    @Test
    fun `login action sets wrongCredentialsAlert route on 401`() = runBlocking {
        declareMock<UserRepository> {
            every { userState } returns MutableStateFlow(UserState.Anonymous(user))
            val exception = createMockedClientException(HttpStatusCode.Unauthorized)
            coEvery { login(any(), any()) } returns Result.failure(exception)
        }

        val viewModel = AccountViewModel()
        delay(200)
        viewModel.perform(AccountAction.Login)
        delay(500)

        assertEquals(true, viewModel.route.value is AccountRoute.Alert)
        val alertViewModel = (viewModel.route.value as AccountRoute.Alert).alert

        assertEquals("account_alert_credentials_title", alertViewModel.title)
    }

    @Test
    fun `login action sets relevant alert route on any other error`() = runBlocking {
        declareMock<UserRepository> {
            every { userState } returns MutableStateFlow(UserState.Anonymous(user))
            val exception = createMockedClientException(HttpStatusCode.BadRequest)
            coEvery { login(any(), any()) } returns Result.failure(exception)
        }

        val viewModel = AccountViewModel()
        delay(200)
        viewModel.perform(AccountAction.Login)
        delay(500)

        assertEquals(true, viewModel.route.value is AccountRoute.Alert)
        val alertViewModel = (viewModel.route.value as AccountRoute.Alert).alert

        assertEquals("alert_client_request_title+400 Bad Request", alertViewModel.title)
    }

    @Test
    fun `register action uses email and password fields`() = runBlocking {
        val expectedEmail = "email"
        val expectedPassword = "password"
        var emailResult = ""
        var passwordResult = ""
        declareMock<UserRepository> {
            every { userState } returns MutableStateFlow(UserState.Anonymous(user))
            coEvery { register(any(), any()) } answers {
                emailResult = it.invocation.args[0] as String
                passwordResult = it.invocation.args[1] as String

                Result.failure(Exception())
            }
        }

        val viewModel = AccountViewModel()
        delay(200)
        viewModel.perform(AccountAction.ChangeEmailText(expectedEmail))
        viewModel.perform(AccountAction.ChangePasswordText(expectedPassword))
        viewModel.perform(AccountAction.Register)
        delay(200)

        assertEquals(expectedEmail, emailResult)
        assertEquals(expectedPassword, passwordResult)
    }

    @Test
    fun `register action sets loading state correctly on success`() = runBlocking {
        declareMock<UserRepository> {
            every { userState } returns MutableStateFlow(UserState.Anonymous(user))
            coEvery { register(any(), any()) } returns Result.success(user)
        }

        val viewModel = AccountViewModel()
        delay(200)
        val results = mutableListOf<AccountState>()
        scope.launch { viewModel.state.collect { results.add(it) } }
        viewModel.perform(AccountAction.Register)
        delay(200)

        results.removeFirst()
        assertEquals(true, (results[0] as AccountState.Anonymous).isRegisterLoading)
        assertEquals(false, (results[1] as AccountState.Anonymous).isRegisterLoading)
    }

    @Test
    fun `register action sets loading state correctly on failure`() = runBlocking {
        declareMock<UserRepository> {
            every { userState } returns MutableStateFlow(UserState.Anonymous(user))
            coEvery { register(any(), any()) } returns Result.failure(Exception())
        }

        val viewModel = AccountViewModel()
        delay(200)
        val results = mutableListOf<AccountState>()
        scope.launch { viewModel.state.collect { results.add(it) } }
        viewModel.perform(AccountAction.Register)
        delay(200)

        results.removeFirst()
        assertEquals(true, (results[0] as AccountState.Anonymous).isRegisterLoading)
        assertEquals(false, (results[1] as AccountState.Anonymous).isRegisterLoading)
    }

    @Test
    fun `register action sets duplicate user alert route on Conflict`() = runBlocking {
        declareMock<UserRepository> {
            every { userState } returns MutableStateFlow(UserState.Anonymous(user))
            val exception = createMockedClientException(HttpStatusCode.Conflict)
            coEvery { register(any(), any()) } returns Result.failure(exception)
        }

        val viewModel = AccountViewModel()
        delay(200)
        viewModel.perform(AccountAction.Register)
        delay(500)

        assertEquals(true, viewModel.route.value is AccountRoute.Alert)

        val alertViewModel = (viewModel.route.value as AccountRoute.Alert).alert
        assertEquals("account_alert_duplicate_title", alertViewModel.title)
    }

    @Test
    fun `register action resets password field and sets passwordRulesAlert route on BadRequest`() =
        runBlocking {
            declareMock<UserRepository> {
                every { userState } returns MutableStateFlow(UserState.Anonymous(user))
                val exception = createMockedClientException(HttpStatusCode.BadRequest)
                coEvery { register(any(), any()) } returns Result.failure(exception)
            }

            val viewModel = AccountViewModel()
            delay(200)
            viewModel.perform(AccountAction.ChangePasswordText("SOMETHING"))
            viewModel.perform(AccountAction.Register)
            delay(500)
            val result = (viewModel.state.value as AccountState.Anonymous).password

            assertEquals("", result)

            assertEquals(true, viewModel.route.value is AccountRoute.Alert)

            val alertViewModel = (viewModel.route.value as AccountRoute.Alert).alert
            assertEquals("account_alert_rules_title", alertViewModel.title)
        }

    @Test
    fun `register action sets relevant alert route on any other error`() = runBlocking {
        declareMock<UserRepository> {
            every { userState } returns MutableStateFlow(UserState.Anonymous(user))
            val exception = createMockedClientException(HttpStatusCode.NotFound)
            coEvery { register(any(), any()) } returns Result.failure(exception)
        }

        val viewModel = AccountViewModel()
        delay(200)
        viewModel.perform(AccountAction.Register)
        delay(500)

        assertEquals(true, viewModel.route.value is AccountRoute.Alert)
        val alertViewModel = (viewModel.route.value as AccountRoute.Alert).alert

        assertEquals("alert_client_request_title+404 Not Found", alertViewModel.title)
    }

    @Test
    fun `ChangeUsernameText sets username state field`() = runBlocking {
        declareMock<UserRepository> {
            every { userState } returns MutableStateFlow(
                UserState.Registered(
                    user,
                    ProfileState.None
                )
            )
        }

        val viewModel = AccountViewModel()
        delay(200)
        val expected = "yeah"

        viewModel.perform(AccountAction.ChangeUsernameText(expected))
        val result = (viewModel.state.value as AccountState.Registered).username

        assertEquals(expected, result)
    }

    @Test
    fun `CreateProfile action uses username state field`() = runBlocking {
        val expected = "username"
        var result = ""
        declareMock<UserRepository> {
            every { userState } returns MutableStateFlow(
                UserState.Registered(
                    user,
                    ProfileState.None
                )
            )
            coEvery { createProfile(any()) } answers {
                result = it.invocation.args[0] as String

                Result.failure(Exception())
            }
        }

        val viewModel = AccountViewModel()
        delay(200)
        viewModel.perform(AccountAction.ChangeUsernameText(expected))
        viewModel.perform(AccountAction.CreateProfile)
        delay(200)

        assertEquals(expected, result)
    }

    @Test
    fun `createProfile action sets loading states on failure`() = runBlocking {
        declareMock<UserRepository> {
            every { userState } returns MutableStateFlow(
                UserState.Registered(
                    user,
                    ProfileState.None
                )
            )
            coEvery { createProfile(any()) } returns Result.failure(Exception())
        }

        val viewModel = AccountViewModel()
        delay(200)
        val results = mutableListOf<AccountState>()
        scope.launch { viewModel.state.collect { results.add(it) } }
        viewModel.perform(AccountAction.CreateProfile)
        delay(200)

        results.removeFirst()
        assertEquals(true, (results[0] as AccountState.Registered).isLoading)
        assertEquals(false, (results[1] as AccountState.Registered).isLoading)
    }

    @Test
    fun `CreateProfile action calls onSetupComplete on success`() = runBlocking {
        var result = false

        declareMock<UserRepository> {
            every { userState } returns MutableStateFlow(
                UserState.Registered(
                    user,
                    ProfileState.None
                )
            )
            coEvery { createProfile(any()) } returns Result.success(mockk())
        }
        val viewModel = AccountViewModel(onSetupComplete = { result = true })
        delay(200)
        viewModel.perform(AccountAction.CreateProfile)
        delay(200)

        assertEquals(true, result)
    }

    @Test
    fun `CreateProfile action sets badNameAlert route on BadRequest`() = runBlocking {
        declareMock<UserRepository> {
            every { userState } returns MutableStateFlow(
                UserState.Registered(
                    user,
                    ProfileState.None
                )
            )
            val exception = createMockedClientException(HttpStatusCode.BadRequest)
            coEvery { createProfile(any()) } returns Result.failure(exception)
        }

        val viewModel = AccountViewModel()
        delay(500)
        viewModel.perform(AccountAction.CreateProfile)
        delay(500)

        assertEquals(true, viewModel.route.value is AccountRoute.Alert)

        val alertViewModel = (viewModel.route.value as AccountRoute.Alert).alert
        assertEquals("account_alert_username_title", alertViewModel.title)
    }

    @Test
    fun `CreateProfile action sets relevant alert route on error`() = runBlocking {
        declareMock<UserRepository> {
            every { userState } returns MutableStateFlow(
                UserState.Registered(
                    user,
                    ProfileState.None
                )
            )
            val exception = mockk<ClientRequestException>(relaxed = true) {
                every { response } returns mockk() { every { status } returns HttpStatusCode.NotFound }
            }
            coEvery { createProfile(any()) } returns Result.failure(exception)
        }

        val viewModel = AccountViewModel()
        delay(200)
        viewModel.perform(AccountAction.CreateProfile)
        delay(200)

        assertEquals(true, viewModel.route.value is AccountRoute.Alert)

        val alertViewModel = (viewModel.route.value as AccountRoute.Alert).alert
        assertEquals("alert_client_request_title+404 Not Found", alertViewModel.title)
    }

    @Test
    fun `ConfirmLogout action calls userRepo logout`() = runBlocking {
        val repoMock = declareMock<UserRepository> {
            every { userState } returns MutableStateFlow(
                UserState.Registered(
                    user,
                    ProfileState.None
                )
            )
            coEvery { logout() } returns Result.success(Unit)
        }

        val viewModel = AccountViewModel()
        delay(200)
        viewModel.perform(AccountAction.ConfirmLogout)

        coVerify(exactly = 1) { repoMock.logout() }
        confirmVerified()
    }

    @Test
    fun `Logout action sets logout alert route`() = runBlocking {
        declareMock<UserRepository> {
            every { userState } returns MutableStateFlow(
                UserState.Registered(
                    user,
                    ProfileState.None
                )
            )
        }

        val viewModel = AccountViewModel()
        delay(200)
        viewModel.perform(AccountAction.Logout)

        assertEquals(true, viewModel.route.value is AccountRoute.LogoutAlert)

        val alertViewModel = (viewModel.route.value as AccountRoute.LogoutAlert).alert
        assertEquals("account_alert_logout_title", alertViewModel.title)
    }
}
