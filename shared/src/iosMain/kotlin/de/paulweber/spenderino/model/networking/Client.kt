package de.paulweber.spenderino.model.networking

import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.ios.Ios

actual val defaultPlatformEngine: HttpClientEngine = Ios.create()
