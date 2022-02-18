import SwiftUI
import Stripe
import shared

struct CheckoutView: View {
    @StateObject var viewModel: CheckoutViewModel

    @State var testState = false

    var body: some View {
        VStack {
            Divider()
            HStack {
                Text("donation_checkout_payment_row")
                Spacer()
                if let paymentSheetFlowController = viewModel.paymentSheetFlowController {
                    PaymentSheet.FlowController.PaymentOptionsButton(
                        paymentSheetFlowController: paymentSheetFlowController,
                        onSheetDismissed: viewModel.onOptionsCompletion
                    ) {
                        Image(uiImage: paymentSheetFlowController.paymentOption?.image ?? UIImage(systemName: "creditcard")!)
                            .resizable()
                            .scaledToFit()
                            .frame(maxWidth: 30, maxHeight: 30, alignment: .leading)
                            .foregroundColor(.black)
                        Text(paymentSheetFlowController.paymentOption?.label ?? "Select...")
                            // Surprisingly, setting the accessibility identifier on the HStack causes the identifier to be
                            // "Payment method-Payment method". We'll set it on a single View instead.
                            .accessibility(identifier: "Payment method")
                    }
                } else {
                    ProgressView()
                }
            }.padding(.horizontal, 16)

            Spacer().frame(height: 32)

            if let paymentSheetFlowController = viewModel.paymentSheetFlowController {
                LoadingButton(isLoading: testState) {
                    testState = true
                } label: {
                    Text("donation_checkout_payment_donate_button")
                }
                .paymentConfirmationSheet(
                    isConfirming: $testState,
                    paymentSheetFlowController: paymentSheetFlowController,
                    onCompletion: viewModel.onCompletion
                )
                .buttonStyle(.borderedProminent)
                .padding(.horizontal, 32)
                .disabled(
                    paymentSheetFlowController.paymentOption == nil
                    || viewModel.isTransactionInProgress
                    || viewModel.isUpdatingSum
                    || testState
                )
            } else {
                Button {} label: {
                    Text("donation_checkout_payment_donate_button_prepare")
                        .frame(minWidth: 200, minHeight: 30)
                }
                .buttonStyle(.borderedProminent)
                .disabled(true)
            }
        }
    }
}
