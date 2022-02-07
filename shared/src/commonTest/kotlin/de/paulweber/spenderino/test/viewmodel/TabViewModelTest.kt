@file:Suppress("IllegalIdentifier")

package de.paulweber.spenderino.test.viewmodel

import de.paulweber.spenderino.test.BaseTest
import de.paulweber.spenderino.viewmodel.DonationScannerViewModel
import de.paulweber.spenderino.viewmodel.PreferencesViewModel
import de.paulweber.spenderino.viewmodel.RecipientViewModel
import de.paulweber.spenderino.viewmodel.TabAction
import de.paulweber.spenderino.viewmodel.TabEnum
import de.paulweber.spenderino.viewmodel.TabState
import de.paulweber.spenderino.viewmodel.TabViewModel
import kotlin.test.Test
import kotlin.test.assertEquals

class TabViewModelTest : BaseTest() {
    @Test
    fun `TabViewModel sets tab on SelectTab`() {
        val initialTab = TabEnum.DONATION
        val initialState = TabState(initialTab)
        val viewModel = TabViewModel(
            state = initialState,
            donationScannerViewModel = DonationScannerViewModel(),
            recipientViewModel = RecipientViewModel(),
            preferencesViewModel = PreferencesViewModel()
        )

        assertEquals(initialState, viewModel.state.value)

        val targetTab = TabEnum.PREFERENCES
        viewModel.perform(TabAction.SelectTab(targetTab))

        assertEquals(TabState(targetTab), viewModel.state.value)
    }
}
