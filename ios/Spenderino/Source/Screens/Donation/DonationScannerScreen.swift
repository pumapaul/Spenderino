import SwiftUI
import shared
import CodeScanner

struct DonationScannerScreen: View {
    let viewModel: DonationScannerViewModel
    @State var code = ""

    var body: some View {
        ZStack {
            CodeScannerView(codeTypes: [.qr]) { result in
                let action: DonationScannerAction
                switch result {
                case .failure(let error):
                    action = DonationScannerAction.ScanError(error: error.localizedDescription)
                case .success(let code):
                    action = DonationScannerAction.CodeScanned(code: code.string)
                }
                viewModel.perform(action: action)
            }

            ConfigureRoutes(route: viewModel.wrappedRoute) { route in
                NavigationLink(
                    route: Binding<DonationScannerRoute.Donation?>(route: route),
                    destination: { DonationScreen(viewModel: $0.viewModel) }
                )
                .alert(
                    route: Binding<DonationScannerRoute.Alert?>(
                        dismissableRoute: route,
                        viewModel: viewModel
                    )
                )
            }
        }.navigationBarTitle("donation_scanner_title", displayMode: .inline)
    }
}

struct DonationScannerView_Previews: PreviewProvider {
    static var previews: some View {
        DonationScannerScreen(viewModel: .init(route: nil))
    }
}
