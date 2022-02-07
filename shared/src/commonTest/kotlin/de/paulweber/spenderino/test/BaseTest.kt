package de.paulweber.spenderino.test

import co.touchlab.kermit.CommonWriter
import co.touchlab.kermit.Logger
import io.ktor.client.features.ClientRequestException
import io.ktor.http.HttpStatusCode
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkClass
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest
import org.koin.test.mock.MockProvider
import kotlin.test.AfterTest
import kotlin.test.BeforeTest

@OptIn(ExperimentalCoroutinesApi::class)
open class BaseTest : KoinTest {
    protected val scope = CoroutineScope(Dispatchers.Unconfined)

    @BeforeTest
    fun initKoin() {
        startKoin {
            MockProvider.register { mockkClass(it) }
        }
    }

    @BeforeTest
    fun coroutineSetup() {
        Dispatchers.setMain(Dispatchers.Unconfined)
    }

    @BeforeTest
    fun loggerSetup() {
        Logger.setLogWriters(listOf(CommonWriter()))
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
        stopKoin()
    }

    protected fun createMockedClientException(statusCode: HttpStatusCode): ClientRequestException {
        return mockk(relaxed = true) {
            every { response } returns mockk() { every { status } returns statusCode }
        }
    }
}
