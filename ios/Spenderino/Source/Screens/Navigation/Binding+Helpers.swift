import SwiftUI
import shared

extension Binding {
    /// Use when binding a route that's dismissable like alerts, sheets, etc.
    ///
    /// This will call viewModel.routeToNull when dismissed by the system.
    init<SuperRoute, TargetRoute>(
        dismissableRoute: SuperRoute?,
        viewModel: ViewModeling
    ) where Value == TargetRoute?, SuperRoute: KotlinBase, TargetRoute: KotlinBase {
        self.init(
            get: { dismissableRoute as? TargetRoute },
            set: {
                if $0 == nil {
                    viewModel.routeToNull()
                }
            }
        )
    }

    /// Use when binding a route that's used in NavigationLinks
    ///
    /// This is a get only binding, as in setting something here has no effect.
    init<SuperRoute, TargetRoute>(route: SuperRoute?) where Value == TargetRoute?, SuperRoute: KotlinBase, TargetRoute: KotlinBase {
        self.init(
            get: { route as? TargetRoute },
            set: { _ in }
        )
    }

    /// Emits the value of the Binding it's called on but also calls `perform` with the Binding's value.
    func didSet(_ perform: @escaping (Value) -> Void) -> Self {
      .init(
        get: { self.wrappedValue },
        set: { newValue, transaction in
          self.transaction(transaction).wrappedValue = newValue
          perform(newValue)
        }
      )
    }

    /// Creates a binding by mapping the current optional value to a boolean describing whether it's
    /// non-`nil`.
    ///
    /// Writing `false` to the binding will `nil` out the base value. Writing `true` does nothing.
    ///
    /// - Returns: A binding to a boolean. Returns `true` if non-`nil`, otherwise `false`.
    public func isPresent<Wrapped>() -> Binding<Bool>
    where Value == Wrapped? {
      .init(
        get: { self.wrappedValue != nil },
        set: { isPresent, transaction in
          if !isPresent {
            self.transaction(transaction).wrappedValue = nil
          }
        }
      )
    }
}
