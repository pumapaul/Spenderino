package de.paulweber.spenderino.utility

import io.ktor.utils.io.core.Closeable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

fun <T> StateFlow<T>.wrap(): CFlow<T> = CFlow(this)

class CFlow<T>(private val origin: StateFlow<T>) : StateFlow<T> by origin {
    val currentValue: T
        get() = origin.value

    fun watchOnMain(block: (T) -> Unit): Closeable {
        val job = Job()

        onEach {
            block(it)
        }.launchIn(CoroutineScope(Dispatchers.Main + job))

        return object : Closeable {
            override fun close() {
                job.cancel()
            }
        }
    }
}
