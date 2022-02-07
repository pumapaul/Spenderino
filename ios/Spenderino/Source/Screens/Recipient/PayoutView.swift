import SwiftUI
import shared

struct PayoutView: View {
    let viewModel: PayoutViewModel

    var body: some View {
        StateObservingView(state: viewModel.wrappedState) { state in
            let state = PayoutStateKs(state)

            switch state {
            case .loading: LoadingView()
            case .error: ErrorView(reloadingClosure: nil)
            case .qRCode(let qrState):
                qrView(qrState)
            case .success:
                Text("payout_success")
            }
        }
        .navigationBarTitleDisplayMode(.inline)
        .navigationTitle("payout_title")
    }

    @ViewBuilder
    func qrView(_ state: PayoutState.QRCode) -> some View {
        VStack {
            Text(state.text)
                .font(.headline)

            RoundedRectangle(cornerRadius: 12)
                .frame(width: 300, height: 300)
                .foregroundColor(Color("LightGrey"))
                .overlay(Image(uiImage: state.code.toUIImage()))
                .cornerRadius(12)
                .overlay(
                    RoundedRectangle(cornerRadius: 12)
                        .stroke(lineWidth: 1)
                        .foregroundColor(.secondary)
                )

        }
    }
}
