package de.paulweber.spenderino.model.repositories.transaction

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class Transaction(
    val amount: Long,
    val amountWithFees: Long,
    val paymentIntentId: String,
    val donator: Entity,
    val recipient: Entity,
    val timestamp: Instant,
    val state: State
) {
    @Serializable
    data class Entity(val username: String?, val id: String)

    enum class State { PENDING, FAILED, COMPLETE }
}
