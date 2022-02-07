package de.paulweber.spenderino.model.repositories.user

import kotlinx.serialization.Serializable

@Serializable
data class Profile(val username: String, val recipientCode: String, val balance: Long)
