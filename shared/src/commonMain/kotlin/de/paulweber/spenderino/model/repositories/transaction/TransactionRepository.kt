package de.paulweber.spenderino.model.repositories.transaction

import de.paulweber.spenderino.model.repositories.balance.Withdrawal
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

interface TransactionRepository {
    suspend fun getTransactions(): Result<List<Transaction>>
    suspend fun getWithdrawals(): Result<List<Withdrawal>>
}

class TransactionRepositoryImpl : TransactionRepository, KoinComponent {
    private val remoteSource: TransactionRemoteSource by inject()

    override suspend fun getTransactions(): Result<List<Transaction>> {
        return remoteSource.getTransactions()
    }

    override suspend fun getWithdrawals(): Result<List<Withdrawal>> {
        return remoteSource.getWithdrawals()
    }
}
