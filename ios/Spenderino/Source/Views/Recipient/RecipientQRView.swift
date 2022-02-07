import SwiftUI
import shared

struct RecipientQRView: View {
    let state: RecipientState.QRState
    @State var isShareSheetPresented = false
    @State var sharedItems: [Any] = []

    var body: some View {
        ScrollView {
            VStack(spacing: 32) {
                Text(state.text)
                    .font(.headline)
                    .fontWeight(.semibold)
                    .multilineTextAlignment(.center)

                RoundedRectangle(cornerRadius: 12)
                    .frame(width: 300, height: 300)
                    .foregroundColor(Color("LightGrey"))
                    .overlay(qrView(state.qrCode))
                    .cornerRadius(12)
                    .overlay(
                        RoundedRectangle(cornerRadius: 12)
                            .stroke(lineWidth: 1)
                            .foregroundColor(.secondary)
                    )

                Text("recipient_qrcode_label")
                    .font(.footnote)
                    .foregroundColor(.secondary)
                    .multilineTextAlignment(.center)

                if let qrCode = state.qrCode {
                    Button {
                        sharedItems = [qrCode.toUIImage()]
                        isShareSheetPresented = true
                    } label: {
                        Image(systemName: "square.and.arrow.up")
                    }
                }
            }
            .padding(32)
            .sheet(
                isPresented: $isShareSheetPresented,
                onDismiss: nil,
                content: { ShareSheet(activityItems: sharedItems) }
            )
        }
    }

    @ViewBuilder
    func qrView(_ byteArray: KotlinByteArray?) -> some View {
        if let byteArray = byteArray {
            Image(uiImage: byteArray.toUIImage())
        } else {
            ProgressView()
        }
    }
}
