@file:Suppress("IllegalIdentifier")

package de.paulweber.spenderino.test.viewmodel

import de.paulweber.spenderino.model.repositories.balance.Withdrawal
import de.paulweber.spenderino.model.repositories.transaction.Transaction
import de.paulweber.spenderino.model.repositories.transaction.TransactionRepository
import de.paulweber.spenderino.model.repositories.user.Profile
import de.paulweber.spenderino.model.repositories.user.ProfileState
import de.paulweber.spenderino.model.repositories.user.User
import de.paulweber.spenderino.model.repositories.user.UserRepository
import de.paulweber.spenderino.model.repositories.user.UserState
import de.paulweber.spenderino.test.BaseTest
import de.paulweber.spenderino.viewmodel.TransactionAction
import de.paulweber.spenderino.viewmodel.TransactionState
import de.paulweber.spenderino.viewmodel.TransactionsViewModel
import io.mockk.coEvery
import io.mockk.every
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.flow.collect
import kotlinx.datetime.Instant
import org.koin.test.mock.declareMock
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class TransactionsViewModelTest : BaseTest() {
    private val withdrawals = listOf(
        Withdrawal("5", "1", 100L, Instant.fromEpochSeconds(1)),
        Withdrawal("2", "2", 200L, Instant.fromEpochSeconds(4)),
        Withdrawal("0", "3", 69420L, Instant.fromEpochSeconds(7)),
    )
    private val transactions = listOf(
        Transaction(
            amount = 500L,
            amountWithFees = 700L,
            paymentIntentId = "4",
            donator = Transaction.Entity(null, "myIdentifier"),
            recipient = Transaction.Entity("Recipient", ""),
            timestamp = Instant.fromEpochSeconds(2),
            state = Transaction.State.PENDING
        ),
        Transaction(
            amount = 500L,
            amountWithFees = 700L,
            paymentIntentId = "3",
            donator = Transaction.Entity("Donator", ""),
            recipient = Transaction.Entity(null, "myIdentifier"),
            timestamp = Instant.fromEpochSeconds(3),
            state = Transaction.State.PENDING
        ),
        Transaction(
            amount = 500L,
            amountWithFees = 700L,
            paymentIntentId = "1",
            donator = Transaction.Entity(null, ""),
            recipient = Transaction.Entity(null, ""),
            timestamp = Instant.fromEpochSeconds(6),
            state = Transaction.State.PENDING
        ),
    )

    @BeforeTest
    fun initializeUserRepo() {
        declareMock<UserRepository> {
            every { userState } returns MutableStateFlow(
                UserState.Registered(
                    User(
                        "myIdentifier",
                        User.UserType.REGISTERED,
                        "email"
                    ),
                    ProfileState.Present(
                        Profile(
                            "Username",
                            "",
                            0L
                        )
                    )
                )
            )
        }
    }

    @Test
    fun `fetchTransactions sets loading state when triggered from error state`() = runBlocking {
        declareMock<TransactionRepository> {
            coEvery { getWithdrawals() } returns Result.success(listOf())
            coEvery { getTransactions() } returns Result.failure(Exception())
        }

        val viewModel = TransactionsViewModel()
        delay(200)
        val result = mutableListOf<TransactionState>()
        scope.launch { viewModel.state.collect { result.add(it) } }
        viewModel.perform(TransactionAction.RELOAD)
        delay(200)

        val expected =
            listOf(TransactionState.Error, TransactionState.Loading, TransactionState.Error)
        assertEquals(expected, result)
    }

    @Test
    fun `fetchTransactions sets reloading state when triggered from base state`() = runBlocking {
        declareMock<TransactionRepository> {
            coEvery { getWithdrawals() } returns Result.success(listOf())
            coEvery { getTransactions() } returns Result.success(listOf())
        }

        val viewModel = TransactionsViewModel()
        delay(200)
        val result = mutableListOf<TransactionState>()
        scope.launch { viewModel.state.collect { result.add(it) } }
        viewModel.perform(TransactionAction.RELOAD)
        delay(200)

        assertEquals(true, result[0] is TransactionState.Base)
        assertEquals(true, result[1] is TransactionState.Reloading)
        assertEquals(true, result[2] is TransactionState.Base)
    }

    @Test
    fun `Withdrawal to WithdrawalItem translation`() = runBlocking {
        declareMock<TransactionRepository> {
            coEvery { getWithdrawals() } returns Result.success(withdrawals)
            coEvery { getTransactions() } returns Result.success(transactions)
        }
        val viewModel = TransactionsViewModel()
        delay(200)
        val result = viewModel.state.value

        assertEquals(true, result is TransactionState.Base)
        val baseState = result as TransactionState.Base
        val item = baseState.transactions.first() as TransactionState.TransactionItem.WithdrawalItem

        assertEquals("0", item.id)
        assertEquals(69420L, item.amount)
        assertEquals(Instant.fromEpochSeconds(7), item.timestamp)
    }

    @Test
    fun `Transaction to incoming DonationItem translation`() = runBlocking {
        declareMock<TransactionRepository> {
            coEvery { getWithdrawals() } returns Result.success(withdrawals)
            coEvery { getTransactions() } returns Result.success(transactions)
        }
        val viewModel = TransactionsViewModel()
        delay(200)
        val result = viewModel.state.value

        assertEquals(true, result is TransactionState.Base)
        val baseState = result as TransactionState.Base
        val item = baseState.transactions[1] as TransactionState.TransactionItem.DonationItem

        assertEquals("1", item.id)
        assertEquals(
            TransactionState.TransactionItem.DonationItem.Direction.INCOMING,
            item.direction
        )
        assertEquals("transactions_item_title_incoming+anon+5 €", item.title)
        assertEquals(200L, item.fees)
        assertEquals(Transaction.State.PENDING, item.state)
        assertEquals(Instant.fromEpochSeconds(6), item.timestamp)
    }

    @Test
    fun `Transaction to outgoing DonationItem translation`() = runBlocking {
        declareMock<TransactionRepository> {
            coEvery { getWithdrawals() } returns Result.success(withdrawals)
            coEvery { getTransactions() } returns Result.success(transactions)
        }
        val viewModel = TransactionsViewModel()
        delay(200)
        val result = viewModel.state.value

        assertEquals(true, result is TransactionState.Base)
        val baseState = result as TransactionState.Base
        val item = baseState.transactions[4] as TransactionState.TransactionItem.DonationItem

        assertEquals(
            TransactionState.TransactionItem.DonationItem.Direction.OUTGOING,
            item.direction
        )
        assertEquals("transactions_item_title_outgoing+5 €+Recipient", item.title)
    }

    @Test
    fun `Item list gets sorted by descending date`() = runBlocking {
        declareMock<TransactionRepository> {
            coEvery { getWithdrawals() } returns Result.success(withdrawals)
            coEvery { getTransactions() } returns Result.success(transactions)
        }
        val viewModel = TransactionsViewModel()
        delay(200)
        val result = viewModel.state.value

        assertEquals(true, result is TransactionState.Base)
        val baseState = result as TransactionState.Base

        assertEquals(listOf("0", "1", "2", "3", "4", "5"), baseState.transactions.map { it.id })
    }
}
