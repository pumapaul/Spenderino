import SwiftUI
import shared

struct TransactionsScreen: View {
    let viewModel: TransactionsViewModel

    var body: some View {
        StateObservingView(state: viewModel.wrappedState) { state in
            let state = TransactionStateKs(state)
            switch state {
            case .loading: LoadingView()
            case .error: ErrorView { viewModel.perform(action: .reload) }
            case .base(let baseState):
                createList(for: baseState.transactions, isReloading: false)
            case .reloading(let baseState):
                createList(for: baseState.transactions, isReloading: true)
            }
        }
        .navigationTitle("transactions_title")
        .navigationBarTitleDisplayMode(.inline)
    }

    func createList(for transactions: [TransactionState.TransactionItem], isReloading: Bool) -> some View {
        List(transactions) {
            TransactionItemView(item: $0)
        }
        .overlay(emptyListView(transactions.isEmpty, isReloading: isReloading))
        .refreshable {
            viewModel.perform(action: .reload)
            for await value in flowAsPublisher(viewModel.wrappedState).values where value is TransactionState.Base {
                break
            }
        }

    }

    @ViewBuilder
    func emptyListView(_ isEmpty: Bool, isReloading: Bool) -> some View {
        if isEmpty {
            VStack(spacing: 32) {
                Text("transactions_empty_label")
                    .font(.footnote)
                    .foregroundColor(.secondary)
                    .multilineTextAlignment(.center)

                Button {
                    viewModel.perform(action: .reload)
                } label: {
                    if isReloading {
                        ProgressView()
                    } else {
                        VStack(spacing: 4) {
                            Image(systemName: "arrow.clockwise")
                                .resizable()
                                .aspectRatio(contentMode: .fit)
                                .frame(width: 24, height: 24)

                            Text("transactions_empty_button")
                                .font(.footnote)
                        }
                    }
                }
                .frame(height: 50)
                .disabled(isReloading)
            }.padding()
        } else {
            EmptyView()
        }
    }
}

extension TransactionState.TransactionItem: Identifiable {}

struct TransactionsView_Previews: PreviewProvider {
    static var previews: some View {
        TransactionsScreen(viewModel: .init(state: TransactionState.Loading(), onBack: {}))
    }
}
