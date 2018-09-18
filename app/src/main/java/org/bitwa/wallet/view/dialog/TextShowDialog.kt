package org.bitwa.wallet.view.dialog

import android.content.Context
import com.afollestad.materialdialogs.MaterialDialog

/**
 * @author jijunpeng created on 2018/7/11.
 */
class TextShowDialog(context: Context) {
    private val dialog by lazy {
        MaterialDialog.Builder(context)
                .title("提示")
                .positiveText("确定")
                .cancelable(false)
                .dismissListener { configContent("") }
                .build()
    }

    fun configTitle(string: String): TextShowDialog {
        dialog.setTitle(string)
        return this
    }

    fun configContent(string: String): TextShowDialog {
        dialog.setContent(string)
        return this
    }

    fun show() {
        dialog.show()
    }
}