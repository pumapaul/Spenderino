import SwiftUI
import shared

struct RecipientScreen: View {
    let viewModel: RecipientViewModel

    var body: some View {
        ZStack {
            StateObservingView(state: viewModel.wrappedState) { state in
                let state = RecipientStateKs(state)

                VStack {
                    Spacer().frame(height: 30)
                    switch state {
                    case .anonymous:
                        anonymousView
                    case .pager(let subState):
                        RecipientPagerView(viewModel: viewModel, state: subState)
                    case .registered:
                        registeredView
                    default: EmptyView()
                    }
                }
            }
            ConfigureRoutes(route: viewModel.wrappedRoute) { route in
                Text("")
                    .sheet(
                        route: Binding<RecipientRoute.Payout?>(
                            dismissableRoute: route,
                            viewModel: viewModel
                        ),
                        onDismiss: { viewModel.perform(action: RecipientAction.ReloadBalance()) },
                        onDismissInTarget: { $0.viewModel.perform(action: .onDismiss) },
                        destination: { PayoutView(viewModel: $0.viewModel) }
                    )
                    .sheet(
                        route: Binding<RecipientRoute.Account?>(
                            dismissableRoute: route,
                            viewModel: viewModel
                        ),
                        destination: { CreateAccountView(viewModel: $0.viewModel) }
                    )
                    .alert(
                        route: Binding<RecipientRoute.Alert?>(
                            dismissableRoute: route,
                            viewModel: viewModel
                        )
                    )
            }

            loadingOrErrorView
        }
        .navigationTitle("recipient_title")
        .navigationBarTitleDisplayMode(.inline)
    }

    var loadingOrErrorView: some View {
        StateObservingView(state: viewModel.wrappedState) { state in
            let state = RecipientStateKs(state)
            switch state {
            case .error:
                ErrorView(reloadingClosure: { viewModel.perform(action: RecipientAction.ReloadRecipient()) })
            case .loading:
                LoadingView()
            default:
                EmptyView()
            }
        }
    }

    var registeredView: some View {
        ScrollView {
            VStack(spacing: 16) {
                Text("recipient_registered_title")
                    .font(.headline)
                    .fontWeight(.semibold)
                    .multilineTextAlignment(.center)

                Text("recipient_registered_label")
                    .font(.callout)
                    .foregroundColor(.secondary)
                    .multilineTextAlignment(.center)

                Spacer().frame(height: 16)

                Button {
                    viewModel.perform(action: RecipientAction.CreateProfile())
                } label: {
                    Text("recipient_registered_button")
                        .frame(minWidth: 200, minHeight: 30)
                }.buttonStyle(.borderedProminent)
            }
            .padding(32)
        }
    }

    var anonymousView: some View {
        ScrollView {
            VStack(spacing: 16) {
                Text("recipient_anon_title")
                    .font(.headline)
                    .fontWeight(.semibold)
                    .multilineTextAlignment(.center)

                Text("recipient_anon_label")
                    .font(.callout)
                    .foregroundColor(.secondary)
                    .multilineTextAlignment(.center)

                Spacer().frame(height: 16)

                Button {
                    viewModel.perform(action: RecipientAction.Login())
                } label: {
                    Text("recipient_anon_button")
                        .frame(minWidth: 200, minHeight: 30)
                }.buttonStyle(.borderedProminent)

                Spacer().frame(height: 16)

                Text("recipient_anon_disclaimer")
                    .font(.footnote)
                    .foregroundColor(.secondary)
                    .multilineTextAlignment(.center)
            }
            .padding(32)
        }
    }
}
