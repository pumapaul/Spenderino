package de.paulweber.spenderino.model.repositories.user

import de.paulweber.spenderino.model.networking.TokenInfo
import de.paulweber.spenderino.model.networking.BASE_URL
import de.paulweber.spenderino.model.repositories.RemoteSource
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.http.ContentType
import io.ktor.http.contentType

interface UserRemoteSource {
    suspend fun triggerAuthorization(): Result<Unit>
    suspend fun login(email: String, password: String): Result<TokenInfo>
    suspend fun register(email: String, password: String): Result<TokenInfo>
}

class UserRemoteSourceImpl : RemoteSource(), UserRemoteSource {
    override suspend fun triggerAuthorization(): Result<Unit> {
        return runCatching { client.get(BASE_URL) }
    }

    override suspend fun login(email: String, password: String): Result<TokenInfo> {
        val parameters = LoginParameters(email, password)
        return runCatching {
            client.post("$BASE_URL/user/login") {
                contentType(ContentType.Application.Json)
                body = parameters
            }
        }
    }

    override suspend fun register(email: String, password: String): Result<TokenInfo> {
        val parameters = RegisterParameters(email, password)
        return runCatching {
            client.post("$BASE_URL/user/register/email") {
                contentType(ContentType.Application.Json)
                body = parameters
            }
        }
    }
}
