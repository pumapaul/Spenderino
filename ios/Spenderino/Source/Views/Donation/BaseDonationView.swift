import SwiftUI
import shared

struct BaseDonationView: View {
    let state: DonationState.Base
    let viewModel: DonationViewModel

    var body: some View {
        VStack {
            Group {
                titleView
                Spacer().frame(height: 20)
                textView
                cardRow("donation_card_succeeds_label", card: "donation_card_succeeds_card", paste: "donation_card_succeeds_paste")
                cardRow("donation_card_auth_label", card: "donation_card_auth_card", paste: "donation_card_auth_paste")
                cardRow("donation_card_fail_label", card: "donation_card_fail_card", paste: "donation_card_fail_paste")
            }
            Spacer()
            Divider().padding(.leading, 8)
            donationRow
            Divider().padding(.leading, 8)
            feeRow
            Divider().padding(.leading, 8)
            totalRow
        }
    }

    @ViewBuilder
    func cardRow(_ text: String, card: String, paste: String) -> some View {
        HStack {
            Text(NSLocalizedString(text, comment: ""))
                .font(.caption)
                .foregroundColor(.secondary)
            Spacer()
            Text(NSLocalizedString(card, comment: ""))
                .font(.caption)
                .fontWeight(.light)
                .foregroundColor(.secondary)
            Button {
                UIPasteboard.general.string = NSLocalizedString(paste, comment: "")
            } label: {
                Image(systemName: "doc.on.doc")
                    .resizable()
                    .frame(width: 24, height: 24)
            }
        }.padding()
    }

    var feeRow: some View {
        HStack {
            Text("donation_row_fee")
                .font(.headline)
            Spacer()
            Text(state.transactionFee.toEuroString())
                .font(.system(size: 15))
        }
        .padding(.horizontal, 16)
        .padding(.vertical, 8)
    }

    var totalRow: some View {
        HStack {
            rowTitle("donation_row_total")
            Spacer()
            Text(state.totalValue.toEuroString())
                .fontWeight(.semibold)
        }
        .padding(.horizontal, 16)
        .padding(.vertical, 8)
    }

    var textView: some View {
        Text("donation_text")
            .foregroundColor(.secondary)
            .font(.body)
            .padding()
    }

    @ViewBuilder
    var titleView: some View {
        VStack {
            Spacer().frame(height: 32)

            Text("donation_headline")
                .font(.headline)

            Spacer().frame(height: 12)

            Text(state.donationInfo.recipient.name)
                .font(.title)
                .fontWeight(.heavy)
        }.padding()
    }

    var donationRow: some View {
        HStack {
            rowTitle("donation_row_donation")
            Spacer()
            Picker(
                "",
                selection: Binding<Int64>(
                    get: { state.donationValue },
                    set: {
                        viewModel.perform(action: DonationAction.ChangeDonationValue(newValue: $0))
                    }
                ),
                content: {
                    ForEach(1..<51) { val in
                        Text("\(val),00 €").tag(Int64(val * 100))
                    }
                }
            )
            .labelsHidden()
        }.padding(.horizontal, 16)
    }

    @ViewBuilder
    func rowTitle(_ text: String) -> some View {
        Text(.init(text))
            .font(.headline)
            .fontWeight(.bold)
    }
}

struct BaseDonationView_Previews: PreviewProvider {
    static var previews: some View {
        BaseDonationView(
            state: .init(
                isTransactionInProgress: false,
                isUpdatingSum: false,
                donationValue: 1,
                transactionFee: 29,
                totalValue: 129,
                donationInfo: .init(
                    recipient: .init(name: "Donation receiver name"),
                    paymentSecret: "",
                    paymentIntentId: "",
                    customerId: "",
                    customerSecret: ""
                )
            ),
            viewModel: .init(url: "", state: DonationState.Loading(), route: nil, onBack: {})
        )
    }
}
