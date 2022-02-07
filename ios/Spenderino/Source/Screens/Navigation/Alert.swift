import SwiftUI
import shared

extension View {
    func alert<Route, Actions: View, Message: View>(
        title: (Route) -> Text,
        route: Binding<Route?>,
        @ViewBuilder actions: @escaping (Route) -> Actions,
        @ViewBuilder message: @escaping (Route) -> Message
    ) -> some View {
        self.alert(
          route.wrappedValue.map(title) ?? Text(""),
          isPresented: route.isPresent(),
          presenting: route.wrappedValue,
          actions: actions,
          message: message
        )
    }

    func alert<Route>(route: Binding<Route?>) -> some View where Route: AlertRoute {
        self.alert(
            title: { Text($0.alert.title) },
            route: route,
            actions: { _ in EmptyView() },
            message: { Text($0.alert.message) }
        )
    }
}
