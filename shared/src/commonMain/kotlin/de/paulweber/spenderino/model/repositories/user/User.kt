package de.paulweber.spenderino.model.repositories.user

import de.paulweber.spenderino.model.networking.TokenInfo
import kotlinx.serialization.Serializable

data class User(val identifier: String, val userType: UserType, val email: String?) {

    constructor(tokenInfo: TokenInfo) : this(
        tokenInfo.identifier,
        tokenInfo.userType,
        tokenInfo.email
    )

    @Serializable
    enum class UserType {
        ANONYMOUS, REGISTERED
    }
}
