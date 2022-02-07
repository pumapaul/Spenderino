@file:Suppress("IllegalIdentifier")

package de.paulweber.spenderino.test.model.repositories

import de.paulweber.spenderino.model.repositories.balance.Balance
import de.paulweber.spenderino.model.repositories.balance.BalanceRemoteSource
import de.paulweber.spenderino.model.repositories.balance.BalanceRepositoryImpl
import de.paulweber.spenderino.model.repositories.balance.Withdrawal
import de.paulweber.spenderino.test.BaseTest
import io.mockk.coEvery
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.Clock
import org.koin.test.mock.declareMock
import kotlin.test.Test
import kotlin.test.assertEquals

class BalanceRepositoryTest : BaseTest() {
    private val balance = Balance(100L, null, listOf())
    private val withdrawal = Withdrawal("id", "beneficiary", 100L, Clock.System.now())
    private val qrCode = ByteArray(10)

    @Test
    fun `getBalance passes successful result from remoteSource to caller`() = runBlocking {
        val expected = balance
        declareMock<BalanceRemoteSource> {
            coEvery { getBalance() } returns Result.success(expected)
        }

        val repo = BalanceRepositoryImpl()
        val result = repo.getBalance()

        assertEquals(true, result.isSuccess)
        assertEquals(expected, result.getOrThrow())
    }

    @Test
    fun `getBalance passes failing result from remoteSource to caller`() = runBlocking {
        val expected = NullPointerException("specific")
        declareMock<BalanceRemoteSource> {
            coEvery { getBalance() } returns Result.failure(expected)
        }

        val repo = BalanceRepositoryImpl()
        val result = repo.getBalance()

        assertEquals(true, result.isFailure)
        assertEquals(expected, result.exceptionOrNull())
    }

    @Test
    fun `getQrCode passes successful result from remoteSource to caller`() = runBlocking {
        val expected = qrCode
        declareMock<BalanceRemoteSource> {
            coEvery { getQrCode() } returns Result.success(expected)
        }

        val repo = BalanceRepositoryImpl()
        val result = repo.getQrCode()

        assertEquals(true, result.isSuccess)
        assertEquals(expected, result.getOrThrow())
    }

    @Test
    fun `getQrCode passes failing result from remoteSource to caller`() = runBlocking {
        val expected = NullPointerException("specific")
        declareMock<BalanceRemoteSource> {
            coEvery { getQrCode() } returns Result.failure(expected)
        }

        val repo = BalanceRepositoryImpl()
        val result = repo.getQrCode()

        assertEquals(true, result.isFailure)
        assertEquals(expected, result.exceptionOrNull())
    }

    @Test
    fun `pollIsWithdrawalActive passes successful result from remoteSource to caller`() = runBlocking {
        val expected = true
        declareMock<BalanceRemoteSource> {
            coEvery { pollWithdrawal() } returns Result.success(expected)
        }

        val repo = BalanceRepositoryImpl()
        val result = repo.pollIsWithdrawalActive()

        assertEquals(true, result.isSuccess)
        assertEquals(expected, result.getOrThrow())
    }

    @Test
    fun `pollIsWithdrawalActive passes failing result from remoteSource to caller`() = runBlocking {
        val expected = NullPointerException("specific")
        declareMock<BalanceRemoteSource> {
            coEvery { pollWithdrawal() } returns Result.failure(expected)
        }

        val repo = BalanceRepositoryImpl()
        val result = repo.pollIsWithdrawalActive()

        assertEquals(true, result.isFailure)
        assertEquals(expected, result.exceptionOrNull())
    }

    @Test
    fun `cancelWithdrawal passes successful result from remoteSource to caller`() = runBlocking {
        val expected = Unit
        declareMock<BalanceRemoteSource> {
            coEvery { cancelWithdrawal() } returns Result.success(expected)
        }

        val repo = BalanceRepositoryImpl()
        val result = repo.cancelWithdrawal()

        assertEquals(true, result.isSuccess)
        assertEquals(expected, result.getOrThrow())
    }

    @Test
    fun `cancelWithdrawal passes failing result from remoteSource to caller`() = runBlocking {
        val expected = NullPointerException("specific")
        declareMock<BalanceRemoteSource> {
            coEvery { cancelWithdrawal() } returns Result.failure(expected)
        }

        val repo = BalanceRepositoryImpl()
        val result = repo.cancelWithdrawal()

        assertEquals(true, result.isFailure)
        assertEquals(expected, result.exceptionOrNull())
    }

    @Test
    fun `createWithdrawal passes input to remoteSource and returns success result correctly`() = runBlocking {
        val input = 100L
        val expected = withdrawal
        declareMock<BalanceRemoteSource> {
            coEvery { createWithdrawal(input) } returns Result.success(expected)
        }

        val repo = BalanceRepositoryImpl()
        val result = repo.createWithdrawal(input)

        assertEquals(true, result.isSuccess)
        assertEquals(expected, result.getOrThrow())
    }

    @Test
    fun `createWithdrawal passes failing result from remoteSource to caller`() = runBlocking {
        val expected = NullPointerException("specific")
        declareMock<BalanceRemoteSource> {
            coEvery { createWithdrawal(any()) } returns Result.failure(expected)
        }

        val repo = BalanceRepositoryImpl()
        val result = repo.createWithdrawal(0L)

        assertEquals(true, result.isFailure)
        assertEquals(expected, result.exceptionOrNull())
    }
}
