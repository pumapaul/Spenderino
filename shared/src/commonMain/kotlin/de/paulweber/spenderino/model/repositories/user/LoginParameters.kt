package de.paulweber.spenderino.model.repositories.user

import kotlinx.serialization.Serializable

@Serializable
data class LoginParameters(val email: String, val password: String)
