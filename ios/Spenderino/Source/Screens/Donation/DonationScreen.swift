import SwiftUI
import shared
import Stripe

struct DonationScreen: View {
    let viewModel: DonationViewModel
    @State var pickerValue: Int64 = 1

    var body: some View {
        ZStack {
            ScrollView {
                StateObservingView(state: viewModel.wrappedState) { state in
                    let state = DonationStateKs(state)
                    switch state {
                    case .base(let baseState):
                        VStack {
                            BaseDonationView(state: baseState, viewModel: viewModel)

                            CheckoutView(viewModel: .init(
                                donationState: baseState,
                                donationViewModel: viewModel
                            ))
                            Spacer().frame(height: 50)
                        }
                    default: EmptyView()
                    }
                }
                ConfigureRoutes(route: viewModel.wrappedRoute) { route in
                    Text("")
                        .alert(
                            route: Binding<DonationRoute.Alert?>(
                                dismissableRoute: route,
                                viewModel: viewModel
                            )
                        )
                }
            }
            overlay

        }
        .navigationBarTitle("donation_title")
        .navigationBarTitleDisplayMode(.inline)
    }

    var overlay: some View {
        StateObservingView(state: viewModel.wrappedState) { state in
            let state = DonationStateKs(state)
            switch state {
            case .error(let errorState):
                errorView(state: errorState)
            case .loading:
                LoadingView()
            case .success(let successState):
                Text(.init(successState.message))
                    .font(.title2)
                    .multilineTextAlignment(.center)
                    .padding()
            default:
                EmptyView()
            }
        }
    }

    @ViewBuilder
    private func errorView(state: DonationState.Error) -> some View {
        let error = DonationStateErrorKs(state)
        switch error {
        case.networkError:
            ErrorView(reloadingClosure: { viewModel.perform(action: DonationAction.Reload()) })
        case .unknownCode:
            Text("donation_unknown_code")
                .font(.title2)
                .fontWeight(.bold)
                .multilineTextAlignment(.center)
                .padding()
        }
    }
}

struct DonationView_Previews: PreviewProvider {
    static var previews: some View {
        DonationScreen(viewModel: .init(url: "", state: DonationState.Error.ErrorUnknownCode(), route: nil, onBack: {}))
    }
}
