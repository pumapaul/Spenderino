package de.paulweber.spenderino.model.repositories.balance

import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

interface BalanceRepository {
    suspend fun getBalance(): Result<Balance>
    suspend fun createWithdrawal(amount: Long): Result<Withdrawal>
    suspend fun cancelWithdrawal(): Result<Unit>
    suspend fun getQrCode(): Result<ByteArray>
    suspend fun pollIsWithdrawalActive(): Result<Boolean>
}

class BalanceRepositoryImpl : BalanceRepository, KoinComponent {

    private val remoteSource: BalanceRemoteSource by inject()

    override suspend fun getBalance(): Result<Balance> {
        return remoteSource.getBalance()
    }

    override suspend fun createWithdrawal(amount: Long): Result<Withdrawal> {
        return remoteSource.createWithdrawal(amount)
    }

    override suspend fun cancelWithdrawal(): Result<Unit> {
        return remoteSource.cancelWithdrawal()
    }

    override suspend fun getQrCode(): Result<ByteArray> {
        return remoteSource.getQrCode()
    }

    override suspend fun pollIsWithdrawalActive(): Result<Boolean> {
        return remoteSource.pollWithdrawal()
    }
}
