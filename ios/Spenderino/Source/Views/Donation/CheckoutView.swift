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
                Button {
                    testState = true
                } label: {
                    if testState {
                        ProgressView()
                            .frame(minWidth: 200, minHeight: 30)
                    } else {
                        Text("donation_checkout_payment_donate_button")
                            .frame(minWidth: 200, minHeight: 30)
                    }
                }.paymentConfirmationSheet(
                    isConfirming: $testState,
                    paymentSheetFlowController: paymentSheetFlowController,
                    onCompletion: viewModel.onCompletion
                )
                .buttonStyle(BorderedProminentButtonStyle())
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
                        .frame(width: 200, height: 30)

                }
                .buttonStyle(BorderedProminentButtonStyle())
                .disabled(true)
            }
        }
    }
}
