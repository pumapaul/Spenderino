import Foundation

extension Int64 {
    func toEuroString() -> String {
        let wholePart = self / 100
        let decimalPart = self % 100
        if decimalPart == 0 {
            return "\(wholePart) €"
        } else {
            return "\(wholePart),\(decimalPart) €"
        }
    }
}
