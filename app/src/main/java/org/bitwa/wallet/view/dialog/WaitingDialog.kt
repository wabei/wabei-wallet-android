package org.bitwa.wallet.view.dialog

import android.content.Context
import org.bitwa.wallet.R
import org.bitwa.wallet.base.dialog.AlertDialog
import org.jetbrains.anko.dip

/**
 * @author jijunpeng created on 2018/7/4.
 */
class WaitingDialog(context: Context) {

    private val dialog by lazy {
        AlertDialog.Builder(context)
                .setWidthAndHeight(context.dip(100), context.dip(100))
                .setContentView(R.layout.dialog_waiting)
                .setCancelable(false)
                .create()
    }

    fun show() {
        if (!dialog.isShowing) {
            dialog.show()
        }
    }

    val isShowing: Boolean
        get() = dialog.isShowing

    fun dismiss() {
        if (dialog.isShowing) {
            dialog.dismiss()
        }
    }
}