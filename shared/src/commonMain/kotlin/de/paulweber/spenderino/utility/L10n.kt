package de.paulweber.spenderino.utility

expect object L10n {
    fun get(id: String, quantity: Int): String
    fun get(id: String): String
    fun format(id: String, vararg formatArgs: Any): String
}
