package de.paulweber.spenderino

import android.content.Context
import de.paulweber.spenderino.model.KeyValueStore
import de.paulweber.spenderino.model.KeyValueStoring
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

actual val platformModule = module {
    single<KeyValueStoring> {
        KeyValueStore().apply {
            this.sharedPreferences = androidContext()
                .getSharedPreferences(
                    "de.paulweber.spenderino.android.SharedPreferences",
                    Context.MODE_PRIVATE
                )
        }
    }
}
