import SwiftUI

extension View {
    func sheet<Route, Content>(
        route: Binding<Route?>,
        onDismiss: (() -> Void)? = nil,
        onDismissInTarget: ((Route) -> Void)? = nil,
        @ViewBuilder destination: @escaping (Route) -> Content
    ) -> some View where Content: View {
        self.sheet(
            isPresented: route.isPresent(),
            onDismiss: {
                onDismiss?()
                guard let onDismissInTarget = onDismissInTarget else { return }
                route.wrappedValue.map(onDismissInTarget)
            },
            content: { route.wrappedValue.map(destination) }
        )
    }
}
