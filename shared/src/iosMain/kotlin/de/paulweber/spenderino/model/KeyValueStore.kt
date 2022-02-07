package de.paulweber.spenderino.model

import platform.Foundation.NSUserDefaults

actual class KeyValueStore actual constructor() : KeyValueStoring {
    private val userDefaults = NSUserDefaults.standardUserDefaults

    override fun string(forKey: String): String? {
        return userDefaults.stringForKey(forKey)
    }

    override fun store(string: String?, forKey: String) {
        userDefaults.setObject(string, forKey)
    }
}
