import Foundation
import shared
import Stripe
import Combine

class CheckoutViewModel: ObservableObject {
    private let customerId: String
    private let customerEphemeralKeySecret: String
    private let paymentIntentClientSecret: String
    weak var donationViewModel: DonationViewModel?

    @Published var paymentSheetFlowController: PaymentSheet.FlowController?
    @Published var paymentResult: PaymentSheetResult?
    @Published var isTransactionInProgress = false
    @Published var isUpdatingSum = false

    private var cancellables = Set<AnyCancellable>()

    init(donationState: DonationState.Base, donationViewModel: DonationViewModel) {
        self.donationViewModel = donationViewModel
        let info = donationState.donationInfo
        self.customerId = info.customerId
        self.customerEphemeralKeySecret = info.customerSecret
        self.paymentIntentClientSecret = info.paymentSecret

        STPAPIClient.shared.publishableKey = donationViewModel.stripeKey

        listenToDonationViewModel()
        createPaymentSheet()
    }

    private func listenToDonationViewModel() {
        guard let viewModel = donationViewModel else { return }
        let publisher = flowAsPublisher(viewModel.wrappedState)
            .compactMap { $0 as? DonationState.Base }

        publisher.map { $0.isTransactionInProgress }
            .assign(to: \.isTransactionInProgress, on: self)
            .store(in: &cancellables)

        publisher.map { $0.isUpdatingSum }
            .assign(to: \.isUpdatingSum, on: self)
            .store(in: &cancellables)
    }

    private func createPaymentSheet() {
        var configuration = PaymentSheet.Configuration()
        configuration.merchantDisplayName = "Spenderino"
        configuration.customer = .init(
            id: customerId, ephemeralKeySecret: customerEphemeralKeySecret)
//        configuration.allowsDelayedPaymentMethods = true
//        configuration.applePay = .init(merchantId: "com.foo.example", merchantCountryCode: "US")

        PaymentSheet.FlowController.create(
            paymentIntentClientSecret: paymentIntentClientSecret,
            configuration: configuration
        ) { [weak self] result in
            switch result {
            case .failure(let error):
                print(error)
            case .success(let paymentSheetFlowController):
                DispatchQueue.main.async {
                    self?.paymentSheetFlowController = paymentSheetFlowController
                }
            }
        }
    }

    func onOptionsCompletion() {
        objectWillChange.send()
    }

    func onCompletion(result: PaymentSheetResult) {
        let stripeResult: StripeResult
        switch result {
        case .canceled: stripeResult = StripeResult.Canceled()
        case .completed: stripeResult = StripeResult.Completed()
        case .failed(let error):
            stripeResult = StripeResult.Failed(localizedErrorMessage: error.localizedDescription)
        }
        let action = DonationAction.TransactionResult(result: stripeResult)
        donationViewModel?.perform(action: action)
    }
}
