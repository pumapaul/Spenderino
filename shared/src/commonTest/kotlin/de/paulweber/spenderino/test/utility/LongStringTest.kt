@file:Suppress("IllegalIdentifier")

package de.paulweber.spenderino.test.utility

import de.paulweber.spenderino.utility.toEuroString
import kotlin.test.Test
import kotlin.test.assertEquals

class LongStringTest {
    @Test
    fun `toEuroString drops decimal part correctly`() {
        val input = 100L
        val target = "1 €"

        val output = input.toEuroString()

        assertEquals(target, output)
    }

    @Test
    fun `toEuroString formats decimals correctly with leading zero`() {
        val input = 101L
        val target = "1,01 €"

        val output = input.toEuroString()

        assertEquals(target, output)
    }

    @Test
    fun `toEuroString formats correctly when decimal part has no leading zero`() {
        val input = 110L
        val target = "1,10 €"

        val output = input.toEuroString()

        assertEquals(target, output)
    }
}
