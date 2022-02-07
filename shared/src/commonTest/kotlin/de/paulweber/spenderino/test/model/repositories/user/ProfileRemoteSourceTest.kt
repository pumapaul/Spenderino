@file:Suppress("IllegalIdentifier")

package de.paulweber.spenderino.test.model.repositories.user

import de.paulweber.spenderino.model.networking.BASE_URL
import de.paulweber.spenderino.model.repositories.user.Profile
import de.paulweber.spenderino.model.repositories.user.ProfileRemoteSourceImpl
import de.paulweber.spenderino.test.model.repositories.RemoteSourceTest
import io.ktor.client.engine.mock.respond
import io.ktor.client.engine.mock.toByteArray
import io.ktor.client.features.ClientRequestException
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals

class ProfileRemoteSourceTest : RemoteSourceTest() {
    private val profileJson = """{
            |"username": "Test",
            |"recipientCode": "https://spenderino.herokuapp.com/r/ece26ee2-d228-4084-a147-c3f8642e3798",
            |"balance": 0
            |}""".trimMargin()

    @Test
    fun `getProfile parses profile json correctly`() = runBlocking {
        loadMockClient { request ->
            if (request.url.toString() == "$BASE_URL/profile") {
                respond(
                    profileJson,
                    HttpStatusCode.OK,
                    headers = headersOf(HttpHeaders.ContentType, "application/json")
                )
            } else respond("", HttpStatusCode.BadRequest)
        }

        val remoteSource = ProfileRemoteSourceImpl()
        val result = remoteSource.getProfile()

        assertEquals(true, result.isSuccess)
        assertEquals(
            Profile(
                "Test",
                "https://spenderino.herokuapp.com/r/ece26ee2-d228-4084-a147-c3f8642e3798",
                0
            ),
            result.getOrThrow()
        )
    }

    @Test
    fun `createProfile properly calls and parses resulting profile`() = runBlocking {
        loadMockClient { request ->
            val isCorrectUrl = request.url.toString() == "$BASE_URL/profile/create"
            val isContentTypeJson = request.body.contentType == ContentType.Application.Json
            val expectedContent = """{"username":"Testusername"}""".encodeToByteArray()
            val isContentEncodedCorrectly = request.body.toByteArray()
                .contentEquals(expectedContent)
            if (isCorrectUrl && isContentTypeJson && isContentEncodedCorrectly) {
                respond(
                    profileJson,
                    HttpStatusCode.OK,
                    headers = headersOf(HttpHeaders.ContentType, "application/json")
                )
            } else respond("", HttpStatusCode.BadRequest)
        }

        val remoteSource = ProfileRemoteSourceImpl()
        val result = remoteSource.createProfile("Testusername")

        assertEquals(true, result.isSuccess)
        assertEquals(
            Profile(
                "Test",
                "https://spenderino.herokuapp.com/r/ece26ee2-d228-4084-a147-c3f8642e3798",
                0
            ),
            result.getOrThrow()
        )
    }

    @Test
    fun `getQRCode returns failing result on 404`() = runBlocking {
        loadMockClient {
            respond("", HttpStatusCode.NotFound)
        }

        val remoteSource = ProfileRemoteSourceImpl()
        val result = remoteSource.getQRCode()

        assertEquals(true, result.isFailure)
        assertEquals(true, result.exceptionOrNull() is ClientRequestException)
        val clientRequestException = result.exceptionOrNull() as ClientRequestException
        assertEquals(
            true,
            clientRequestException.response.status == HttpStatusCode.NotFound
        )
    }
}
