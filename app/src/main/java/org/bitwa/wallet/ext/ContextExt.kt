package org.bitwa.wallet.ext

import android.content.Context

/**
 * @author jijunpeng created on 2018/7/2.
 */
val Context.versionName
    get() = this.packageManager.getPackageInfo(this.packageName, 0).versionName
val Context.versionCode
    get() = this.packageManager.getPackageInfo(this.packageName, 0).versionCode