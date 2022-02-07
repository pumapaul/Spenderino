package de.paulweber.spenderino.model.repositories.user

import kotlinx.serialization.Serializable

@Serializable
data class CreateProfileParameters(val username: String)
