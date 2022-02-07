import SwiftUI
import shared

extension NavigationLink {
    init<Route, WrappedDestination>(
        route: Binding<Route?>,
        @ViewBuilder destination: @escaping (Route) -> WrappedDestination
    ) where Label == EmptyView, Destination == WrappedDestination?, Route: Navigating {
        self.init(
            isActive: route.isPresent().didSet {
                if let route = route.wrappedValue, !$0 {
                    route.viewModel.onBackButton()
                }
            },
            destination: { route.wrappedValue.map(destination) },
            label: { EmptyView() }
        )
    }
}
