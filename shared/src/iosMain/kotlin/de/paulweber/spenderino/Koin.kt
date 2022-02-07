package de.paulweber.spenderino

import de.paulweber.spenderino.model.KeyValueStore
import de.paulweber.spenderino.model.KeyValueStoring
import org.koin.dsl.module

actual val platformModule = module {
    single<KeyValueStoring> { KeyValueStore() }
}
