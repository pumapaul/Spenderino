package de.paulweber.spenderino.viewmodel

enum class TabEnum {
    DONATION, RECIPIENT, PREFERENCES
}

data class TabState(
    val selectedTab: TabEnum
)

sealed class TabAction {
    data class SelectTab(val newTab: TabEnum) : TabAction()
}

class TabViewModel(
    state: TabState,
    val donationScannerViewModel: DonationScannerViewModel,
    val recipientViewModel: RecipientViewModel,
    val preferencesViewModel: PreferencesViewModel
) : ViewModel<TabAction, Unit, TabState>(state) {
    override fun perform(action: TabAction) {
        when (action) {
            is TabAction.SelectTab -> setState(state.value.copy(selectedTab = action.newTab))
        }
    }
}
