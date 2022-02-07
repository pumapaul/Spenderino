package de.paulweber.spenderino.viewmodel

import de.paulweber.spenderino.model.repositories.balance.Withdrawal
import de.paulweber.spenderino.model.repositories.transaction.Transaction
import de.paulweber.spenderino.model.repositories.transaction.TransactionRepository
import de.paulweber.spenderino.model.repositories.user.User
import de.paulweber.spenderino.model.repositories.user.UserRepository
import de.paulweber.spenderino.model.repositories.user.UserState
import de.paulweber.spenderino.utility.L10n
import de.paulweber.spenderino.utility.toEuroString
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.koin.core.component.inject

sealed class TransactionState {
    object Error : TransactionState()
    object Loading : TransactionState()
    data class Reloading(val transactions: List<TransactionItem>) : TransactionState()
    data class Base(val transactions: List<TransactionItem>) : TransactionState()

    sealed class TransactionItem {
        abstract val id: String

        data class WithdrawalItem(
            override val id: String,
            val amount: Long,
            val text: String,
            val timestamp: Instant
        ) :
            TransactionItem()

        data class DonationItem(
            override val id: String,
            val timestampString: String,
            val direction: Direction,
            val title: String,
            val fees: Long,
            val state: Transaction.State,
            val timestamp: Instant
        ) : TransactionItem() {
            enum class Direction { INCOMING, OUTGOING }
        }
    }
}

enum class TransactionAction {
    RELOAD
}

class TransactionsViewModel(
    state: TransactionState = TransactionState.Loading,
    onBack: () -> Unit = {}
) :
    ViewModel<TransactionAction, Unit, TransactionState>(state, onBack = onBack) {

    private val userRepo: UserRepository by inject()
    private val transactionRepo: TransactionRepository by inject()

    private val currentUser = MutableStateFlow<User?>(null)

    init {
        scope.launch {
            userRepo.userState
                .map {
                    when (it) {
                        is UserState.Anonymous -> it.user
                        is UserState.Registered -> it.user
                        else -> null
                    }
                }
                .collect { currentUser.value = it }
        }
        fetchTransactions()
    }

    private fun setLoadingState() {
        val loadingState = if (state.value is TransactionState.Base) {
            TransactionState.Reloading((state.value as TransactionState.Base).transactions)
        } else TransactionState.Loading
        setState(loadingState)
    }

    private fun fetchTransactions() = scope.launch {
        setLoadingState()

        val withdrawals = transactionRepo.getWithdrawals()
            .getOrDefault(listOf())
            .filter { it.timestamp != null }
        transactionRepo.getTransactions().fold(
            onSuccess = { donations ->
                val donationItems = donations.mapNotNull { createDonationItem(it) }
                val withdrawalItems = withdrawals.map { createWithdrawalItem(it) }

                val items = mutableListOf<TransactionState.TransactionItem>()
                items.addAll(donationItems)
                items.addAll(withdrawalItems)
                val sorted = items.sortedByDescending {
                    when (it) {
                        is TransactionState.TransactionItem.DonationItem -> it.timestamp
                        is TransactionState.TransactionItem.WithdrawalItem -> it.timestamp
                    }
                }
                val newState = TransactionState.Base(sorted)
                setState(newState)
            },
            onFailure = { setState(TransactionState.Error) }
        )
    }

    override fun perform(action: TransactionAction) {
        when (action) {
            TransactionAction.RELOAD -> fetchTransactions()
        }
    }

    private fun createDonationItem(
        donation: Transaction
    ): TransactionState.TransactionItem.DonationItem? {
        return currentUser.value?.let { user ->
            val direction = if (user.identifier == donation.donator.id) {
                TransactionState.TransactionItem.DonationItem.Direction.OUTGOING
            } else {
                TransactionState.TransactionItem.DonationItem.Direction.INCOMING
            }
            val fees = donation.amountWithFees - donation.amount

            TransactionState.TransactionItem.DonationItem(
                id = donation.paymentIntentId,
                direction = direction,
                timestampString = donation.timestamp.toShortDateTime(),
                title = createDonationTitle(donation, direction),
                fees = fees,
                state = donation.state,
                timestamp = donation.timestamp
            )
        }
    }

    private fun createWithdrawalItem(
        withdrawal: Withdrawal
    ): TransactionState.TransactionItem.WithdrawalItem {
        return TransactionState.TransactionItem.WithdrawalItem(
            withdrawal.id,
            withdrawal.amount,
            getPastWithdrawalText(withdrawal),
            timestamp = withdrawal.timestamp!!
        )
    }

    private fun createDonationTitle(
        transaction: Transaction,
        direction: TransactionState.TransactionItem.DonationItem.Direction
    ): String {
        return when (direction) {
            TransactionState.TransactionItem.DonationItem.Direction.INCOMING -> {
                val donator = transaction.donator.username ?: L10n.get("anon")
                L10n.format(
                    "transactions_item_title_incoming",
                    donator,
                    transaction.amount.toEuroString()
                )
            }
            TransactionState.TransactionItem.DonationItem.Direction.OUTGOING -> {
                val recipient = transaction.recipient.username ?: L10n.get("anon")
                L10n.format(
                    "transactions_item_title_outgoing",
                    transaction.amount.toEuroString(),
                    recipient
                )
            }
        }
    }

    private fun getPastWithdrawalText(withdrawal: Withdrawal): String {
        val dateTime = withdrawal.timestamp!!.toLocalDateTime(TimeZone.currentSystemDefault())
        val date = dateTime.date
        val hours = if (dateTime.hour > 9) "${dateTime.hour}" else "0${dateTime.hour}"
        val minutes = if (dateTime.minute > 9) "${dateTime.minute}" else "0${dateTime.minute}"
        val time = "$hours:$minutes"
        return L10n.format("balance_past_withdrawal", date, time)
    }
}

fun Instant.toShortDateTime(): String {
    val dateTime = this.toLocalDateTime(TimeZone.currentSystemDefault())
    val date = dateTime.date
    val hours = if (dateTime.hour > 9) "${dateTime.hour}" else "0${dateTime.hour}"
    val minutes = if (dateTime.minute > 9) "${dateTime.minute}" else "0${dateTime.minute}"
    val time = "$hours:$minutes"
    return "$date, $time"
}
