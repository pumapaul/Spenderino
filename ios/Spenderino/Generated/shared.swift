import shared

/**
 * selector: ClassContext/Spenderino:shared/de/paulweber/spenderino/model/repositories/RemoteException */
public enum RemoteExceptionKs {

  case unauthorized
  case iO(RemoteException.IO)
  case client(RemoteException.Client)
  case server(RemoteException.Server)
  case other(RemoteException.Other)

  public init(_ obj: RemoteException) {
    if obj is shared.RemoteException.Unauthorized {
      self = .unauthorized
    } else if let obj = obj as? shared.RemoteException.IO {
      self = .iO(obj)
    } else if let obj = obj as? shared.RemoteException.Client {
      self = .client(obj)
    } else if let obj = obj as? shared.RemoteException.Server {
      self = .server(obj)
    } else if let obj = obj as? shared.RemoteException.Other {
      self = .other(obj)
    } else {
      fatalError("RemoteExceptionKs not syncronized with RemoteException class")
    }
  }

}

/**
 * selector: ClassContext/Spenderino:shared/de/paulweber/spenderino/model/repositories/user/ProfileState */
public enum ProfileStateKs {

  case error
  case loading
  case none
  case present(ProfileStatePresent)

  public init(_ obj: ProfileState) {
    if obj is shared.ProfileStateError {
      self = .error
    } else if obj is shared.ProfileStateLoading {
      self = .loading
    } else if obj is shared.ProfileStateNone {
      self = .none
    } else if let obj = obj as? shared.ProfileStatePresent {
      self = .present(obj)
    } else {
      fatalError("ProfileStateKs not syncronized with ProfileState class")
    }
  }

}

/**
 * selector: ClassContext/Spenderino:shared/de/paulweber/spenderino/model/repositories/user/UserState */
public enum UserStateKs {

  case error
  case loading
  case anonymous(UserStateAnonymous)
  case registered(UserStateRegistered)

  public init(_ obj: UserState) {
    if obj is shared.UserStateError {
      self = .error
    } else if obj is shared.UserStateLoading {
      self = .loading
    } else if let obj = obj as? shared.UserStateAnonymous {
      self = .anonymous(obj)
    } else if let obj = obj as? shared.UserStateRegistered {
      self = .registered(obj)
    } else {
      fatalError("UserStateKs not syncronized with UserState class")
    }
  }

}

/**
 * selector: ClassContext/Spenderino:shared/de/paulweber/spenderino/model/repositories/donation/StripeResult */
public enum StripeResultKs {

  case completed
  case canceled
  case failed(StripeResult.Failed)

  public init(_ obj: StripeResult) {
    if obj is shared.StripeResult.Completed {
      self = .completed
    } else if obj is shared.StripeResult.Canceled {
      self = .canceled
    } else if let obj = obj as? shared.StripeResult.Failed {
      self = .failed(obj)
    } else {
      fatalError("StripeResultKs not syncronized with StripeResult class")
    }
  }

}

/**
 * selector: ClassContext/Spenderino:shared/de/paulweber/spenderino/viewmodel/AccountAction */
public enum AccountActionKs {

  case login
  case register
  case createProfile
  case logout
  case confirmLogout
  case changeEmailText(AccountAction.ChangeEmailText)
  case changePasswordText(AccountAction.ChangePasswordText)
  case changeUsernameText(AccountAction.ChangeUsernameText)

  public init(_ obj: AccountAction) {
    if obj is shared.AccountAction.Login {
      self = .login
    } else if obj is shared.AccountAction.Register {
      self = .register
    } else if obj is shared.AccountAction.CreateProfile {
      self = .createProfile
    } else if obj is shared.AccountAction.Logout {
      self = .logout
    } else if obj is shared.AccountAction.ConfirmLogout {
      self = .confirmLogout
    } else if let obj = obj as? shared.AccountAction.ChangeEmailText {
      self = .changeEmailText(obj)
    } else if let obj = obj as? shared.AccountAction.ChangePasswordText {
      self = .changePasswordText(obj)
    } else if let obj = obj as? shared.AccountAction.ChangeUsernameText {
      self = .changeUsernameText(obj)
    } else {
      fatalError("AccountActionKs not syncronized with AccountAction class")
    }
  }

}

/**
 * selector: ClassContext/Spenderino:shared/de/paulweber/spenderino/viewmodel/AccountRoute */
public enum AccountRouteKs {

  case alert(AccountRoute.Alert)
  case logoutAlert(AccountRoute.LogoutAlert)

  public init(_ obj: AccountRoute) {
    if let obj = obj as? shared.AccountRoute.Alert {
      self = .alert(obj)
    } else if let obj = obj as? shared.AccountRoute.LogoutAlert {
      self = .logoutAlert(obj)
    } else {
      fatalError("AccountRouteKs not syncronized with AccountRoute class")
    }
  }

}

/**
 * selector: ClassContext/Spenderino:shared/de/paulweber/spenderino/viewmodel/AccountState */
public enum AccountStateKs {

  case loading
  case error
  case anonymous(AccountState.Anonymous)
  case registered(AccountState.Registered)
  case setupComplete(AccountState.SetupComplete)

  public init(_ obj: AccountState) {
    if obj is shared.AccountState.Loading {
      self = .loading
    } else if obj is shared.AccountState.Error {
      self = .error
    } else if let obj = obj as? shared.AccountState.Anonymous {
      self = .anonymous(obj)
    } else if let obj = obj as? shared.AccountState.Registered {
      self = .registered(obj)
    } else if let obj = obj as? shared.AccountState.SetupComplete {
      self = .setupComplete(obj)
    } else {
      fatalError("AccountStateKs not syncronized with AccountState class")
    }
  }

}

/**
 * selector: ClassContext/Spenderino:shared/de/paulweber/spenderino/viewmodel/AppAction */
public enum AppActionKs {

  case deepLink(AppAction.DeepLink)

  public init(_ obj: AppAction) {
    if let obj = obj as? shared.AppAction.DeepLink {
      self = .deepLink(obj)
    } else {
      fatalError("AppActionKs not syncronized with AppAction class")
    }
  }

}

/**
 * selector: ClassContext/Spenderino:shared/de/paulweber/spenderino/viewmodel/DonationAction */
public enum DonationActionKs {

  case reload
  case changeDonationValue(DonationAction.ChangeDonationValue)
  case changeTransactionInProgress(DonationAction.ChangeTransactionInProgress)
  case transactionResult(DonationAction.TransactionResult)

  public init(_ obj: DonationAction) {
    if obj is shared.DonationAction.Reload {
      self = .reload
    } else if let obj = obj as? shared.DonationAction.ChangeDonationValue {
      self = .changeDonationValue(obj)
    } else if let obj = obj as? shared.DonationAction.ChangeTransactionInProgress {
      self = .changeTransactionInProgress(obj)
    } else if let obj = obj as? shared.DonationAction.TransactionResult {
      self = .transactionResult(obj)
    } else {
      fatalError("DonationActionKs not syncronized with DonationAction class")
    }
  }

}

/**
 * selector: ClassContext/Spenderino:shared/de/paulweber/spenderino/viewmodel/DonationRoute */
public enum DonationRouteKs {

  case alert(DonationRoute.Alert)

  public init(_ obj: DonationRoute) {
    if let obj = obj as? shared.DonationRoute.Alert {
      self = .alert(obj)
    } else {
      fatalError("DonationRouteKs not syncronized with DonationRoute class")
    }
  }

}

/**
 * selector: ClassContext/Spenderino:shared/de/paulweber/spenderino/viewmodel/DonationScannerAction */
public enum DonationScannerActionKs {

  case codeScanned(DonationScannerAction.CodeScanned)
  case scanError(DonationScannerAction.ScanError)

  public init(_ obj: DonationScannerAction) {
    if let obj = obj as? shared.DonationScannerAction.CodeScanned {
      self = .codeScanned(obj)
    } else if let obj = obj as? shared.DonationScannerAction.ScanError {
      self = .scanError(obj)
    } else {
      fatalError("DonationScannerActionKs not syncronized with DonationScannerAction class")
    }
  }

}

/**
 * selector: ClassContext/Spenderino:shared/de/paulweber/spenderino/viewmodel/DonationScannerRoute */
public enum DonationScannerRouteKs {

  case donation(DonationScannerRoute.Donation)
  case alert(DonationScannerRoute.Alert)

  public init(_ obj: DonationScannerRoute) {
    if let obj = obj as? shared.DonationScannerRoute.Donation {
      self = .donation(obj)
    } else if let obj = obj as? shared.DonationScannerRoute.Alert {
      self = .alert(obj)
    } else {
      fatalError("DonationScannerRouteKs not syncronized with DonationScannerRoute class")
    }
  }

}

/**
 * selector: ClassContext/Spenderino:shared/de/paulweber/spenderino/viewmodel/DonationState.Error */
public enum DonationStateErrorKs {

  case networkError
  case unknownCode

  public init(_ obj: DonationState.Error) {
    if obj is shared.DonationState.ErrorNetworkError {
      self = .networkError
    } else if obj is shared.DonationState.ErrorUnknownCode {
      self = .unknownCode
    } else {
      fatalError("DonationStateErrorKs not syncronized with DonationState.Error class")
    }
  }

}

/**
 * selector: ClassContext/Spenderino:shared/de/paulweber/spenderino/viewmodel/DonationState */
public enum DonationStateKs {

  case loading
  case error(DonationState.Error)
  case base(DonationState.Base)
  case success(DonationState.Success)

  public init(_ obj: DonationState) {
    if obj is shared.DonationState.Loading {
      self = .loading
    } else if let obj = obj as? shared.DonationState.Error {
      self = .error(obj)
    } else if let obj = obj as? shared.DonationState.Base {
      self = .base(obj)
    } else if let obj = obj as? shared.DonationState.Success {
      self = .success(obj)
    } else {
      fatalError("DonationStateKs not syncronized with DonationState class")
    }
  }

}

/**
 * selector: ClassContext/Spenderino:shared/de/paulweber/spenderino/viewmodel/PayoutState */
public enum PayoutStateKs {

  case loading
  case error
  case qRCode(PayoutState.QRCode)
  case success

  public init(_ obj: PayoutState) {
    if obj is shared.PayoutState.Loading {
      self = .loading
    } else if obj is shared.PayoutState.Error {
      self = .error
    } else if let obj = obj as? shared.PayoutState.QRCode {
      self = .qRCode(obj)
    } else if obj is shared.PayoutState.Success {
      self = .success
    } else {
      fatalError("PayoutStateKs not syncronized with PayoutState class")
    }
  }

}

/**
 * selector: ClassContext/Spenderino:shared/de/paulweber/spenderino/viewmodel/PreferencesRoute */
public enum PreferencesRouteKs {

  case account(PreferencesRoute.Account)
  case transactions(PreferencesRoute.Transactions)

  public init(_ obj: PreferencesRoute) {
    if let obj = obj as? shared.PreferencesRoute.Account {
      self = .account(obj)
    } else if let obj = obj as? shared.PreferencesRoute.Transactions {
      self = .transactions(obj)
    } else {
      fatalError("PreferencesRouteKs not syncronized with PreferencesRoute class")
    }
  }

}

/**
 * selector: ClassContext/Spenderino:shared/de/paulweber/spenderino/viewmodel/PreferencesState */
public enum PreferencesStateKs {

  case loading
  case error
  case anonymous
  case registered(PreferencesState.Registered)
  case setupComplete(PreferencesState.SetupComplete)

  public init(_ obj: PreferencesState) {
    if obj is shared.PreferencesState.Loading {
      self = .loading
    } else if obj is shared.PreferencesState.Error {
      self = .error
    } else if obj is shared.PreferencesState.Anonymous {
      self = .anonymous
    } else if let obj = obj as? shared.PreferencesState.Registered {
      self = .registered(obj)
    } else if let obj = obj as? shared.PreferencesState.SetupComplete {
      self = .setupComplete(obj)
    } else {
      fatalError("PreferencesStateKs not syncronized with PreferencesState class")
    }
  }

}

/**
 * selector: ClassContext/Spenderino:shared/de/paulweber/spenderino/viewmodel/RecipientAction */
public enum RecipientActionKs {

  case login
  case createProfile
  case reloadRecipient
  case reloadBalance
  case createWithdrawal(RecipientAction.CreateWithdrawal)

  public init(_ obj: RecipientAction) {
    if obj is shared.RecipientAction.Login {
      self = .login
    } else if obj is shared.RecipientAction.CreateProfile {
      self = .createProfile
    } else if obj is shared.RecipientAction.ReloadRecipient {
      self = .reloadRecipient
    } else if obj is shared.RecipientAction.ReloadBalance {
      self = .reloadBalance
    } else if let obj = obj as? shared.RecipientAction.CreateWithdrawal {
      self = .createWithdrawal(obj)
    } else {
      fatalError("RecipientActionKs not syncronized with RecipientAction class")
    }
  }

}

/**
 * selector: ClassContext/Spenderino:shared/de/paulweber/spenderino/viewmodel/RecipientRoute */
public enum RecipientRouteKs {

  case account(RecipientRoute.Account)
  case alert(RecipientRoute.Alert)
  case payout(RecipientRoute.Payout)

  public init(_ obj: RecipientRoute) {
    if let obj = obj as? shared.RecipientRoute.Account {
      self = .account(obj)
    } else if let obj = obj as? shared.RecipientRoute.Alert {
      self = .alert(obj)
    } else if let obj = obj as? shared.RecipientRoute.Payout {
      self = .payout(obj)
    } else {
      fatalError("RecipientRouteKs not syncronized with RecipientRoute class")
    }
  }

}

/**
 * selector: ClassContext/Spenderino:shared/de/paulweber/spenderino/viewmodel/RecipientState.BalanceState */
public enum RecipientStateBalanceStateKs {

  case loading
  case error
  case base(RecipientState.BalanceStateBase)
  case reloading(RecipientState.BalanceStateReloading)

  public init(_ obj: RecipientState.BalanceState) {
    if obj is shared.RecipientState.BalanceStateLoading {
      self = .loading
    } else if obj is shared.RecipientState.BalanceStateError {
      self = .error
    } else if let obj = obj as? shared.RecipientState.BalanceStateBase {
      self = .base(obj)
    } else if let obj = obj as? shared.RecipientState.BalanceStateReloading {
      self = .reloading(obj)
    } else {
      fatalError("RecipientStateBalanceStateKs not syncronized with RecipientState.BalanceState class")
    }
  }

}

/**
 * selector: ClassContext/Spenderino:shared/de/paulweber/spenderino/viewmodel/RecipientState */
public enum RecipientStateKs {

  case pager(RecipientState.Pager)
  case registered(RecipientState.Registered)
  case anonymous
  case loading
  case error

  public init(_ obj: RecipientState) {
    if let obj = obj as? shared.RecipientState.Pager {
      self = .pager(obj)
    } else if let obj = obj as? shared.RecipientState.Registered {
      self = .registered(obj)
    } else if obj is shared.RecipientState.Anonymous {
      self = .anonymous
    } else if obj is shared.RecipientState.Loading {
      self = .loading
    } else if obj is shared.RecipientState.Error {
      self = .error
    } else {
      fatalError("RecipientStateKs not syncronized with RecipientState class")
    }
  }

}

/**
 * selector: ClassContext/Spenderino:shared/de/paulweber/spenderino/viewmodel/TabAction */
public enum TabActionKs {

  case selectTab(TabAction.SelectTab)

  public init(_ obj: TabAction) {
    if let obj = obj as? shared.TabAction.SelectTab {
      self = .selectTab(obj)
    } else {
      fatalError("TabActionKs not syncronized with TabAction class")
    }
  }

}

/**
 * selector: ClassContext/Spenderino:shared/de/paulweber/spenderino/viewmodel/TransactionState.TransactionItem */
public enum TransactionStateTransactionItemKs {

  case withdrawalItem(TransactionState.TransactionItemWithdrawalItem)
  case donationItem(TransactionState.TransactionItemDonationItem)

  public init(_ obj: TransactionState.TransactionItem) {
    if let obj = obj as? shared.TransactionState.TransactionItemWithdrawalItem {
      self = .withdrawalItem(obj)
    } else if let obj = obj as? shared.TransactionState.TransactionItemDonationItem {
      self = .donationItem(obj)
    } else {
      fatalError("TransactionStateTransactionItemKs not syncronized with TransactionState.TransactionItem class")
    }
  }

}

/**
 * selector: ClassContext/Spenderino:shared/de/paulweber/spenderino/viewmodel/TransactionState */
public enum TransactionStateKs {

  case error
  case loading
  case reloading(TransactionState.Reloading)
  case base(TransactionState.Base)

  public init(_ obj: TransactionState) {
    if obj is shared.TransactionState.Error {
      self = .error
    } else if obj is shared.TransactionState.Loading {
      self = .loading
    } else if let obj = obj as? shared.TransactionState.Reloading {
      self = .reloading(obj)
    } else if let obj = obj as? shared.TransactionState.Base {
      self = .base(obj)
    } else {
      fatalError("TransactionStateKs not syncronized with TransactionState class")
    }
  }

}
