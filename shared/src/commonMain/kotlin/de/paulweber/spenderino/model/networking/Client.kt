package de.paulweber.spenderino.model.networking

import de.paulweber.spenderino.utility.BuildKonfig
import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.features.ClientRequestException
import io.ktor.client.features.auth.Auth
import io.ktor.client.features.auth.providers.BearerTokens
import io.ktor.client.features.auth.providers.bearer
import io.ktor.client.features.get
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.serializer.KotlinxSerializer
import io.ktor.client.features.logging.Logging
import io.ktor.client.request.post
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType

expect val defaultPlatformEngine: HttpClientEngine

private fun getDefaultPlatformEngine(): HttpClientEngine {
    return defaultPlatformEngine
}

private val tokenClient = HttpClient(getDefaultPlatformEngine()) {
    install(JsonFeature) {
        serializer = KotlinxSerializer()
    }
    install(Logging)
}

val tokenStore: TokenStore by lazy { TokenStore() }

val BASE_URL = BuildKonfig.BASE_URL

val client = HttpClient(getDefaultPlatformEngine()) {

    install(JsonFeature) {
        serializer = KotlinxSerializer()
    }
    install(Logging)

    install(Auth) {
        bearer {
            loadTokens {
                tokenStore.token.value?.toBearerToken() ?: registerAnonymous()
            }

            refreshTokens {
                refreshToken(it)
            }
        }
    }
}

private suspend fun registerAnonymous(): BearerTokens? {
    val result: Result<TokenInfo> =
        runCatching { tokenClient.post("$BASE_URL/user/register/anonymous") }
    return result.fold(
        onSuccess = {
            tokenStore.setNewToken(it)
            it.toBearerToken()
        },
        onFailure = {
            null
        }
    )
}

private suspend fun refreshToken(response: HttpResponse): BearerTokens? {
    return tokenStore.token.value?.let { token ->
        token.refreshToken?.let {
            val result: Result<TokenInfo> = runCatching {
                tokenClient.post("$BASE_URL/user/refresh") {
                    contentType(ContentType.Application.Json)
                    body = RefreshParameters(token.identifier, it)
                }
            }
            result.fold(
                onSuccess = {
                    tokenStore.setNewToken(it)
                    it.toBearerToken()
                },
                onFailure = {
                    if (it is ClientRequestException && it.response.status == HttpStatusCode.Unauthorized) {
                        registerAnonymous()
                    } else {
                        println("Error during token refresh: $it")
                        null
                    }
                }
            )
        } ?: run {
            if (response.headers["WWW-Authenticate"]?.contains("anonymous") == true) {
                registerAnonymous()
            } else null
        }
    }
}
