package de.paulweber.spenderino.model

import android.content.SharedPreferences
import org.koin.core.component.KoinComponent

actual class KeyValueStore actual constructor() : KeyValueStoring, KoinComponent {
    lateinit var sharedPreferences: SharedPreferences

    override fun string(forKey: String): String? {
        return sharedPreferences.getString(forKey, null)
    }

    override fun store(string: String?, forKey: String) {
        sharedPreferences.edit()
            .putString(forKey, string)
            .apply()
    }
}
