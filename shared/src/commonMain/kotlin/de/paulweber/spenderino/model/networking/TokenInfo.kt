package de.paulweber.spenderino.model.networking

import de.paulweber.spenderino.model.repositories.user.User
import io.ktor.client.features.auth.providers.BearerTokens
import kotlinx.serialization.Serializable

@Serializable
data class TokenInfo(
    val identifier: String,
    val userType: User.UserType,
    val email: String?,
    val accessToken: String,
    val refreshToken: String?
) {
    fun toBearerToken(): BearerTokens {
        return BearerTokens(accessToken, refreshToken ?: "")
    }
}
