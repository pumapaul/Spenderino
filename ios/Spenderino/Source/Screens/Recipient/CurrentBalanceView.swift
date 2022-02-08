import SwiftUI

struct CurrentBalanceView: View {
    let currentBalance: Int64

    var body: some View {
        VStack(spacing: 12) {
            Text("balance_current_headline")
                .font(.title)
                .fontWeight(.semibold)

            Text(currentBalance.toEuroString())
                .font(.largeTitle)
                .fontWeight(.bold)
                .padding()
                .background(
                    Color("LightGrey")
                        .cornerRadius(16)
                )
        }
        .padding(.horizontal, 32)
        .padding(.vertical, 16)

    }
}

struct CurrentBalanceView_Previews: PreviewProvider {
    static var previews: some View {
        CurrentBalanceView(currentBalance: 1)
            .previewLayout(.sizeThatFits)
    }
}
