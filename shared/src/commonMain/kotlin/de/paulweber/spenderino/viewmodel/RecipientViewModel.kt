package de.paulweber.spenderino.viewmodel

import de.paulweber.spenderino.model.repositories.balance.BalanceRepository
import de.paulweber.spenderino.model.repositories.toAlert
import de.paulweber.spenderino.model.repositories.user.Profile
import de.paulweber.spenderino.model.repositories.user.ProfileState
import de.paulweber.spenderino.model.repositories.user.User
import de.paulweber.spenderino.model.repositories.user.UserRepository
import de.paulweber.spenderino.model.repositories.user.UserState
import de.paulweber.spenderino.utility.L10n
import de.paulweber.spenderino.utility.toEuroString
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import org.koin.core.component.inject

sealed class RecipientState {
    data class Pager(val qrState: QRState, val balanceState: BalanceState) : RecipientState()
    data class Registered(val user: User) : RecipientState()
    object Anonymous : RecipientState()
    object Loading : RecipientState()
    object Error : RecipientState()

    @Suppress("ArrayInDataClass")
    data class QRState(val text: String, val qrCode: ByteArray?)

    sealed class BalanceState {
        object Loading : BalanceState()
        object Error : BalanceState()

        data class Base(val stateHolder: StateHolder) : BalanceState()
        data class Reloading(val stateHolder: StateHolder) : BalanceState()
        data class StateHolder(
            val currentBalance: Long,
            val isCreatingWithdrawal: Boolean,
        )
    }
}

sealed class RecipientRoute {
    data class Account(val viewModel: AccountViewModel) : RecipientRoute()
    data class Alert(override val alert: AlertViewModel) : RecipientRoute(), AlertRoute
    data class Payout(override val viewModel: PayoutViewModel) : RecipientRoute(), Navigating
}

sealed class RecipientAction {
    object Login : RecipientAction()
    object CreateProfile : RecipientAction()
    object ReloadRecipient : RecipientAction()
    object ReloadBalance : RecipientAction()
    data class CreateWithdrawal(val amount: Long) : RecipientAction()
}

class RecipientViewModel(
    state: RecipientState = RecipientState.Loading,
    route: RecipientRoute? = null
) :
    ViewModel<RecipientAction, RecipientRoute, RecipientState>(state, route) {
    private val userRepo: UserRepository by inject()
    private val balanceRepo: BalanceRepository by inject()

    val minWithdrawalSum: Long = 500
    val maxWithdrawalSum: Long = 5000

    val withdrawalUnderMinText = L10n.format(
        "balance_withdrawal_under_min",
        minWithdrawalSum.toEuroString(),
        minWithdrawalSum.toEuroString(),
        maxWithdrawalSum.toEuroString()
    )

    init {
        scope.launch {
            userRepo.userState
                .distinctUntilChanged()
                .collect { onNewUserState(it) }
        }
    }

    private suspend fun onNewUserState(userState: UserState) {
        when (userState) {
            is UserState.Loading -> setState(RecipientState.Loading)
            is UserState.Anonymous -> setState(RecipientState.Anonymous)
            UserState.Error -> setState(RecipientState.Error)
            is UserState.Registered -> {
                when (userState.profileState) {
                    is ProfileState.Loading -> setState(RecipientState.Loading)
                    ProfileState.Error -> setState(RecipientState.Error)
                    ProfileState.None -> setState(RecipientState.Registered(userState.user))
                    is ProfileState.Present -> onRegisteredProfile(userState.profileState.profile)
                }
            }
        }
    }

    private fun generateProfileText(profile: Profile): String {
        return L10n.format("recipient_qrcode_title", profile.username)
    }

    private suspend fun onRegisteredProfile(profile: Profile) {
        val text = generateProfileText(profile)
        val profileState = RecipientState.QRState(text, null)
        val newState = RecipientState.Pager(profileState, RecipientState.BalanceState.Loading)
        setState(newState)

        fetchQrCode(profile)
        fetchBalance()
    }

    private suspend fun fetchQrCode(profile: Profile) = scope.launch {
        val qrCode = userRepo.getQRCode().getOrNull()
        if (state.value is RecipientState.Pager) {
            val text = generateProfileText(profile)
            val newQrState = RecipientState.QRState(text, qrCode)
            val newState = (state.value as RecipientState.Pager).copy(qrState = newQrState)
            setState(newState)
        }
    }

    private fun fetchBalance() = scope.launch {
        if (state.value is RecipientState.Pager) {
            setBalanceLoadingState()

            balanceRepo.getBalance().fold(
                onSuccess = {
                    val stateHolder = RecipientState.BalanceState.StateHolder(it.amount, false)
                    setBalanceState(RecipientState.BalanceState.Base(stateHolder))
                },
                onFailure = { setBalanceState(RecipientState.BalanceState.Error) }
            )
        }
    }

    private fun setBalanceState(balanceState: RecipientState.BalanceState) {
        if (state.value is RecipientState.Pager) {
            val pagerState = state.value as RecipientState.Pager
            val newState = pagerState.copy(balanceState = balanceState)
            setState(newState)
        }
    }

    private fun setBalanceLoadingState() {
        if (state.value is RecipientState.Pager) {
            val pagerState = state.value as RecipientState.Pager
            val balanceLoadingState = getPreviousBalanceStateHolder(pagerState.balanceState)?.let {
                RecipientState.BalanceState.Reloading(it)
            } ?: RecipientState.BalanceState.Loading
            val loadingState = pagerState.copy(balanceState = balanceLoadingState)
            setState(loadingState)
        }
    }

    private fun getPreviousBalanceStateHolder(
        balanceState: RecipientState.BalanceState
    ): RecipientState.BalanceState.StateHolder? {
        return when (balanceState) {
            is RecipientState.BalanceState.Base -> balanceState.stateHolder
            is RecipientState.BalanceState.Reloading -> balanceState.stateHolder
            else -> null
        }
    }

    override fun perform(action: RecipientAction) {
        when (action) {
            RecipientAction.Login -> onLogin()
            RecipientAction.CreateProfile -> onCreateProfile()
            RecipientAction.ReloadRecipient -> onReloadRecipient()
            is RecipientAction.CreateWithdrawal -> onCreateWithdrawal(action.amount)
            RecipientAction.ReloadBalance -> fetchBalance()
        }
    }

    private fun onReloadRecipient() = scope.launch {
        setState(RecipientState.Loading)
        userRepo.triggerAuthorization()
    }

    private fun onLogin() {
        val viewModel = AccountViewModel(
            AccountState.Anonymous(),
            onSetupComplete = this::routeToNull,
            onBack = this::routeToNull
        )
        val route = RecipientRoute.Account(viewModel)
        setRoute(route)
    }

    private fun onCreateProfile() {
        val user = (state.value as RecipientState.Registered).user
        val viewModel = AccountViewModel(
            AccountState.Registered(user),
            onSetupComplete = this::routeToNull,
            onBack = this::routeToNull
        )
        val route = RecipientRoute.Account(viewModel)
        setRoute(route)
    }

    private fun setIsCreatingWithdrawal(boolean: Boolean) {
        if (state.value is RecipientState.Pager) {
            val pagerState = state.value as RecipientState.Pager

            if (pagerState.balanceState is RecipientState.BalanceState.Base) {
                val currentStateHolder = pagerState.balanceState.stateHolder
                val newStateHolder = currentStateHolder.copy(isCreatingWithdrawal = boolean)
                val newBalanceState = RecipientState.BalanceState.Base(newStateHolder)
                setBalanceState(newBalanceState)
            }
        }
    }

    private fun onCreateWithdrawal(amount: Long) = scope.launch {
        setIsCreatingWithdrawal(true)

        balanceRepo.createWithdrawal(amount).fold(
            onSuccess = { withdrawal ->
                setIsCreatingWithdrawal(false)
                val payoutViewModel = PayoutViewModel(
                    withdrawal,
                    onBack = {
                        fetchBalance()
                        routeToNull()
                    }
                )
                val route = RecipientRoute.Payout(payoutViewModel)
                setRoute(route)
            },
            onFailure = {
                setIsCreatingWithdrawal(false)
                setAlertRoute(it)
            }
        )
    }

    private fun setAlertRoute(throwable: Throwable) {
        val alert = throwable.toAlert(this::routeToNull)
        val route = RecipientRoute.Alert(alert)
        setRoute(route)
    }
}
