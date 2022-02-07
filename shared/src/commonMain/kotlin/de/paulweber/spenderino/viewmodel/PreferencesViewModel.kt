package de.paulweber.spenderino.viewmodel

import de.paulweber.spenderino.model.repositories.user.Profile
import de.paulweber.spenderino.model.repositories.user.ProfileState
import de.paulweber.spenderino.model.repositories.user.User
import de.paulweber.spenderino.model.repositories.user.UserRepository
import de.paulweber.spenderino.model.repositories.user.UserState
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import org.koin.core.component.inject

sealed class PreferencesState {
    object Loading : PreferencesState()
    object Error : PreferencesState()
    object Anonymous : PreferencesState()
    data class Registered(val user: User) : PreferencesState()
    data class SetupComplete(val user: User, val profile: Profile) : PreferencesState()
}

sealed class PreferencesRoute {
    data class Account(override val viewModel: AccountViewModel) : PreferencesRoute(), Navigating
    data class Transactions(override val viewModel: TransactionsViewModel) : PreferencesRoute(),
        Navigating
}

enum class PreferencesAction {
    ACCOUNT, TRANSACTIONS
}

class PreferencesViewModel(
    state: PreferencesState = PreferencesState.Anonymous,
    route: PreferencesRoute? = null
) :
    ViewModel<PreferencesAction, PreferencesRoute, PreferencesState>(state, route) {
    private val userRepo: UserRepository by inject()

    init {
        scope.launch {
            userRepo.userState
                .distinctUntilChanged()
                .collect { onNewUserState(it) }
        }
    }

    private fun onNewUserState(userState: UserState) {
        val newState = when (userState) {
            is UserState.Loading -> PreferencesState.Loading
            is UserState.Anonymous -> PreferencesState.Anonymous
            UserState.Error -> PreferencesState.Error
            is UserState.Registered -> {
                when (userState.profileState) {
                    is ProfileState.Loading -> PreferencesState.Loading
                    ProfileState.Error -> PreferencesState.Error
                    ProfileState.None -> PreferencesState.Registered(userState.user)
                    is ProfileState.Present -> PreferencesState.SetupComplete(
                        userState.user,
                        userState.profileState.profile
                    )
                }
            }
        }
        setState(newState)
    }

    override fun perform(action: PreferencesAction) {
        when (action) {
            PreferencesAction.ACCOUNT -> onAccount()
            PreferencesAction.TRANSACTIONS -> onTransactions()
        }
    }

    private fun onAccount() {
        val accountState = createAccountState()
        val viewModel = AccountViewModel(accountState, onBack = this::routeToNull)
        val route = PreferencesRoute.Account(viewModel)
        setRoute(route)
    }

    private fun createAccountState(): AccountState {
        return when (state.value) {
            is PreferencesState.Anonymous -> AccountState.Anonymous()
            is PreferencesState.Registered -> {
                AccountState.Registered((state.value as PreferencesState.Registered).user)
            }
            is PreferencesState.SetupComplete -> {
                val setupComplete = state.value as PreferencesState.SetupComplete
                AccountState.SetupComplete(setupComplete.user, setupComplete.profile)
            }
            PreferencesState.Error -> AccountState.Anonymous()
            PreferencesState.Loading -> AccountState.Anonymous()
        }
    }

    private fun onTransactions() {
        val viewModel = TransactionsViewModel(onBack = this::routeToNull)
        val route = PreferencesRoute.Transactions(viewModel)
        setRoute(route)
    }
}
