package org.bitwa.wallet.ext

import android.view.View
import androidx.core.view.isGone
import androidx.core.view.isInvisible
import androidx.core.view.isVisible

/**
 * @author jijunpeng created on 2018/7/3.
 */
fun View.toGone() {
    if (!this.isGone) {
        visibility = View.GONE
    }
}

fun View.toVisible() {
    if (!this.isVisible) {
        visibility = View.VISIBLE
    }
}

fun View.toInvisible() {
    if (!this.isInvisible) {
        visibility = View.INVISIBLE
    }
}