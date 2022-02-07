package de.paulweber.spenderino.model.networking

import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.okhttp.OkHttp

actual val defaultPlatformEngine: HttpClientEngine = OkHttp.create()
