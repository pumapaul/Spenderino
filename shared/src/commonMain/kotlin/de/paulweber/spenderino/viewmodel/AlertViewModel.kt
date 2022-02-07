package de.paulweber.spenderino.viewmodel

import de.paulweber.spenderino.utility.L10n

data class AlertAction(val title: String, val block: () -> Unit)

data class AlertViewModel(
    val title: String,
    val message: String,
    val actions: List<AlertAction>,
    val onDestroy: () -> Unit
) {
    constructor(localizationKey: String, onDestroy: () -> Unit) : this(
        L10n.get("${localizationKey}_title"),
        L10n.get("${localizationKey}_message"),
        listOf(),
        onDestroy
    )
}
