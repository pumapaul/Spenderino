import SwiftUI
import shared

struct DonationItemView: View {
    let item: TransactionState.TransactionItemDonationItem

    var body: some View {
        HStack {
            image

            VStack(alignment: .leading, spacing: 4) {
                Text(.init(item.title))
                    .font(.callout)
                    .multilineTextAlignment(.leading)
                subtitle
            }

            Spacer()

            state

        }.padding(.vertical, 4)
    }

    @ViewBuilder
    var state: some View {
        switch item.state {
        case .pending:
            statePanel(text: "transactions_item_pending", color: .yellow)
        case .complete:
            statePanel(text: "transactions_item_complete", color: .green)
        case .failed:
            statePanel(text: "transactions_item_failed", color: .red)
        default:
            EmptyView()
        }
    }

    func statePanel(text: String, color: Color) -> some View {
        Text(.init(text))
            .font(.system(size: 8))
            .fontWeight(.heavy).kerning(-0.3)
            .opacity(0.8)
            .padding(6)
            .background(
                color
                    .cornerRadius(8)
            )
    }

    @ViewBuilder
    var image: some View {
        switch item.direction {
        case .outgoing:
            Text(Image(systemName: "arrow.right"))
                .font(.title)
                .fontWeight(.bold)
                .foregroundColor(.teal)
        case .incoming:
            Text(Image(systemName: "arrow.left"))
                .font(.title)
                .fontWeight(.bold)
                .foregroundColor(.green)
        default:
            EmptyView()
        }
    }

    @ViewBuilder
    var subtitle: some View {
        switch item.direction {
        case .outgoing:
            HStack(spacing: 0) {
                date
                Spacer()
                Text("Fee: ")
                    .font(.caption2)
                    .foregroundColor(.secondary)
                Text(item.fees.toEuroString())
                    .font(.caption2)
                    .foregroundColor(.secondary)
            }

        case .incoming:
            date
        default:
            EmptyView()
        }
    }

    var date: some View {
        Text(item.timestampString)
            .font(.caption2)
            .foregroundColor(.secondary)
    }
}
