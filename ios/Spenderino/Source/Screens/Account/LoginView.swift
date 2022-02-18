import SwiftUI
import shared

struct LoginView: View {
    let viewModel: AccountViewModel
    let state: AccountState.Anonymous

    var body: some View {
        VStack(spacing: 32) {
            VStack(alignment: .leading, spacing: 4) {
                Text("account_email_label")
                    .font(.caption)
                    .fontWeight(.semibold)

                emailTextField

                Spacer().frame(height: 4)

                Text("account_password_label")
                    .font(.caption)
                    .fontWeight(.semibold)

                passwordTextField
            }

            VStack(alignment: .center) {
                loginButton
                Text("or")
                    .font(.footnote)
                    .foregroundColor(.secondary)
                registerButton
            }
        }
        .padding()
        .disabled(state.isRegisterLoading || state.isLoginLoading)
    }

    var loginButton: some View {
        LoadingButton(isLoading: state.isLoginLoading) {
            viewModel.perform(action: AccountAction.Login())
        } label: {
            Text("account_button_login")
        }
        .buttonStyle(.borderedProminent)
    }

    var registerButton: some View {
        LoadingButton(isLoading: state.isRegisterLoading) {
            viewModel.perform(action: AccountAction.Register())
        } label: {
            Text("account_button_register")
        }
        .buttonStyle(.bordered)
    }

    var emailTextField: some View {
        TextField(
            "account_email_hint",
            text: Binding<String>(
                get: { state.email },
                set: { viewModel.perform(action: AccountAction.ChangeEmailText(text: $0)) }
            )
        )

        .textFieldStyle(.roundedBorder)
        .textContentType(.emailAddress)
        .autocapitalization(.none)
    }

    var passwordTextField: some View {
        SecureField(
            "account_password_hint",
            text: Binding<String>(
                get: { state.password },
                set: { viewModel.perform(action: AccountAction.ChangePasswordText(text: $0)) }
            )
        )
        .textFieldStyle(.roundedBorder)
        .textContentType(.password)
    }
}

struct AnonymousAccountView_Previews: PreviewProvider {
    static var previews: some View {
        let viewModel = AccountViewModel(
            state: AccountState.Anonymous(
                isLoginLoading: true,
                isRegisterLoading: true,
                email: "",
                password: ""
            ),
            route: nil,
            onSetupComplete: {},
            onBack: {}
        )
        LoginView(viewModel: viewModel, state: .init(
            isLoginLoading: false,
            isRegisterLoading: false,
            email: "Test",
            password: "GANZ VIEL STUFF")
        ).previewLayout(.sizeThatFits)
    }
}
