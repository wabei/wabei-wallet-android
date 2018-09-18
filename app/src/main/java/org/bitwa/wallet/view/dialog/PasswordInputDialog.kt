package org.bitwa.wallet.view.dialog

import android.content.Context
import android.widget.EditText
import android.widget.TextView
import org.bitwa.wallet.R
import org.bitwa.wallet.base.dialog.AlertDialog


/**
 * @author jijunpeng created on 2018/7/4.
 */
class PasswordInputDialog(context: Context) {
    private var listener: ((password: String) -> Unit)? = null
    private val dialog by lazy {
        val dialog = AlertDialog.Builder(context)
                .setContentView(R.layout.dialog_password_input)
                .fullWidth()
                .fromBottom(true)
                .setCancelable(true)
                .create()
        dialog.setOnClickListener(R.id.mCancelBtn) {
            dialog.cancel()
        }
        dialog.setOnClickListener(R.id.mConfirmBtn) {
            val passwordEt = dialog.getView<EditText>(R.id.mPasswordEt)
            val password = passwordEt.text.toString()
            dialog.dismiss()
            passwordEt.setText("")
            listener?.invoke(password)
        }
        dialog
    }

    fun configTitle(title: String): PasswordInputDialog {
        dialog.getView<TextView>(R.id.mTitleTv).text = title
        return this
    }

    fun show(listener: ((password: String) -> Unit)? = null) {
        this.listener = listener
        if (!dialog.isShowing) {
            dialog.show()
        }
    }
}