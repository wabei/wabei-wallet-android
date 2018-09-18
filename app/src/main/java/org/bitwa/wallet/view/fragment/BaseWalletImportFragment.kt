package org.bitwa.wallet.view.fragment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import org.bitwa.wallet.R
import org.bitwa.wallet.base.BaseFragment
import org.bitwa.wallet.view.activity.SuccessActivity
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jetbrains.anko.support.v4.act

/**
 * @author jijunpeng created on 2018/7/5.
 */
abstract class BaseWalletImportFragment : BaseFragment() {
    override fun initWidget(view: View, bundle: Bundle?) {
        super.initWidget(view, bundle)
        view.findViewById<View>(R.id.mConfirmBtn)?.onClick { startImport() }
    }

    protected abstract fun startImport()

    protected fun gotoSuccess() {
        SuccessActivity.showForResult(this, 1, "导入成功", "恭喜您成功导入钱包")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                act.setResult(Activity.RESULT_OK)
                act.finish()
            }
            return
        }
        super.onActivityResult(requestCode, resultCode, data)
    }
}