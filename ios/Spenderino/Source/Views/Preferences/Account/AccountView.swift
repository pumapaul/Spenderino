import SwiftUI
import shared

struct AccountView: View {
    let viewModel: AccountViewModel

    var body: some View {
        ZStack {
            Form {
                Section {
                    CreateAccountView(viewModel: viewModel)
                }

                StateObservingView(state: viewModel.wrappedState) {
                    if !($0 is AccountState.Anonymous) {
                        logoutSection
                    }
                }
            }

            routes
        }
        .navigationBarTitle("account_title", displayMode: .inline)
    }

    var routes: some View {
        ConfigureRoutes(route: viewModel.wrappedRoute) { route in
            Text("")
            .alert(
                title: { Text($0.alert.title) },
                route: Binding<AccountRoute.LogoutAlert?>(
                    dismissableRoute: route,
                    viewModel: viewModel
                ),
                actions: { _ in
                    Button("account_alert_logout_confirm", role: .destructive) {
                        viewModel.perform(action: AccountAction.ConfirmLogout())
                    }
                },
                message: { Text($0.alert.message) }
            )

        }
    }

    var logoutSection: some View {
        Section {
            Button {
                viewModel.perform(action: AccountAction.Logout())
            } label: {
                Text("account_logout_button")
                    .foregroundColor(.red)
            }
        }
    }
}

struct AccountView_Previews: PreviewProvider {
    static var previews: some View {
        AccountView(
            viewModel: .init(
                state: AccountState.Anonymous(
                    isLoginLoading: false,
                    isRegisterLoading: false,
                    email: "",
                    password: ""
                ),
                route: nil,
                onSetupComplete: {},
                onBack: {}
            )
        )
    }
}
