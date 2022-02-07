package de.paulweber.spenderino.model.repositories.balance

import kotlinx.serialization.Serializable

@Serializable
data class Balance(val amount: Long, val withdrawal: Withdrawal?, val pastWithdrawals: List<Withdrawal>)
