package de.paulweber.spenderino.model.networking

import de.paulweber.spenderino.model.KeyValueStoring
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

interface TokenStoring {
    val token: Flow<TokenInfo?>
    fun setNewToken(token: TokenInfo?)
}

class TokenStore : TokenStoring, KoinComponent {
    private val keyValueStore: KeyValueStoring by inject()
    private val tokenKey = "BearerToken"

    override val token: StateFlow<TokenInfo?>
        get() = mutableToken

    private val mutableToken: MutableStateFlow<TokenInfo?>

    init {
        val token = retrieveToken()
        mutableToken = MutableStateFlow(token)
    }

    private fun storeToken(token: TokenInfo?) {
        val optionalString = token?.let { Json.encodeToString(token) }
        keyValueStore.store(optionalString, tokenKey)
    }

    private fun retrieveToken(): TokenInfo? {
        val string = keyValueStore.string(tokenKey)
        return string?.let { Json.decodeFromString(it) }
    }

    override fun setNewToken(token: TokenInfo?) {
        this.mutableToken.value = token
        storeToken(token)
    }
}
