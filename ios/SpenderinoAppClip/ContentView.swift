import SwiftUI

struct ContentView: View {
    @State var code = "no url"
    var body: some View {
        Text(code)
            .background(
                Color.red
                    .edgesIgnoringSafeArea(.all)
            )
            .onContinueUserActivity("NSUserActivityTypeBrowsingWeb") { activity in
                code = activity.webpageURL?.absoluteString ?? "no url"
            }
    }
}

struct ContentView_Previews: PreviewProvider {
    static var previews: some View {
        ContentView()
    }
}
