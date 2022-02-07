package de.paulweber.spenderino.model.repositories.donation

import kotlinx.serialization.Serializable

@Serializable
data class DonationUpdateSumParameters(val paymentIntentId: String, val newSum: Long, val newDonationValue: Long)
