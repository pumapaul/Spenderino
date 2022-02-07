import SwiftUI
import shared
import StripeUICore

struct AccountRowView: View {
    let state: PreferencesStateKs
    var body: some View {
        switch state {
        case .loading, .error, .anonymous:
            row(name: nil)
        case .registered(let subState):
            row(name: subState.user.email)
        case .setupComplete(let subState):
            row(name: subState.profile.username)
        }
    }

    func row(name: String?) -> some View {
        HStack(spacing: 12) {
            Circle()
                .frame(width: 64, height: 64)
                .foregroundColor(Color("LightGrey"))
                .overlay(
                    Image(systemName: "questionmark")
                        .foregroundColor(.primary)
                )

            VStack(alignment: .leading, spacing: 4) {
                if let name = name {
                    Text(name)
                        .font(.headline)
                        .foregroundColor(.primary)
                } else {
                    Text("prefs_account_row_anonymous")
                        .font(.headline)
                        .foregroundColor(.primary)
                }

                Text("prefs_account_row_info")
                    .font(.caption)
                    .foregroundColor(.secondary)
            }
            Spacer()
            Image(systemName: "chevron.right")
                .foregroundColor(.secondary)
        }
        .padding(.vertical, 8)
    }
}

struct AccountRowView_Previews: PreviewProvider {
    static var previews: some View {
        let user = User(identifier: "something", userType: .registered, email: "my@email.de")
        let profile = Profile(username: "Alexia", recipientCode: "", balance: 0)
        AccountRowView(state: .anonymous)
            .previewLayout(.sizeThatFits)
        AccountRowView(state: .registered(PreferencesState.Registered(user: user)))
            .previewLayout(.sizeThatFits)
        AccountRowView(state: .setupComplete(PreferencesState.SetupComplete(user: user, profile: profile)))
            .previewLayout(.sizeThatFits)

    }
}
