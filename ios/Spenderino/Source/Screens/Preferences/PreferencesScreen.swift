import SwiftUI
import shared

struct PreferencesScreen: View {
    let viewModel: PreferencesViewModel

    var body: some View {
        ZStack {

            StateObservingView(state: viewModel.wrappedState) { state in
                let state = PreferencesStateKs(state)

                Form {
                    Section {}.opacity(0)

                    Section {
                        Button {
                            viewModel.perform(action: .account)
                        } label: {
                            AccountRowView(state: state)
                        }

                    }
                    Section {
                        Button {
                            viewModel.perform(action: .transactions)
                        } label: {
                            HStack {
                                Text("account_transactions_button")
                                    .foregroundColor(.primary)
                                Spacer()
                                Image(systemName: "chevron.right")
                                    .foregroundColor(.secondary)
                            }
                        }
                    }
                }
            }
            ConfigureRoutes(route: viewModel.wrappedRoute) { route in
                NavigationLink(
                    route: Binding<PreferencesRoute.Transactions?>(route: route),
                    destination: { TransactionsScreen(viewModel: $0.viewModel) }
                )
                NavigationLink(
                    route: Binding<PreferencesRoute.Account?>(route: route),
                    destination: { AccountScreen(viewModel: $0.viewModel) }
                )
            }
        }
        .navigationBarTitle("preferences_title")
    }
}

struct PreferencesView_Previews: PreviewProvider {
    static var previews: some View {
        PreferencesScreen(viewModel: .init(state: .init(), route: nil))
    }
}
