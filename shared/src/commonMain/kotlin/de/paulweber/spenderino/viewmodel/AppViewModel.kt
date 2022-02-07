package de.paulweber.spenderino.viewmodel

object AppState

sealed class AppAction {
    data class DeepLink(val url: String) : AppAction()
}

class AppViewModel(val tabViewModel: TabViewModel) :
    ViewModel<AppAction, Unit, AppState>(AppState) {
    companion object {
        fun bootstrap(): AppViewModel {
            val tabViewModel = TabViewModel(
                TabState(TabEnum.RECIPIENT),
                DonationScannerViewModel(),
                RecipientViewModel(),
                PreferencesViewModel()
            )
            return AppViewModel(tabViewModel)
        }
    }

    override fun perform(action: AppAction) {
        when (action) {
            is AppAction.DeepLink -> onDeepLink(action.url)
        }
    }

    private fun onDeepLink(url: String) {
        when {
            url.startsWith("https://spenderino.herokuapp.com/r/") -> routeToDonation(url)
            else -> Unit
        }
    }

    private fun routeToDonation(url: String) {
        val codeScanAction = DonationScannerAction.CodeScanned(url)
        val selectTabAction = TabAction.SelectTab(TabEnum.DONATION)
        tabViewModel.perform(selectTabAction)
        tabViewModel.donationScannerViewModel.perform(codeScanAction)
    }
}
