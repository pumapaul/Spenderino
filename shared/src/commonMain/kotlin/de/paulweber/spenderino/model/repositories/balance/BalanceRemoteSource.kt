package de.paulweber.spenderino.model.repositories.balance

import de.paulweber.spenderino.model.networking.BASE_URL
import de.paulweber.spenderino.model.repositories.RemoteSource
import io.ktor.client.request.get
import io.ktor.client.request.post

interface BalanceRemoteSource {
    suspend fun getBalance(): Result<Balance>
    suspend fun createWithdrawal(amount: Long): Result<Withdrawal>
    suspend fun cancelWithdrawal(): Result<Unit>
    suspend fun getQrCode(): Result<ByteArray>
    suspend fun pollWithdrawal(): Result<Boolean>
}

class BalanceRemoteSourceImpl : RemoteSource(), BalanceRemoteSource {
    override suspend fun getBalance(): Result<Balance> {
        return runCatching {
            client.get("$BASE_URL/balance")
        }
    }

    override suspend fun createWithdrawal(amount: Long): Result<Withdrawal> {
        return runCatching {
            client.post("$BASE_URL/withdrawal/create/$amount")
        }
    }

    override suspend fun cancelWithdrawal(): Result<Unit> {
        return runCatching {
            client.post("$BASE_URL/withdrawal/cancel")
        }
    }

    override suspend fun getQrCode(): Result<ByteArray> {
        return runCatching {
            client.get("$BASE_URL/withdrawal/qrcode")
        }
    }

    override suspend fun pollWithdrawal(): Result<Boolean> {
        return runCatching {
            client.get("$BASE_URL/withdrawal")
        }
    }
}
