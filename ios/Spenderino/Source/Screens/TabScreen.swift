import SwiftUI
import shared

struct TabScreen: View {
    let viewModel: TabViewModel

    var body: some View {
        StateObservingView(state: viewModel.wrappedState) { state in
            TabView(
                selection: Binding<TabEnum>(
                    get: { state.selectedTab },
                    set: { viewModel.perform(action: TabAction.SelectTab(newTab: $0)) }
                )) {
                    NavigationView {
                        DonationScannerScreen(viewModel: viewModel.donationScannerViewModel)
                    }
                    .tabItem { Label("tab_donation", systemImage: "camera") }
                    .tag(TabEnum.donation)

                    NavigationView {
                        RecipientScreen(viewModel: viewModel.recipientViewModel)
                    }
                    .tabItem { Label("tab_recipient", systemImage: "qrcode") }
                    .tag(TabEnum.recipient)

                    NavigationView {
                        PreferencesScreen(viewModel: viewModel.preferencesViewModel)
                    }
                    .tabItem { Label("tab_preferences", systemImage: "gearshape") }
                    .tag(TabEnum.preferences)
                }
        }
    }
}
