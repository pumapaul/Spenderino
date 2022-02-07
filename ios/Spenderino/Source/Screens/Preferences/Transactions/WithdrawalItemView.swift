import SwiftUI
import shared

struct WithdrawalItemView: View {
    let item: TransactionState.TransactionItemWithdrawalItem

    var body: some View {
        HStack {
            Image(systemName: "arrow.down.circle.fill")
            Text(item.amount.toEuroString())
                .fontWeight(.semibold)
            Text(item.text)
                .font(.footnote)
        }.padding(.vertical, 4)
    }
}
