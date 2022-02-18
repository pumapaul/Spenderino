import SwiftUI
import shared

struct CreateAccountView: View {
    let viewModel: AccountViewModel

    var body: some View {
        ZStack {
            StateObservingView(state: viewModel.wrappedState) { state in
                let state = AccountStateKs(state)

                switch state {
                case .loading: LoadingView()
                case .error: ErrorView(reloadingClosure: nil)
                case .anonymous(let anonState):
                    LoginView(viewModel: viewModel, state: anonState)
                case .registered(let regState):
                    CreateProfileView(viewModel: viewModel, state: regState)
                case .setupComplete:
                    Text("account_setup_complete")
                        .font(.footnote)
                        .foregroundColor(.secondary)
                        .multilineTextAlignment(.center)
                        .padding()
                }
            }
            ConfigureRoutes(route: viewModel.wrappedRoute) { route in
                Text("")
                    .alert(
                        route: Binding<AccountRoute.Alert?>(
                            dismissableRoute: route,
                            viewModel: viewModel
                        )
                    )
            }
        }
    }
}

struct CreateAccountView_Previews: PreviewProvider {
    static var previews: some View {
        CreateAccountView(viewModel: .init(
            state: AccountState.Anonymous(
                isLoginLoading: false,
                isRegisterLoading: false,
                email: "",
                password: ""
            ),
            route: nil,
            onSetupComplete: {},
            onBack: {}
        ))
    }
}
