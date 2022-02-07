package de.paulweber.spenderino.model.repositories

import de.paulweber.spenderino.utility.L10n
import de.paulweber.spenderino.viewmodel.AlertViewModel
import io.ktor.client.features.ClientRequestException
import io.ktor.client.features.ServerResponseException
import io.ktor.http.HttpStatusCode
import io.ktor.utils.io.errors.IOException

sealed class RemoteException {
    object Unauthorized : RemoteException()
    data class IO(val cause: IOException) : RemoteException()
    data class Client(val cause: ClientRequestException) : RemoteException()
    data class Server(val cause: ServerResponseException) : RemoteException()
    data class Other(val cause: Throwable) : RemoteException()
}

fun Throwable.toRemoteException(): RemoteException {
    return when (this) {
        is ClientRequestException -> {
            if (this.response.status == HttpStatusCode.Unauthorized) {
                RemoteException.Unauthorized
            } else {
                RemoteException.Client(this)
            }
        }
        is ServerResponseException -> RemoteException.Server(this)
        is IOException -> RemoteException.IO(this)
        else -> RemoteException.Other(this)
    }
}

fun Throwable.toAlert(onDestroy: () -> Unit): AlertViewModel {
    return when (this) {
        is IOException -> AlertViewModel("alert_network", onDestroy)
        is ClientRequestException -> {
            val key = "alert_client_request"
            val title = L10n.format("${key}_title", this.response.status)
            val message = L10n.format("${key}_message", this.message)
            AlertViewModel(title, message, listOf(), onDestroy)
        }
        is ServerResponseException -> {
            val key = "alert_server_error"
            val title = L10n.format("${key}_title", this.response.status)
            val message = L10n.format("${key}_message", this.message ?: "")
            AlertViewModel(title, message, listOf(), onDestroy)
        }
        else -> AlertViewModel("alert_unknown", onDestroy)
    }
}
