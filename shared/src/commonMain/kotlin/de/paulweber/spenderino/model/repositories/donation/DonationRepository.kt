package de.paulweber.spenderino.model.repositories.donation

import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

interface DonationRepository {
    suspend fun getPaymentIntentClientSecret(receiverId: String): Result<DonationInformation>
    suspend fun updateDonationSum(paymentIntentId: String, newValue: Long, newWithoutFees: Long): Result<Unit>
}

class DonationRepositoryImpl : DonationRepository, KoinComponent {
    private val remoteSource: DonationRemoteSource by inject()

    override suspend fun getPaymentIntentClientSecret(receiverId: String): Result<DonationInformation> {
        return remoteSource.getPaymentIntentClientSecret(receiverId)
    }

    override suspend fun updateDonationSum(paymentIntentId: String, newValue: Long, newWithoutFees: Long): Result<Unit> {
        return remoteSource.updateDonationSum(paymentIntentId, newValue, newWithoutFees)
    }
}
