package de.paulweber.spenderino.model.repositories.balance

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class Withdrawal(val id: String, val beneficiary: String, val amount: Long, val timestamp: Instant?)
