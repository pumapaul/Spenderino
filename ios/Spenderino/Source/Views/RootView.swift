import SwiftUI
import shared

struct RootView: View {
    let viewModel: TabViewModel

    var body: some View {
        StateObservingView(state: viewModel.wrappedState) { state in
            TabView(
                selection: Binding<TabEnum>(
                    get: { state.selectedTab },
                    set: { viewModel.perform(action: TabAction.SelectTab(newTab: $0)) }
                )) {
                    NavigationView {
                        DonationScannerView(viewModel: viewModel.donationScannerViewModel)
                    }
                    .tabItem { Label("tab_donation", systemImage: "camera") }
                    .tag(TabEnum.donation)

                    NavigationView {
                        RecipientView(viewModel: viewModel.recipientViewModel)
                    }
                    .tabItem { Label("tab_recipient", systemImage: "qrcode") }
                    .tag(TabEnum.recipient)

                    NavigationView {
                        PreferencesView(viewModel: viewModel.preferencesViewModel)
                    }
                    .tabItem { Label("tab_preferences", systemImage: "gearshape") }
                    .tag(TabEnum.preferences)
                }
        }
    }
}
