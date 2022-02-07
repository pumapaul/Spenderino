@file:Suppress("IllegalIdentifier")

package de.paulweber.spenderino.test.utility

import de.paulweber.spenderino.utility.wrap
import de.paulweber.spenderino.test.BaseTest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlin.test.Test
import kotlin.test.assertEquals

class CFlowTest : BaseTest() {
    @Test
    fun `CFlow exposes correct currentValue`() {
        val stateFlow = MutableStateFlow("validation")

        val cFlow = stateFlow.wrap()

        assertEquals(stateFlow.value, cFlow.currentValue)
    }

    @Test
    fun `CFlow watchOnMain pipes values correctly`() {
        val initialValue = "first"
        val stateFlow = MutableStateFlow(initialValue)
        val cFlow = stateFlow.wrap()
        var output = ""

        cFlow.watchOnMain { output = it }
        assertEquals(initialValue, output)

        val nextValue = "second"
        stateFlow.value = nextValue
        assertEquals(nextValue, output)
    }
}
