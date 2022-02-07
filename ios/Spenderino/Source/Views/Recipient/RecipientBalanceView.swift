import SwiftUI
import shared

struct RecipientBalanceView: View {
    let viewModel: RecipientViewModel
    let state: RecipientState.BalanceState
    @State var pickerValue: Int

    var body: some View {
        let state = RecipientStateBalanceStateKs(state)
        switch state {
        case .loading: LoadingView()
        case .error: ErrorView(reloadingClosure: nil)
        case .base(let baseState):
            baseView(baseState.stateHolder, isReloading: false)
        case .reloading(let reloadingState):
            baseView(reloadingState.stateHolder, isReloading: true)
        }
    }

    private func baseView(_ holder: RecipientState.BalanceStateStateHolder, isReloading: Bool) -> some View {
        ScrollView {
            VStack(alignment: .center) {
                HStack {
                    Spacer()
                }
                Spacer().frame(height: 32)

                Button {
                    viewModel.perform(action: RecipientAction.ReloadBalance())
                } label: {
                    if isReloading {
                        ProgressView()
                    } else {
                        Image(systemName: "arrow.clockwise")
                    }
                }

                CurrentBalanceView(currentBalance: holder.currentBalance)

                if holder.currentBalance < viewModel.minWithdrawalSum {
                    underMinimumAmountView
                } else {
                    createWithdrawalView(holder)
                }
            }
        }
    }

    var underMinimumAmountView: some View {
        Text(viewModel.withdrawalUnderMinText)
            .font(.body)
            .fontWeight(.semibold)
            .padding(.horizontal, 32)
            .multilineTextAlignment(.center)
    }

    @ViewBuilder
    func createWithdrawalView(_ stateHolder: RecipientState.BalanceStateStateHolder) -> some View {
        VStack {
            Text("balance_withdrawal_create")
                .font(.subheadline)
                .fontWeight(.semibold)
                .multilineTextAlignment(.center)

            HStack {
                Text("balance_withdrawal_amount_label")
                    .font(.callout)

                Picker("", selection: $pickerValue) {
                    let minSum = Int(viewModel.minWithdrawalSum) / 100
                    let maxSum = Int(
                        min(
                            (viewModel.maxWithdrawalSum / 100),
                            (stateHolder.currentBalance / 100)
                        ) + 1
                    )
                    ForEach(minSum..<maxSum) { val in
                        Text("\(val) €").tag(val)
                    }
                }.labelsHidden()
            }

            Button {
                let action = RecipientAction.CreateWithdrawal(amount: Int64(pickerValue * 100))
                viewModel.perform(action: action)
            } label: {
                if stateHolder.isCreatingWithdrawal {
                    ProgressView()
                } else {
                    Text("balance_withdrawal_create_button")
                }
            }
            .buttonStyle(BorderedProminentButtonStyle())
            .disabled(stateHolder.isCreatingWithdrawal)

        }.padding(16)
    }
}
