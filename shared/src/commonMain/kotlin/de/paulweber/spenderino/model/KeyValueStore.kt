package de.paulweber.spenderino.model

interface KeyValueStoring {
    fun string(forKey: String): String?
    fun store(string: String?, forKey: String)
}

expect class KeyValueStore() : KeyValueStoring
