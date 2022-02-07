package de.paulweber.spenderino.model.networking

import kotlinx.serialization.Serializable

@Serializable
data class RefreshParameters(val identifier: String, val refreshToken: String)
