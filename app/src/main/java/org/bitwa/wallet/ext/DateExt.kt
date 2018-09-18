package org.bitwa.wallet.ext

import java.text.SimpleDateFormat
import java.util.*

/**
 * @author jijunpeng created on 2018/7/9.
 */
private val dateFormat by lazy { SimpleDateFormat("yyyy/MM/dd", Locale.getDefault()) }
private val dateTimeFormat by lazy { SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault()) }

fun Date.toDateStr(): String {
    return dateFormat.format(this)
}

fun Date.toDateTimeStr(): String {
    return dateTimeFormat.format(this)
}