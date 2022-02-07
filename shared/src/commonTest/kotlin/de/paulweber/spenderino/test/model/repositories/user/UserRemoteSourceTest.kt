@file:Suppress("IllegalIdentifier")

package de.paulweber.spenderino.test.model.repositories.user

import de.paulweber.spenderino.model.networking.BASE_URL
import de.paulweber.spenderino.model.networking.TokenInfo
import de.paulweber.spenderino.model.repositories.user.User
import de.paulweber.spenderino.model.repositories.user.UserRemoteSourceImpl
import de.paulweber.spenderino.test.model.repositories.RemoteSourceTest
import io.ktor.client.engine.mock.respond
import io.ktor.client.engine.mock.toByteArray
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals

class UserRemoteSourceTest : RemoteSourceTest() {
    @Suppress("MaxLineLength")
    private val accessToken = "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJpZGVudGlmaWVyIjoiNjYwMTc3NGEtZTkxNy00ZDliLTk1YWQtZTI1YWI0MjcyYjEwIiwiaXNzIjoiaHR0cHM6Ly9zcGVuZGVyaW5vLmhlcm9rdWFwcC5jb20vIiwidXNlclR5cGUiOiJSRUdJU1RFUkVEIiwiZXhwIjoxNjQzOTk1OTU4fQ.B7Iprl28cPZgwM8yifQqEAlHYP58WmX4wq1zakfxh5Tu9qU_hp53OHGwIiEbwXyWJ82XnpoHb6hvwfPHCYNovQ"

    private val tokenInfoString = """
        {
          "identifier": "6601774a-e917-4d9b-95ad-e25ab4272b10",
          "userType": "REGISTERED",
          "email": "Test",
          "accessToken": "$accessToken",
          "refreshToken": "afed7a91-6ac4-4a94-853a-beee44c5b3d4"
        }
    """.trimIndent()
    private val tokenInfo = TokenInfo(
        "6601774a-e917-4d9b-95ad-e25ab4272b10",
        User.UserType.REGISTERED,
        "Test",
        accessToken,
        "afed7a91-6ac4-4a94-853a-beee44c5b3d4"
    )

    @Test
    fun `triggerAuthorization fires a request against BASE_URL`() = runBlocking {
        loadMockClient {
            respond("", HttpStatusCode.OK)
        }
        val remoteSource = UserRemoteSourceImpl()
        val result = remoteSource.triggerAuthorization()

        assertEquals(true, result.isSuccess)
    }

    @Test
    fun `login serializes parameters and returns TokenInfo`() = runBlocking {
        loadMockClient {
            val isCorrectUrl = it.url.toString() == "$BASE_URL/user/login"
            val isContentTypeJson = it.body.contentType == ContentType.Application.Json
            val expectedContent = """{"email":"email@email.de","password":"secretPassword"}"""
                .encodeToByteArray()
            val isContentEncodedCorrectly = it.body.toByteArray()
                .contentEquals(expectedContent)
            if (isCorrectUrl && isContentTypeJson && isContentEncodedCorrectly) {
                respond(
                    tokenInfoString,
                    HttpStatusCode.OK,
                    headers = headersOf(HttpHeaders.ContentType, "application/json")
                )
            } else respond("", HttpStatusCode.BadRequest)
        }

        val remoteSource = UserRemoteSourceImpl()
        val result = remoteSource.login("email@email.de", "secretPassword")

        assertEquals(true, result.isSuccess)
        assertEquals(
            tokenInfo,
            result.getOrThrow()
        )
    }

    @Test
    fun `register serializes parameters and returns TokenInfo`() = runBlocking {
        loadMockClient {
            val isCorrectUrl = it.url.toString() == "$BASE_URL/user/register/email"
            val isContentTypeJson = it.body.contentType == ContentType.Application.Json
            val expectedContent = """{"email":"email@email.de","password":"secretPassword"}"""
                .encodeToByteArray()
            val isContentEncodedCorrectly = it.body.toByteArray()
                .contentEquals(expectedContent)
            if (isCorrectUrl && isContentTypeJson && isContentEncodedCorrectly) {
                respond(
                    tokenInfoString,
                    HttpStatusCode.OK,
                    headers = headersOf(HttpHeaders.ContentType, "application/json")
                )
            } else respond("", HttpStatusCode.BadRequest)
        }

        val remoteSource = UserRemoteSourceImpl()
        val result = remoteSource.register("email@email.de", "secretPassword")

        assertEquals(true, result.isSuccess)
        assertEquals(
            tokenInfo,
            result.getOrThrow()
        )
    }
}
