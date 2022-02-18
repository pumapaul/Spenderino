import SwiftUI
import shared

struct CreateProfileView: View {
    let viewModel: AccountViewModel
    let state: AccountState.Registered

    var body: some View {
        VStack(spacing: 32) {
            VStack(alignment: .leading, spacing: 4) {
                Text("account_profile_instructions")
                    .font(.footnote)
                    .foregroundColor(.secondary)

                Spacer().frame(height: 8)

                Text("account_profile_username_label")
                    .font(.caption)
                    .fontWeight(.semibold)

                textField

                Spacer().frame(height: 4)

                Text("account_profile_username_hint")
                    .font(.caption)
                    .foregroundColor(.secondary)
            }
            createButton
        }
        .padding()
        .disabled(state.isLoading)
    }

    var createButton: some View {
        LoadingButton(isLoading: state.isLoading) {
            viewModel.perform(action: AccountAction.CreateProfile())
        } label: {
            Text("account_profile_button")
        }
        .buttonStyle(.borderedProminent)
    }

    var textField: some View {
        TextField(
            "account_profile_username_placeholder",
            text: Binding<String>(
                get: { state.username },
                set: {
                    let action = AccountAction.ChangeUsernameText(text: $0)
                    viewModel.perform(action: action)
                }
            )
        )
        .textFieldStyle(.roundedBorder)
        .textContentType(.name)
    }
}

struct CreateProfileAccountView_Previews: PreviewProvider {
    static var previews: some View {
        let state = AccountState.Registered(
            user: User(identifier: "", userType: .registered, email: "something@something.de"),
            isLoading: false,
            username: ""
        )
        CreateProfileView(
            viewModel: .init(
                state: state,
                route: nil,
                onSetupComplete: {},
                onBack: {}
            ),
            state: state
        )
    }
}
