import Foundation

extension Optional {
    func map<T>(_ mappingFunction: (Wrapped) -> T ) -> T? {
        return self == nil ? nil : mappingFunction(self!)
    }
}
