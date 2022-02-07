import SwiftUI

struct ErrorView: View {
    let reloadingClosure: (() -> Void)?

    var body: some View {
        VStack(spacing: 16) {
            Text("error_title")
                .font(.title)
                .fontWeight(.bold)
            Text(reloadingClosure == nil ? "error_message" : "error_message_reload")
                .multilineTextAlignment(.center)

            reloadButton()
        }.padding(32)
    }

    @ViewBuilder
    private func reloadButton() -> some View {
        if let reloadingClosure = reloadingClosure {
            Button(action: reloadingClosure) {
                VStack(spacing: 4) {
                    Image(systemName: "arrow.clockwise")
                        .resizable()
                        .aspectRatio(contentMode: .fit)
                        .frame(width: 44, height: 44)

                    Text("error_reload")
                        .font(.title2)
                        .fontWeight(.semibold)
                }
            }.padding(16)
        } else {
            EmptyView()
        }
    }
}
struct ErrorView_Previews: PreviewProvider {
    static var previews: some View {

        ErrorView(reloadingClosure: nil).previewLayout(.sizeThatFits)

        ErrorView(reloadingClosure: {}).previewLayout(.sizeThatFits)

    }
}
