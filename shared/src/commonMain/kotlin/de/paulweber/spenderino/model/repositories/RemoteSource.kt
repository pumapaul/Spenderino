package de.paulweber.spenderino.model.repositories

import io.ktor.client.HttpClient
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

abstract class RemoteSource : KoinComponent {
    protected val client: HttpClient by inject()
}
