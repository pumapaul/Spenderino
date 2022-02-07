import SwiftUI
import shared

@main
struct SpenderinoApp: App {
    @UIApplicationDelegateAdaptor(AppDelegate.self) var appDelegate

	var body: some Scene {
		WindowGroup {
            MainView(viewModel: AppViewModel.companion.bootstrap())
		}
	}
}
