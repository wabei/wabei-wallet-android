package org.bitwa.wallet.view.dialog

import android.content.Context
import com.afollestad.materialdialogs.MaterialDialog

/**
 * @author jijunpeng created on 2018/7/11.
 */
class TextInputDialog(context: Context) {
    private var listener: ((input: String) -> Unit)? = null
    private val dialog by lazy {
        MaterialDialog.Builder(context)
                .title("提示")
                .input("", "", false) { dialog, input ->
                    listener?.invoke(input.toString())
                }
                .positiveText("确定")
                .negativeText("取消")
                .build()
    }

    fun configTitle(title: String): TextInputDialog {
        this.dialog.setTitle(title)
        return this
    }

    fun configHintAndPrefill(hint: String?, prefill: String?): TextInputDialog {
        this.dialog.inputEditText?.hint = hint
        this.dialog.inputEditText?.setText(prefill)
        return this
    }

    fun show(listener: ((input: String) -> Unit)? = null) {
        this.listener = listener
        dialog.show()
    }
}