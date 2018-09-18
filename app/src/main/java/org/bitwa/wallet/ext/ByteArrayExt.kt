package org.bitwa.wallet.ext

import org.spongycastle.pqc.math.linearalgebra.ByteUtils

/**
 * @author jijunpeng created on 2018/7/6.
 */
fun ByteArray.toHexString(): String {
    return ByteUtils.toHexString(this)
}