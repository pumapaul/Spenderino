package de.paulweber.spenderino.viewmodel

import de.paulweber.spenderino.model.repositories.RemoteException
import de.paulweber.spenderino.model.repositories.user.Profile
import de.paulweber.spenderino.model.repositories.toAlert
import de.paulweber.spenderino.model.repositories.toRemoteException
import de.paulweber.spenderino.model.repositories.user.ProfileState
import de.paulweber.spenderino.model.repositories.user.User
import de.paulweber.spenderino.model.repositories.user.UserRepository
import de.paulweber.spenderino.model.repositories.user.UserState
import io.ktor.client.features.ClientRequestException
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import org.koin.core.component.inject

sealed class AccountState {
    object Loading : AccountState()
    object Error : AccountState()
    data class Anonymous(
        val isLoginLoading: Boolean = false,
        val isRegisterLoading: Boolean = false,
        val email: String = "",
        val password: String = ""
    ) : AccountState()

    data class Registered(
        val user: User,
        val isLoading: Boolean = false,
        val username: String = ""
    ) : AccountState()

    data class SetupComplete(val user: User, val profile: Profile) : AccountState()
}

sealed class AccountRoute {
    data class Alert(override val alert: AlertViewModel) : AccountRoute(), AlertRoute
    data class LogoutAlert(override val alert: AlertViewModel) : AccountRoute(), AlertRoute
}

sealed class AccountAction {
    object Login : AccountAction()
    object Register : AccountAction()
    object CreateProfile : AccountAction()
    object Logout : AccountAction()
    object ConfirmLogout : AccountAction()
    data class ChangeEmailText(val text: String) : AccountAction()
    data class ChangePasswordText(val text: String) : AccountAction()
    data class ChangeUsernameText(val text: String) : AccountAction()
}

class AccountViewModel(
    state: AccountState = AccountState.Anonymous(),
    route: AccountRoute? = null,
    val onSetupComplete: () -> Unit = {},
    onBack: () -> Unit = {}
) : ViewModel<AccountAction, AccountRoute, AccountState>(state, route, onBack) {
    private val userRepo: UserRepository by inject()

    private val wrongCredentialsAlert: AlertViewModel by lazy {
        AlertViewModel(
            "account_alert_credentials",
            this::routeToNull
        )
    }
    private val duplicateUserAlert: AlertViewModel by lazy {
        AlertViewModel(
            "account_alert_duplicate",
            this::routeToNull
        )
    }
    private val passwordRulesAlert: AlertViewModel by lazy {
        AlertViewModel(
            "account_alert_rules",
            this::routeToNull
        )
    }
    private val badNameAlert: AlertViewModel by lazy {
        AlertViewModel(
            "account_alert_username",
            this::routeToNull
        )
    }
    private val logoutAlert: AlertViewModel by lazy {
        AlertViewModel(
            "account_alert_logout",
            this::routeToNull
        )
    }

    init {
        scope.launch {
            userRepo.userState
                .distinctUntilChanged()
                .collect { onNewUserState(it) }
        }
    }

    private fun onNewUserState(userState: UserState) {
        val newState = when (userState) {
            is UserState.Loading -> AccountState.Loading
            is UserState.Anonymous -> AccountState.Anonymous()
            UserState.Error -> AccountState.Error
            is UserState.Registered -> {
                when (userState.profileState) {
                    is ProfileState.Loading -> AccountState.Loading
                    ProfileState.Error -> AccountState.Error
                    ProfileState.None -> AccountState.Registered(userState.user)
                    is ProfileState.Present -> AccountState.SetupComplete(
                        userState.user,
                        userState.profileState.profile
                    )
                }
            }
        }
        setState(newState)
    }

    override fun perform(action: AccountAction) {
        when (action) {
            is AccountAction.Login -> onLogin()
            is AccountAction.Register -> onRegister()
            is AccountAction.ChangeEmailText -> onEmailTextChanged(action.text)
            is AccountAction.ChangePasswordText -> onPasswordTextChanged(action.text)
            is AccountAction.ChangeUsernameText -> onUsernameTextChanged(action.text)
            AccountAction.CreateProfile -> onCreateProfile()
            AccountAction.Logout -> onLogout()
            AccountAction.ConfirmLogout -> onConfirmLogout()
        }
    }

    private fun onLogin() = scope.launch {
        val email = (state.value as AccountState.Anonymous).email
        val password = (state.value as AccountState.Anonymous).password

        setIsLoginLoading(true)
        userRepo.login(email, password).fold(
            onSuccess = {
                setIsLoginLoading(false)
            },
            onFailure = {
                setIsLoginLoading(false)
                val alert = when (it.toRemoteException()) {
                    is RemoteException.Unauthorized -> wrongCredentialsAlert
                    else -> it.toAlert { routeToNull() }
                }
                setRoute(AccountRoute.Alert(alert))
            }
        )
    }

    private fun onRegister() = scope.launch {
        val email = (state.value as AccountState.Anonymous).email
        val password = (state.value as AccountState.Anonymous).password

        setIsRegisterLoading(true)

        userRepo.register(email, password).onFailure { onRegisterFailure(it) }
        setIsRegisterLoading(false)
    }

    private fun onRegisterFailure(it: Throwable) {
        val alert = when (it) {
            is ClientRequestException -> {
                when (it.response.status) {
                    HttpStatusCode.Conflict -> duplicateUserAlert
                    HttpStatusCode.BadRequest -> {
                        onPasswordTextChanged("")
                        passwordRulesAlert
                    }
                    else -> it.toAlert(this::routeToNull)
                }
            }
            else -> it.toAlert(this::routeToNull)
        }
        setRoute(AccountRoute.Alert(alert))
    }

    private fun onCreateProfile() = scope.launch {
        setIsCreateProfileLoading(true)

        val username = (state.value as AccountState.Registered).username

        userRepo.createProfile(username).fold(
            onSuccess = {
                onSetupComplete()
            },
            onFailure = {
                setIsCreateProfileLoading(false)
                val alert =
                    if (it is ClientRequestException && it.response.status == HttpStatusCode.BadRequest) {
                        badNameAlert
                    } else it.toAlert { routeToNull() }
                val route = AccountRoute.Alert(alert)
                setRoute(route)
            }
        )
    }

    private fun onConfirmLogout() = scope.launch {
        userRepo.logout().onSuccess {
            setState(AccountState.Loading)
        }
    }

    private fun onLogout() {
        val route = AccountRoute.LogoutAlert(logoutAlert)
        setRoute(route)
    }

    private fun setIsCreateProfileLoading(boolean: Boolean) {
        if (state.value is AccountState.Registered) {
            setState((state.value as AccountState.Registered).copy(isLoading = boolean))
        }
    }

    private fun setIsLoginLoading(boolean: Boolean) {
        if (state.value is AccountState.Anonymous) {
            setState((state.value as AccountState.Anonymous).copy(isLoginLoading = boolean))
        }
    }

    private fun setIsRegisterLoading(boolean: Boolean) {
        if (state.value is AccountState.Anonymous) {
            setState((state.value as AccountState.Anonymous).copy(isRegisterLoading = boolean))
        }
    }

    private fun onEmailTextChanged(newText: String) {
        if (state.value is AccountState.Anonymous) {
            setState((state.value as AccountState.Anonymous).copy(email = newText))
        }
    }

    private fun onPasswordTextChanged(newText: String) {
        if (state.value is AccountState.Anonymous) {
            setState((state.value as AccountState.Anonymous).copy(password = newText))
        }
    }

    private fun onUsernameTextChanged(newText: String) {
        if (state.value is AccountState.Registered) {
            setState((state.value as AccountState.Registered).copy(username = newText))
        }
    }
}
