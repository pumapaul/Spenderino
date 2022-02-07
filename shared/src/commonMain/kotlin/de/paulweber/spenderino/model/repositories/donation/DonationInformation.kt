package de.paulweber.spenderino.model.repositories.donation

import kotlinx.serialization.Serializable

@Serializable
data class DonationInformation(
    val recipient: Recipient,
    val paymentSecret: String,
    val paymentIntentId: String,
    val customerId: String,
    val customerSecret: String
)
