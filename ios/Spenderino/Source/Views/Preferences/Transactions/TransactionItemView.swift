import SwiftUI
import shared

struct TransactionItemView: View {
    let item: TransactionState.TransactionItem
    var body: some View {
        switch item {
        case let donationItem as TransactionState.TransactionItemDonationItem:
            DonationItemView(item: donationItem)
        case let withdrawalItem as TransactionState.TransactionItemWithdrawalItem:
            WithdrawalItemView(item: withdrawalItem)
        default:
            EmptyView()
        }
    }
}
