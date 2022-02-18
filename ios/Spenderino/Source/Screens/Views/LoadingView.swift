import SwiftUI

struct LoadingView: View {
    var body: some View {
        VStack(spacing: 4) {
            ProgressView()

            Text("loading")
                .font(.footnote)
                .foregroundColor(Color.gray)
        }
    }
}

struct LoadingView_Previews: PreviewProvider {
    static var previews: some View {
        LoadingView().previewLayout(.sizeThatFits)
    }
}
