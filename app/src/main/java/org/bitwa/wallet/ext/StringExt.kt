package org.bitwa.wallet.ext

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import org.spongycastle.pqc.math.linearalgebra.ByteUtils

/**
 * @author jijunpeng created on 2018/7/5.
 */
fun String.isValidPassword(): Boolean {
    return this.length >= 8
}

fun String.parseToByteArray(): ByteArray {
    return ByteUtils.fromHexString(this)
}

fun String.isValidWalletAddress(): Boolean {
    if (this.length != 42) {
        return false
    }
    if (!this.startsWith("0x")) {
        return false
    }
    return this.toLowerCase().substring(2).matches(Regex("^[0-9a-f]{40}$"))
}

fun String.copyToClip(context: Context) {
    val clip = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clipData = ClipData.newPlainText("address", this)
    clip.primaryClip = clipData
}