@file:Suppress("IllegalIdentifier")

package de.paulweber.spenderino.test.viewmodel

import de.paulweber.spenderino.model.repositories.balance.Balance
import de.paulweber.spenderino.model.repositories.balance.BalanceRepository
import de.paulweber.spenderino.model.repositories.user.Profile
import de.paulweber.spenderino.model.repositories.user.ProfileState
import de.paulweber.spenderino.model.repositories.user.User
import de.paulweber.spenderino.model.repositories.user.UserRepository
import de.paulweber.spenderino.model.repositories.user.UserState
import de.paulweber.spenderino.test.BaseTest
import de.paulweber.spenderino.viewmodel.RecipientAction
import de.paulweber.spenderino.viewmodel.RecipientRoute
import de.paulweber.spenderino.viewmodel.RecipientState
import de.paulweber.spenderino.viewmodel.RecipientViewModel
import io.mockk.coEvery
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
import kotlin.test.assertNotNull

class RecipientViewModelTests : BaseTest() {
    private val user = User("", User.UserType.ANONYMOUS, null)
    private val profile = Profile("My name", "", 100L)
    private val balance = Balance(2500L, null, listOf())

    @Test
    fun `loading UserState results in Loading State`() = runBlocking {
        declareMock<UserRepository> {
            every { userState } returns MutableStateFlow(UserState.Loading)
        }
        declareMock<BalanceRepository> { }

        val viewModel = RecipientViewModel()
        delay(100)

        assertEquals(RecipientState.Loading, viewModel.state.value)
    }

    @Test
    fun `anonymous UserState results in Anonymous State`() = runBlocking {
        declareMock<UserRepository> {
            every { userState } returns MutableStateFlow(UserState.Anonymous(user))
        }
        declareMock<BalanceRepository> { }

        val viewModel = RecipientViewModel()
        delay(100)

        assertEquals(RecipientState.Anonymous, viewModel.state.value)
    }

    @Test
    fun `error UserState results in Error State`() = runBlocking {
        declareMock<UserRepository> {
            every { userState } returns MutableStateFlow(UserState.Error)
        }
        declareMock<BalanceRepository> { }

        val viewModel = RecipientViewModel()
        delay(100)

        assertEquals(RecipientState.Error, viewModel.state.value)
    }

    @Test
    fun `registered UserState with loading profile results in Loading State`() = runBlocking {
        declareMock<UserRepository> {
            every { userState } returns MutableStateFlow(
                UserState.Registered(
                    user,
                    ProfileState.Loading
                )
            )
        }
        declareMock<BalanceRepository> { }

        val viewModel = RecipientViewModel()
        delay(100)

        assertEquals(RecipientState.Loading, viewModel.state.value)
    }

    @Test
    fun `registered UserState with error profile results in Error State`() = runBlocking {
        declareMock<UserRepository> {
            every { userState } returns MutableStateFlow(
                UserState.Registered(
                    user,
                    ProfileState.Error
                )
            )
        }
        declareMock<BalanceRepository> { }

        val viewModel = RecipientViewModel()
        delay(100)

        assertEquals(RecipientState.Error, viewModel.state.value)
    }

    @Test
    fun `registered UserState with no profile results in Registered State`() = runBlocking {
        declareMock<UserRepository> {
            every { userState } returns MutableStateFlow(
                UserState.Registered(
                    user,
                    ProfileState.None
                )
            )
        }
        declareMock<BalanceRepository> { }

        val viewModel = RecipientViewModel()
        delay(100)

        assertEquals(RecipientState.Registered(user), viewModel.state.value)
    }

    @Test
    fun `registered UserState with profile results in Pager State with QR code`() = runBlocking {
        val expectedQrCode = ByteArray(10)
        declareMock<UserRepository> {
            every { userState } returns MutableStateFlow(
                UserState.Registered(
                    user,
                    ProfileState.Present(profile)
                )
            )
            coEvery { getQRCode() } returns Result.success(expectedQrCode)
        }
        declareMock<BalanceRepository> {
            coEvery { getBalance() } coAnswers {
                delay(200)
                Result.success(balance)
            }
        }

        val viewModel = RecipientViewModel()
        delay(500)

        val pagerState = viewModel.state.value as RecipientState.Pager
        val expectedQRState = RecipientState.QRState(
            "recipient_qrcode_title+My name",
            expectedQrCode
        )
        val expectedBalanceState = RecipientState.BalanceState.Base(
            RecipientState.BalanceState.StateHolder(
                2500L,
                false
            )
        )
        assertEquals(expectedQRState, pagerState.qrState)
        assertEquals(expectedBalanceState, pagerState.balanceState)
    }

    @Test
    fun `getBalance error results in Pager State with balance Error state`() = runBlocking {
        declareMock<UserRepository> {
            every { userState } returns MutableStateFlow(
                UserState.Registered(
                    user,
                    ProfileState.Present(profile)
                )
            )
            coEvery { getQRCode() } returns Result.success(ByteArray(10))
        }
        declareMock<BalanceRepository> {
            coEvery { getBalance() } returns Result.failure(Exception())
        }

        val viewModel = RecipientViewModel()
        delay(400)

        val result = (viewModel.state.value as RecipientState.Pager).balanceState
        assertEquals(RecipientState.BalanceState.Error, result)
    }

    @Test
    fun `login action sets AccountViewModel route`() = runBlocking {
        declareMock<UserRepository> {
            every { userState } returns MutableStateFlow(UserState.Anonymous(user))
        }
        val viewModel = RecipientViewModel()
        delay(100)
        viewModel.perform(RecipientAction.Login)

        assertEquals(true, viewModel.route.value is RecipientRoute.Account)
        val result = (viewModel.route.value as RecipientRoute.Account).viewModel

        result.onSetupComplete()

        assertEquals(null, viewModel.route.value)
    }

    @Test
    fun `createProfile action sets AccountViewModel route`() = runBlocking {
        declareMock<UserRepository> {
            every { userState } returns MutableStateFlow(
                UserState.Registered(
                    user,
                    ProfileState.None
                )
            )
        }
        val viewModel = RecipientViewModel()
        delay(100)
        viewModel.perform(RecipientAction.CreateProfile)

        assertEquals(true, viewModel.route.value is RecipientRoute.Account)
        val result = (viewModel.route.value as RecipientRoute.Account).viewModel

        result.onSetupComplete()

        assertEquals(null, viewModel.route.value)
    }

    @Test
    fun `CreateWithdrawal action sets isCreatingWithdrawal flag and removes it on error`() =
        runBlocking {
            declareMock<UserRepository> {
                every { userState } returns MutableStateFlow(
                    UserState.Registered(
                        user,
                        ProfileState.Present(profile)
                    )
                )
                coEvery { getQRCode() } returns Result.success(ByteArray(10))
            }
            declareMock<BalanceRepository> {
                coEvery { getBalance() } returns Result.success(balance)
                coEvery { createWithdrawal(any()) } returns Result.failure(Exception())
            }

            val results = mutableListOf<RecipientState>()
            val viewModel = RecipientViewModel()
            delay(400)
            scope.launch { viewModel.state.collect { results.add(it) } }
            viewModel.perform(RecipientAction.CreateWithdrawal(500L))
            delay(200)

            assertEquals(3, results.size)
            val balanceStates = results.map {
                (it as RecipientState.Pager)
                    .balanceState as RecipientState.BalanceState.Base
            }
            assertEquals(true, balanceStates[1].stateHolder.isCreatingWithdrawal)
            assertEquals(false, balanceStates[2].stateHolder.isCreatingWithdrawal)
        }

    @Test
    fun `CreateWithdrawal action sets alert route on error`() =
        runBlocking {
            declareMock<UserRepository> {
                every { userState } returns MutableStateFlow(
                    UserState.Registered(
                        user,
                        ProfileState.Present(profile)
                    )
                )
                coEvery { getQRCode() } returns Result.success(ByteArray(10))
            }
            declareMock<BalanceRepository> {
                coEvery { getBalance() } returns Result.success(balance)
                coEvery { createWithdrawal(any()) } returns Result.failure(Exception())
            }

            val viewModel = RecipientViewModel()
            delay(400)
            viewModel.perform(RecipientAction.CreateWithdrawal(500L))
            delay(200)

            assertEquals(true, viewModel.route.value is RecipientRoute.Alert)
        }

    @Test
    fun `CreateWithdrawal action sets payout route on success`() =
        runBlocking {
            declareMock<UserRepository> {
                every { userState } returns MutableStateFlow(
                    UserState.Registered(
                        user,
                        ProfileState.Present(profile)
                    )
                )
                coEvery { getQRCode() } returns Result.success(ByteArray(10))
            }
            declareMock<BalanceRepository> {
                coEvery { getBalance() } returns Result.success(balance)
                coEvery { createWithdrawal(any()) } returns Result.success(mockk(relaxed = true))
                coEvery { pollIsWithdrawalActive() } returns Result.success(true)
            }

            val viewModel = RecipientViewModel()
            delay(400)
            viewModel.perform(RecipientAction.CreateWithdrawal(500L))
            delay(200)

            assertEquals(true, viewModel.route.value is RecipientRoute.Payout)

            val payoutViewModel = (viewModel.route.value as RecipientRoute.Payout).viewModel
            val results = mutableListOf<RecipientState>()
            scope.launch { viewModel.state.collect { results.add(it) } }
            payoutViewModel.onBackButton()

            assertEquals(null, viewModel.route.value)
            assertNotNull(results.any {
                it is RecipientState.Pager &&
                    it.balanceState is RecipientState.BalanceState.Reloading
            })
            return@runBlocking
        }
}
