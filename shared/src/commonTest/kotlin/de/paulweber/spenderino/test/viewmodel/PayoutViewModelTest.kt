@file:Suppress("IllegalIdentifier")

package de.paulweber.spenderino.test.viewmodel

import de.paulweber.spenderino.model.repositories.balance.BalanceRepository
import de.paulweber.spenderino.test.BaseTest
import de.paulweber.spenderino.utility.toEuroString
import de.paulweber.spenderino.viewmodel.PayoutState
import de.paulweber.spenderino.viewmodel.PayoutViewModel
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.koin.test.mock.declareMock
import kotlin.test.Test
import kotlin.test.assertEquals

class PayoutViewModelTest : BaseTest() {
    @Test
    fun `State gets set to error on fetching qr code error`() = runBlocking {
        declareMock<BalanceRepository> {
            coEvery { pollIsWithdrawalActive() } returns Result.success(true)

            coEvery { getQrCode() } coAnswers {
                delay(100)
                Result.failure(Exception())
            }
        }

        val viewModel = PayoutViewModel(mockk(relaxed = true))
        assertEquals(PayoutState.Loading, viewModel.state.value)

        delay(200)
        assertEquals(PayoutState.Error, viewModel.state.value)
    }

    @Test
    fun `State gets set to qr code on fetching qr code`() = runBlocking {
        val expectedQrCode = ByteArray(10)
        declareMock<BalanceRepository> {
            coEvery { pollIsWithdrawalActive() } returns Result.success(true)
            coEvery { getQrCode() } coAnswers {
                delay(100)
                Result.success(expectedQrCode)
            }
        }

        val viewModel = PayoutViewModel(mockk(relaxed = true))
        assertEquals(PayoutState.Loading, viewModel.state.value)

        delay(200)

        val expected = PayoutState.QRCode("balance_withdrawal+${0L.toEuroString()}", expectedQrCode)
        assertEquals(expected, viewModel.state.value)
    }

    @Test
    fun `Polling success results in Success State`() = runBlocking {
        declareMock<BalanceRepository> {
            coEvery { pollIsWithdrawalActive() } returns Result.success(false)
            coEvery { getQrCode() } returns Result.success(ByteArray(10))
        }

        val viewModel = PayoutViewModel(mockk(relaxed = true))

        delay(3000)

        assertEquals(PayoutState.Success, viewModel.state.value)
    }
}
