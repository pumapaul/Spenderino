import SwiftUI
import shared
import PagerTabStripView

struct RecipientPagerView: View {
    let viewModel: RecipientViewModel
    let state: RecipientState.Pager

    var body: some View {
        PagerTabStripView {
            RecipientQRView(state: state.qrState)
                .pagerTabItem {
                    Text("recipient_tab_qr")
                }

            RecipientBalanceView(
                viewModel: viewModel,
                state: state.balanceState,
                pickerValue: Int(viewModel.minWithdrawalSum / 100)
            )
                .pagerTabItem {
                    Text("recipient_tab_balance")
                }
        }
        .pagerTabStripViewStyle(.segmentedControl(backgroundColor: .white, padding: .init(), placedInToolbar: false))
    }
}
