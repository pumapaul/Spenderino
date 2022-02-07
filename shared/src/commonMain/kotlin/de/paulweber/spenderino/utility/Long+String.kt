package de.paulweber.spenderino.utility

fun Long.toEuroString(): String {
    val wholePart = this / 100
    val decimalPart = this % 100
    return when {
        decimalPart == 0L -> "$wholePart €"
        decimalPart < 10L -> "$wholePart,0$decimalPart €"
        else -> "$wholePart,$decimalPart €"
    }
}
