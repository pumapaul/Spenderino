import SwiftUI
import shared

struct MainScreen: View {
    let viewModel: AppViewModel
    var body: some View {
        TabScreen(viewModel: viewModel.tabViewModel)
            .onOpenURL { url in
                viewModel.perform(action: AppAction.DeepLink(url: url.absoluteString))
            }
    }
}
