package de.paulweber.spenderino.model.repositories.user

import de.paulweber.spenderino.model.networking.TokenInfo
import de.paulweber.spenderino.model.networking.TokenStoring
import io.ktor.client.HttpClient
import io.ktor.client.features.ClientRequestException
import io.ktor.client.features.auth.Auth
import io.ktor.client.features.auth.providers.BearerAuthProvider
import io.ktor.client.features.get
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

interface UserRepository {
    val userState: Flow<UserState>

    suspend fun triggerAuthorization(): Result<Unit>
    suspend fun login(email: String, password: String): Result<User>
    suspend fun logout(): Result<Unit>
    suspend fun register(email: String, password: String): Result<User>

    suspend fun createProfile(username: String): Result<Profile>
    suspend fun getQRCode(): Result<ByteArray>
}

class UserRepositoryImpl : UserRepository, KoinComponent {
    private val tokenStore: TokenStoring by inject()
    private val httpClient: HttpClient by inject()

    override val userState: Flow<UserState>
        get() = mutableUserState
    private val mutableUserState = MutableStateFlow<UserState>(UserState.Loading)

    private val userRemoteSource: UserRemoteSource by inject()
    private val profileRemoteSource: ProfileRemoteSource by inject()

    private val scope = CoroutineScope(Dispatchers.Default)

    init {
        scope.launch {
            tokenStore.token
                .distinctUntilChanged { a, b ->
                    when {
                        a == null && b == null -> true
                        a != null && b == null -> false
                        a == null && b != null -> false
                        else -> a!!.identifier == b!!.identifier
                    }
                }
                .collect { onNewToken(it) }
        }
    }

    private suspend fun onNewToken(token: TokenInfo?) {
        token?.let {
            val user = User(it)
            when (user.userType) {
                User.UserType.ANONYMOUS -> mutableUserState.value = UserState.Anonymous(user)
                User.UserType.REGISTERED -> onNewUser(user)
            }
        } ?: run {
            triggerAuthorization()
        }
    }

    private suspend fun onNewUser(user: User) {
        mutableUserState.value = UserState.Registered(user, ProfileState.Loading)
        profileRemoteSource.getProfile().fold(
            onSuccess = {
                mutableUserState.value = UserState.Registered(user, ProfileState.Present(it))
            },
            onFailure = {
                when (it) {
                    is ClientRequestException -> {
                        if (it.response.status == HttpStatusCode.NotFound) {
                            mutableUserState.value = UserState.Registered(user, ProfileState.None)
                        } else {
                            mutableUserState.value = UserState.Registered(user, ProfileState.Error)
                        }
                    }
                    else -> mutableUserState.value = UserState.Registered(user, ProfileState.Error)
                }
            }
        )
    }

    override suspend fun triggerAuthorization(): Result<Unit> {
        mutableUserState.value = UserState.Loading
        val result = userRemoteSource.triggerAuthorization().onFailure {
            mutableUserState.value = UserState.Error
        }
        return result
    }

    override suspend fun login(email: String, password: String): Result<User> {
        val loginResult = userRemoteSource.login(email, password)
        loginResult.onSuccess {
            tokenStore.setNewToken(it)
            clearClientTokens()
        }
        return loginResult.map { User(it) }
    }

    override suspend fun register(email: String, password: String): Result<User> {
        val result = userRemoteSource.register(email, password)
        result.onSuccess {
            tokenStore.setNewToken(it)
            clearClientTokens()
        }
        return result.map { User(it) }
    }

    override suspend fun logout(): Result<Unit> {
        tokenStore.setNewToken(null)
        clearClientTokens()
        return Result.success(Unit)
    }

    private suspend fun clearClientTokens() {
        httpClient.clearTokens()
    }

    override suspend fun createProfile(username: String): Result<Profile> {
        val result = profileRemoteSource.createProfile(username)
        result.onSuccess {
            if (mutableUserState.value is UserState.Registered) {
                val user = (mutableUserState.value as UserState.Registered).user
                mutableUserState.value = UserState.Registered(user, ProfileState.Present(it))
            }
        }
        return result
    }

    override suspend fun getQRCode(): Result<ByteArray> {
        return profileRemoteSource.getQRCode()
    }

    suspend fun HttpClient.clearTokens() {
        this[Auth].providers.filterIsInstance<BearerAuthProvider>().first().clearToken()
    }
}
