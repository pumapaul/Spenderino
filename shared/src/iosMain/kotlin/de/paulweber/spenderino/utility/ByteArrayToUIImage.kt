package de.paulweber.spenderino.utility

import kotlinx.cinterop.addressOf
import kotlinx.cinterop.convert
import kotlinx.cinterop.usePinned
import platform.Foundation.NSData
import platform.Foundation.create
import platform.UIKit.UIImage

fun ByteArray.toUIImage(): UIImage = this.usePinned {
    val nsData = NSData.create(bytes = it.addressOf(0), this.size.convert())
    return UIImage(data = nsData)
}
