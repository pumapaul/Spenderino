@file:Suppress("IllegalIdentifier")

package de.paulweber.spenderino.test.model.repositories

import de.paulweber.spenderino.model.repositories.balance.Withdrawal
import de.paulweber.spenderino.model.repositories.transaction.Transaction
import de.paulweber.spenderino.model.repositories.transaction.TransactionRemoteSource
import de.paulweber.spenderino.model.repositories.transaction.TransactionRepositoryImpl
import de.paulweber.spenderino.test.BaseTest
import io.mockk.coEvery
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.Clock
import org.koin.test.mock.declareMock
import kotlin.test.Test
import kotlin.test.assertEquals

class TransactionRepositoryTest : BaseTest() {
    private val transaction = Transaction(
        amount = 100L,
        amountWithFees = 100L,
        paymentIntentId = "",
        donator = Transaction.Entity(null, "first"),
        recipient = Transaction.Entity(null, "second"),
        timestamp = Clock.System.now(),
        state = Transaction.State.PENDING
    )
    private val withdrawal = Withdrawal("id", "beneficiary", 100L, Clock.System.now())

    @Test
    fun `getTransactions passes successful result from remoteSource to caller`() = runBlocking {
        val expected = listOf(transaction)
        declareMock<TransactionRemoteSource> {
            coEvery { getTransactions() } returns Result.success(expected)
        }

        val repo = TransactionRepositoryImpl()
        val result = repo.getTransactions()

        assertEquals(true, result.isSuccess)
        assertEquals(expected, result.getOrThrow())
    }

    @Test
    fun `getTransactions passes failing result from remoteSource to caller`() = runBlocking {
        val expected = NullPointerException("specific")
        declareMock<TransactionRemoteSource> {
            coEvery { getTransactions() } returns Result.failure(expected)
        }

        val repo = TransactionRepositoryImpl()
        val result = repo.getTransactions()

        assertEquals(true, result.isFailure)
        assertEquals(expected, result.exceptionOrNull())
    }

    @Test
    fun `getWithdrawals passes successful result from remoteSource to caller`() = runBlocking {
        val expected = listOf(withdrawal)
        declareMock<TransactionRemoteSource> {
            coEvery { getWithdrawals() } returns Result.success(expected)
        }

        val repo = TransactionRepositoryImpl()
        val result = repo.getWithdrawals()

        assertEquals(true, result.isSuccess)
        assertEquals(expected, result.getOrThrow())
    }

    @Test
    fun `getWithdrawals passes failing result from remoteSource to caller`() = runBlocking {
        val expected = NullPointerException("specific")
        declareMock<TransactionRemoteSource> {
            coEvery { getWithdrawals() } returns Result.failure(expected)
        }

        val repo = TransactionRepositoryImpl()
        val result = repo.getWithdrawals()

        assertEquals(true, result.isFailure)
        assertEquals(expected, result.exceptionOrNull())
    }
}
