package org.bitwa.wallet.view.dialog

import android.content.Context
import com.afollestad.materialdialogs.MaterialDialog
import org.bitwa.wallet.ext.copyToClip

/**
 * @author jijunpeng created on 2018/7/10.
 */
class TextWithCopyDialog(context: Context) {
    private var negativeListener: ((text: String) -> Unit)? = null
    private val dialog by lazy {
        MaterialDialog.Builder(context)
                .title("提示")
                .positiveText("确定")
                .neutralText("复制")
                .cancelable(false)
                .onNeutral { dialog, which ->
                    val content = dialog.contentView?.text?.toString()
                    content?.copyToClip(dialog.context)
                    negativeListener?.invoke(content ?: "")
                }
                .dismissListener { configContent("") }
                .build()
    }

    fun configTitle(string: String): TextWithCopyDialog {
        dialog.setTitle(string)
        return this
    }

    fun configContent(string: String): TextWithCopyDialog {
        dialog.setContent(string)
        return this
    }

    fun show(negative: ((text: String) -> Unit)?) {
        this.negativeListener = negative
        dialog.show()
    }
}