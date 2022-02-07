package de.paulweber.spenderino.model.repositories.donation

import de.paulweber.spenderino.model.networking.BASE_URL
import de.paulweber.spenderino.model.repositories.RemoteSource
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.http.ContentType
import io.ktor.http.contentType

interface DonationRemoteSource {
    suspend fun getPaymentIntentClientSecret(receiverId: String): Result<DonationInformation>
    suspend fun updateDonationSum(paymentIntentId: String, newValue: Long, newWithoutFees: Long): Result<Unit>
}

class DonationRemoteSourceImpl : RemoteSource(), DonationRemoteSource {
    override suspend fun getPaymentIntentClientSecret(receiverId: String): Result<DonationInformation> {
        return runCatching {
            client.get("$BASE_URL/r/$receiverId")
        }
    }

    override suspend fun updateDonationSum(paymentIntentId: String, newValue: Long, newWithoutFees: Long): Result<Unit> {
        return runCatching {
            val params = DonationUpdateSumParameters(paymentIntentId, newValue, newWithoutFees)
            client.post("$BASE_URL/donation/change-sum") {
                contentType(ContentType.Application.Json)
                body = params
            }
        }
    }
}
