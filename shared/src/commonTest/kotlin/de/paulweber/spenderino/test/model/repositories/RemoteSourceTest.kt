package de.paulweber.spenderino.test.model.repositories

import de.paulweber.spenderino.test.BaseTest
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.MockRequestHandler
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.serializer.KotlinxSerializer
import org.koin.dsl.module

open class RemoteSourceTest : BaseTest() {
    protected fun loadMockClient(handler: MockRequestHandler) {
        val koinModule = module {
            single {
                createMockClient(handler)
            }
        }
        getKoin().loadModules(listOf(koinModule))
    }

    private fun createMockClient(handler: MockRequestHandler): HttpClient {
        return HttpClient(MockEngine) {
            install(JsonFeature) {
                serializer = KotlinxSerializer()
            }
            engine {
                addHandler(handler)
            }
        }
    }
}
