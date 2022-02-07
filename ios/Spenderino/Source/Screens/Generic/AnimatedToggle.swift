import SwiftUI

struct AnimatedToggle: View {
    let title: String
    @Binding var isOn: Bool
    @State private var localIsOn = false

    var body: some View {
        Toggle(
            title,
            isOn: Binding<Bool>(
                get: { localIsOn },
                set: { isOn = $0 }
            )
        ).onChange(of: isOn) { newValue in
            withAnimation {
                localIsOn = newValue
            }
        }
    }
}
