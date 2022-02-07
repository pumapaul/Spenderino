package de.paulweber.spenderino.model.repositories.donation

sealed class StripeResult {
    object Completed : StripeResult()
    object Canceled : StripeResult()
    data class Failed(val localizedErrorMessage: String) : StripeResult()
}
