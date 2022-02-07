import SwiftUI
import shared
import Combine

private class ObservableOptionalModel<Observed>: ObservableObject {
    @Published var observed: Observed

    private var cancellables = Set<AnyCancellable>()

    init(initial: Observed, publisher: AnyPublisher<Observed, Never>) {
        observed = initial

        publisher
            .receive(on: DispatchQueue.main)
            .assign(to: &$observed)
    }
}

public struct ConfigureRoutes<Route: AnyObject, Content>: View where Content: View {
    @ObservedObject private var route: ObservableOptionalModel<Route?>

    private let content: (Route?) -> Content

    public init(route: CFlow<Route>, @ViewBuilder content: @escaping (Route?) -> Content) {
        let routePublisher = flowAsOptionalPublisher(route)
        self.route = ObservableOptionalModel(initial: route.currentValue, publisher: routePublisher)
        self.content = content
    }

    public var body: some View {
        content(route.observed)
    }
}
