package org.bitwa.wallet.ext

/**
 * @author jijunpeng created on 2018/7/3.
 */
fun  Boolean.ifTrue(block: () -> Unit) {
    if (this) {
        block()
    }
}

fun Boolean.other(block: () -> Unit) {
    if (!this) {
        block()
    }
}