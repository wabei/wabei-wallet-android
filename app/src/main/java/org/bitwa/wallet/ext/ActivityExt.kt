package org.bitwa.wallet.ext

import android.app.Activity
import org.bitwa.wallet.WabeiApp

/**
 * @author jijunpeng created on 2018/7/5.
 */
val Activity.wabeiApp: WabeiApp
    get() = this.applicationContext as WabeiApp