package de.paulweber.spenderino.viewmodel

import de.paulweber.spenderino.model.networking.BASE_URL
import de.paulweber.spenderino.utility.L10n

sealed class DonationScannerRoute {
    data class Donation(override val viewModel: DonationViewModel) : DonationScannerRoute(),
        Navigating

    data class Alert(override val alert: AlertViewModel) : DonationScannerRoute(), AlertRoute
}

sealed class DonationScannerAction {
    data class CodeScanned(val code: String) : DonationScannerAction()
    data class ScanError(val error: String) : DonationScannerAction()
}

class DonationScannerViewModel(route: DonationScannerRoute? = null) :
    ViewModel<DonationScannerAction, DonationScannerRoute, Unit>(Unit, route) {
    override fun perform(action: DonationScannerAction) {
        when (action) {
            is DonationScannerAction.CodeScanned -> onCodeScanned(action.code)
            is DonationScannerAction.ScanError -> onScanError(action.error)
        }
    }

    private fun onCodeScanned(code: String) {
        val route = if (code.startsWith("$BASE_URL/r/")) {
            val viewModel = DonationViewModel(url = code, onBack = this::routeToNull)
            DonationScannerRoute.Donation(viewModel)
        } else {
            val alert = AlertViewModel("donation_scanner_alert_unknown", this::routeToNull)
            DonationScannerRoute.Alert(alert)
        }
        setRoute(route)
    }

    private fun onScanError(error: String) {
        val alert = AlertViewModel(
            title = L10n.get("alert_unknown_title"),
            message = error,
            actions = listOf(),
            onDestroy = this::routeToNull
        )
        val route = DonationScannerRoute.Alert(alert)
        setRoute(route)
    }
}
