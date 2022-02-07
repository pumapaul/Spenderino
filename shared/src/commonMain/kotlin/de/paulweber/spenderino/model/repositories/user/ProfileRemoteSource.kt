package de.paulweber.spenderino.model.repositories.user

import de.paulweber.spenderino.model.networking.BASE_URL
import de.paulweber.spenderino.model.repositories.RemoteSource
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.http.ContentType
import io.ktor.http.contentType

interface ProfileRemoteSource {
    suspend fun getProfile(): Result<Profile>
    suspend fun createProfile(username: String): Result<Profile>
    suspend fun getQRCode(): Result<ByteArray>
}

class ProfileRemoteSourceImpl : RemoteSource(), ProfileRemoteSource {
    override suspend fun getProfile(): Result<Profile> {
        return runCatching {
            client.get("$BASE_URL/profile")
        }
    }

    override suspend fun createProfile(username: String): Result<Profile> {
        return runCatching {
            client.post("$BASE_URL/profile/create") {
                contentType(ContentType.Application.Json)
                body = CreateProfileParameters(username)
            }
        }
    }

    override suspend fun getQRCode(): Result<ByteArray> {
        return runCatching {
            client.get("$BASE_URL/profile/qrcode")
        }
    }
}
