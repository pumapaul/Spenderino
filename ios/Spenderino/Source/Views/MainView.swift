import SwiftUI
import shared

struct MainView: View {
    let viewModel: AppViewModel
    var body: some View {
        RootView(viewModel: viewModel.tabViewModel)
            .onOpenURL { url in
                viewModel.perform(action: AppAction.DeepLink(url: url.absoluteString))
            }
    }
}
