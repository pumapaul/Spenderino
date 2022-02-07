package de.paulweber.spenderino.model.repositories.transaction

import de.paulweber.spenderino.model.networking.BASE_URL
import de.paulweber.spenderino.model.repositories.RemoteSource
import de.paulweber.spenderino.model.repositories.balance.Withdrawal
import io.ktor.client.request.get

interface TransactionRemoteSource {
    suspend fun getTransactions(): Result<List<Transaction>>
    suspend fun getWithdrawals(): Result<List<Withdrawal>>
}

class TransactionRemoteSourceImpl : RemoteSource(), TransactionRemoteSource {
    override suspend fun getTransactions(): Result<List<Transaction>> {
        return runCatching { client.get("$BASE_URL/transactions") }
    }

    override suspend fun getWithdrawals(): Result<List<Withdrawal>> {
        return runCatching { client.get("$BASE_URL/withdrawals") }
    }
}
