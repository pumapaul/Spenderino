import SwiftUI
import Combine
import shared

private class ObservableModel<Observed>: ObservableObject {
    @Published var observed: Observed

    init(initial: Observed, publisher: AnyPublisher<Observed, Never>) {
        observed = initial

        publisher
            .compactMap { $0 }
            .receive(on: DispatchQueue.main)
            .assign(to: &$observed)
    }
}

public struct StateObservingView<State: AnyObject, Content>: View where Content: View {
    @ObservedObject private var state: ObservableModel<State>

    private let content: (State) -> Content

    public init(state: CFlow<State>, @ViewBuilder content: @escaping (State) -> Content) {
        let statePublisher = flowAsPublisher(state)
        self.state = ObservableModel(initial: state.currentValue!, publisher: statePublisher)
        self.content = content
    }

    public var body: some View {
        content(state.observed)
    }
}
