@file:Suppress("IllegalIdentifier")

package de.paulweber.spenderino.test.viewmodel

import de.paulweber.spenderino.model.networking.BASE_URL
import de.paulweber.spenderino.test.BaseTest
import de.paulweber.spenderino.viewmodel.DonationScannerAction
import de.paulweber.spenderino.viewmodel.DonationScannerRoute
import de.paulweber.spenderino.viewmodel.DonationScannerViewModel
import kotlin.test.Test
import kotlin.test.assertEquals

class DonationScannerViewModelTest : BaseTest() {
    @Test
    fun `ScanError action routes to alert`() {
        val expected = "some string"

        val viewModel = DonationScannerViewModel()
        viewModel.perform(DonationScannerAction.ScanError(expected))

        assertEquals(true, viewModel.route.value is DonationScannerRoute.Alert)

        val alert = (viewModel.route.value as DonationScannerRoute.Alert).alert
        assertEquals("alert_unknown_title", alert.title)
        assertEquals(expected, alert.message)
    }

    @Test
    fun `CodeScanned action with valid code routes to DonationViewModel`() {
        val code = "$BASE_URL/r/something"

        val viewModel = DonationScannerViewModel()
        viewModel.perform(DonationScannerAction.CodeScanned(code))

        assertEquals(true, viewModel.route.value is DonationScannerRoute.Donation)
        val donationViewModel = (viewModel.route.value as DonationScannerRoute.Donation).viewModel

        donationViewModel.onBackButton()
        assertEquals(null, viewModel.route.value)
    }
    @Test
    fun `CodeScanned action with invalid code routes to alert`() {
        val viewModel = DonationScannerViewModel()
        viewModel.perform(DonationScannerAction.CodeScanned(""))

        assertEquals(true, viewModel.route.value is DonationScannerRoute.Alert)

        val alert = (viewModel.route.value as DonationScannerRoute.Alert).alert
        assertEquals("donation_scanner_alert_unknown_title", alert.title)
    }
}
