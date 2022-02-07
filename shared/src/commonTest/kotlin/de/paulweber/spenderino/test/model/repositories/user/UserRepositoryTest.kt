@file:Suppress("IllegalIdentifier")

package de.paulweber.spenderino.test.model.repositories.user

import de.paulweber.spenderino.model.networking.TokenInfo
import de.paulweber.spenderino.model.networking.TokenStoring
import de.paulweber.spenderino.model.repositories.user.Profile
import de.paulweber.spenderino.model.repositories.user.ProfileRemoteSource
import de.paulweber.spenderino.model.repositories.user.ProfileState
import de.paulweber.spenderino.model.repositories.user.User
import de.paulweber.spenderino.model.repositories.user.UserRemoteSource
import de.paulweber.spenderino.model.repositories.user.UserRepositoryImpl
import de.paulweber.spenderino.model.repositories.user.UserState
import de.paulweber.spenderino.test.BaseTest
import io.ktor.client.HttpClient
import io.ktor.http.HttpStatusCode
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.spyk
import io.mockk.verify
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.flow.collect
import org.koin.test.mock.declareMock
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals

class UserRepositoryTest : BaseTest() {
    private val anonToken = TokenInfo(
        identifier = "anonId",
        userType = User.UserType.ANONYMOUS,
        email = null,
        accessToken = "accessToken",
        refreshToken = null
    )
    private val registeredToken = TokenInfo(
        identifier = "registered",
        userType = User.UserType.REGISTERED,
        email = "some@email.com",
        accessToken = "accessToken",
        refreshToken = "refreshToken"
    )
    private val profile = Profile("username", "recipientCode", 100L)

    @Test
    fun `userState is loading on initialization`() = runBlocking {
        declareMock<TokenStoring> {
            every { token } returns MutableStateFlow(null)
        }
        declareMock<UserRemoteSource> {
            coEvery { triggerAuthorization() } returns Result.success(Unit)
        }
        var userState: UserState? = null
        val userRepo = UserRepositoryImpl()

        scope.launch { userRepo.userState.collect { userState = it } }
        delay(100)

        val expected = UserState.Loading
        assertEquals(expected, userState)
    }

    @Test
    fun `authorization gets triggered on tokenStore providing no token`() = runBlocking {
        declareMock<TokenStoring> {
            every { token } returns MutableStateFlow(null)
        }
        val remoteSource = declareMock<UserRemoteSource> {
            coEvery { triggerAuthorization() } returns Result.success(Unit)
        }

        UserRepositoryImpl()
        delay(100)

        coVerify(exactly = 1) { remoteSource.triggerAuthorization() }
        confirmVerified(remoteSource)
    }

    @Test
    fun `userState is error on failed authentication`() = runBlocking {
        declareMock<TokenStoring> {
            every { token } returns MutableStateFlow(null)
        }
        declareMock<UserRemoteSource> {
            coEvery { triggerAuthorization() } returns Result.failure(Exception())
        }

        var userState: UserState? = null
        val userRepo = UserRepositoryImpl()

        scope.launch { userRepo.userState.collect { userState = it } }
        delay(200)

        val expected = UserState.Error
        assertEquals(expected, userState)
    }

    @Test
    fun `userState is anon on tokenStore providing anonymous token`() = runBlocking {
        declareMock<TokenStoring> {
            every { token } returns MutableStateFlow(anonToken)
        }
        var userState: UserState? = null
        val userRepo = UserRepositoryImpl()

        scope.launch { userRepo.userState.collect { userState = it } }
        delay(100)

        val expected = UserState.Anonymous(User(anonToken))
        assertEquals(expected, userState)
    }

    @Test
    fun `userState is registered and loads profile on registered token`() = runBlocking {
        val user = User(registeredToken)
        declareMock<TokenStoring> {
            every { token } returns MutableStateFlow(registeredToken)
        }
        declareMock<ProfileRemoteSource> {
            coEvery { getProfile() } coAnswers {
                delay(200)
                Result.success(profile)
            }
        }

        val userStates = mutableListOf<UserState>()
        scope.launch {
            UserRepositoryImpl().apply {
                userState.collect { userStates.add(it) }
            }
        }
        delay(500)

        assertContains(userStates, UserState.Registered(user, ProfileState.Loading))
        assertEquals(UserState.Registered(user, ProfileState.Present(profile)), userStates.last())
    }

    @Test
    fun `userState is registered with profile error when registered token but getProfile errors`() =
        runBlocking {
            val user = User(registeredToken)
            declareMock<TokenStoring> {
                every { token } returns MutableStateFlow(registeredToken)
            }
            declareMock<ProfileRemoteSource> {
                coEvery { getProfile() } returns Result.failure(Exception())
            }
            var userState: UserState? = null
            val userRepo = UserRepositoryImpl()

            scope.launch { userRepo.userState.collect { userState = it } }
            delay(100)

            assertEquals(UserState.Registered(user, ProfileState.Error), userState)
        }

    @Test
    fun `userState is registered with profile error on registered token but getProfile client errors`() =
        runBlocking {
            val user = User(registeredToken)
            declareMock<TokenStoring> {
                every { token } returns MutableStateFlow(registeredToken)
            }
            declareMock<ProfileRemoteSource> {
                coEvery { getProfile() } returns Result.failure(Exception())
            }

            var userState: UserState? = null
            val userRepo = UserRepositoryImpl()

            scope.launch { userRepo.userState.collect { userState = it } }
            delay(500)

            assertEquals(UserState.Registered(user, ProfileState.Error), userState)
        }

    @Test
    fun `userState is registered with profile non on registered token but getProfile 404s`() =
        runBlocking {
            val user = User(registeredToken)
            declareMock<TokenStoring> {
                every { token } returns MutableStateFlow(registeredToken)
            }

            declareMock<ProfileRemoteSource> {
                val exception = createMockedClientException(HttpStatusCode.NotFound)
                coEvery { getProfile() } returns Result.failure(exception)
            }

            var userState: UserState? = null
            val userRepo = UserRepositoryImpl()

            scope.launch { userRepo.userState.collect { userState = it } }
            delay(1000)

            assertEquals(UserState.Registered(user, ProfileState.None), userState)
        }

    @Test
    fun `login returns User on success`() = runBlocking {
        declareMock<TokenStoring> {
            every { setNewToken(any()) } returns Unit
            every { token } returns MutableStateFlow(anonToken)
        }
        val mockedClient = declareMock<HttpClient> {}
        declareMock<UserRemoteSource> {
            coEvery { login(any(), any()) } returns Result.success(registeredToken)
        }

        val userRepo = spyk(UserRepositoryImpl())
        val result = with(userRepo) {
            coEvery { mockedClient.clearTokens() } returns Unit
            login("email", "password")
        }

        assertEquals(true, result.isSuccess)
        assertEquals(User(registeredToken), result.getOrThrow())
    }

    @Test
    fun `login sets tokens in tokenStore on success`() = runBlocking {
        val tokenStore = declareMock<TokenStoring> {
            every { setNewToken(any()) } returns Unit
            every { token } returns MutableStateFlow(anonToken)
        }
        val mockedClient = declareMock<HttpClient> {}
        declareMock<UserRemoteSource> {
            coEvery { login(any(), any()) } returns Result.success(registeredToken)
        }

        val userRepo = spyk(UserRepositoryImpl())
        with(userRepo) {
            coEvery { mockedClient.clearTokens() } returns Unit
            login("email", "password")
        }

        verify(exactly = 1) { tokenStore.setNewToken(registeredToken) }
        confirmVerified()
    }

    @Test
    fun `login clears tokens on client on success`() = runBlocking {
        declareMock<TokenStoring> {
            every { setNewToken(any()) } returns Unit
            every { token } returns MutableStateFlow(anonToken)
        }
        val mockedClient = declareMock<HttpClient> {}
        declareMock<UserRemoteSource> {
            coEvery { login(any(), any()) } returns Result.success(registeredToken)
        }

        val userRepo = spyk(UserRepositoryImpl())
        with(userRepo) {
            coEvery { mockedClient.clearTokens() } returns Unit
            login("email", "password")
            coVerify(exactly = 1) { mockedClient.clearTokens() }
        }
        confirmVerified()
    }

    @Test
    fun `register returns User on success`() = runBlocking {
        declareMock<TokenStoring> {
            every { setNewToken(any()) } returns Unit
            every { token } returns MutableStateFlow(anonToken)
        }
        val mockedClient = declareMock<HttpClient> {}
        declareMock<UserRemoteSource> {
            coEvery { register(any(), any()) } returns Result.success(registeredToken)
        }

        val userRepo = spyk(UserRepositoryImpl())
        val result = with(userRepo) {
            coEvery { mockedClient.clearTokens() } returns Unit
            register("email", "password")
        }

        assertEquals(true, result.isSuccess)
        assertEquals(User(registeredToken), result.getOrThrow())
    }

    @Test
    fun `register sets tokens in tokenStore on success`() = runBlocking {
        val tokenStore = declareMock<TokenStoring> {
            every { setNewToken(any()) } returns Unit
            every { token } returns MutableStateFlow(anonToken)
        }
        val mockedClient = declareMock<HttpClient> {}
        declareMock<UserRemoteSource> {
            coEvery { register(any(), any()) } returns Result.success(registeredToken)
        }

        val userRepo = spyk(UserRepositoryImpl())
        with(userRepo) {
            coEvery { mockedClient.clearTokens() } returns Unit
            register("email", "password")
        }

        verify(exactly = 1) { tokenStore.setNewToken(registeredToken) }
        confirmVerified()
    }

    @Test
    fun `register clears tokens on client on success`() = runBlocking {
        declareMock<TokenStoring> {
            every { setNewToken(any()) } returns Unit
            every { token } returns MutableStateFlow(anonToken)
        }
        val mockedClient = declareMock<HttpClient> {}
        declareMock<UserRemoteSource> {
            coEvery { register(any(), any()) } returns Result.success(registeredToken)
        }

        val userRepo = spyk(UserRepositoryImpl())
        with(userRepo) {
            coEvery { mockedClient.clearTokens() } returns Unit
            register("email", "password")
            coVerify(exactly = 1) { mockedClient.clearTokens() }
        }
        confirmVerified()
    }

    @Test
    fun `logout clears tokens on client and tokenStore`() = runBlocking {
        val tokenStore = declareMock<TokenStoring> {
            every { setNewToken(null) } returns Unit
            every { token } returns MutableStateFlow(anonToken)
        }
        val mockedClient = declareMock<HttpClient> {}

        val userRepo = spyk(UserRepositoryImpl())
        with(userRepo) {
            coEvery { mockedClient.clearTokens() } returns Unit
            logout()
            coVerify(exactly = 1) { mockedClient.clearTokens() }
        }
        verify(exactly = 1) { tokenStore.setNewToken(null) }
        confirmVerified()
    }

    @Test
    fun `getQrCode passes qr code from remote repo`() = runBlocking {
        val expected = ByteArray(10)
        declareMock<ProfileRemoteSource> {
            coEvery { getQRCode() } returns Result.success(expected)
        }

        val repo = UserRepositoryImpl()
        val result = repo.getQRCode()

        assertEquals(true, result.isSuccess)
        assertEquals(expected, result.getOrThrow())
    }

    @Test
    fun `createProfile returns Profile on success`() = runBlocking {
        declareMock<TokenStoring> {
            every { token } returns MutableStateFlow(anonToken)
        }
        declareMock<ProfileRemoteSource> {
            coEvery { createProfile(any()) } returns Result.success(profile)
        }

        val userRepo = UserRepositoryImpl()
        val result = userRepo.createProfile("")

        assertEquals(true, result.isSuccess)
        assertEquals(profile, result.getOrThrow())
    }

    @Test
    fun `createProfile sets profile state on success`() = runBlocking {
        declareMock<TokenStoring> {
            every { token } returns MutableStateFlow(registeredToken)
        }
        declareMock<ProfileRemoteSource> {
            coEvery { getProfile() } returns Result.failure(Exception())
            coEvery { createProfile(any()) } returns Result.success(profile)
        }

        var userState: UserState? = null
        val userRepo = UserRepositoryImpl()
        scope.launch {
            userRepo.userState.collect {
                userState = it
            }
        }
        delay(200)
        userRepo.createProfile("")
        delay(200)

        val expected = UserState.Registered(User(registeredToken), ProfileState.Present(profile))
        assertEquals(expected, userState)
    }
}
