package de.paulweber.spenderino.utility

import android.content.Context

actual object L10n {
    lateinit var context: Context

    actual fun get(id: String): String {
        return if (this::context.isInitialized) {
            val resourceId = context.resources.getIdentifier(id, "string", context.packageName)
            if (resourceId == 0) return id
            context.getString(resourceId)
        } else {
            id
        }
    }

    actual fun get(id: String, quantity: Int): String {
        return if (this::context.isInitialized) {
            val resourceId = context.resources.getIdentifier(id, "plurals", context.packageName)
            if (resourceId == 0) return id
            context.resources.getQuantityString(resourceId, quantity, quantity)
        } else {
            id
        }
    }

    actual fun format(id: String, vararg formatArgs: Any): String {
        return if (this::context.isInitialized) {
            val resourceId = context.resources.getIdentifier(id, "string", context.packageName)
            if (resourceId == 0) return id
            context.resources.getString(resourceId, *formatArgs)
        } else {
            var result = id
            formatArgs.iterator().forEach {
                result = "$result+$it"
            }
            result
        }
    }
}
