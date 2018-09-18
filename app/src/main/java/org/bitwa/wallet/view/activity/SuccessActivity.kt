package org.bitwa.wallet.view.activity

import android.app.Activity
import android.os.Bundle
import android.support.v4.app.Fragment
import kotlinx.android.synthetic.main.activity_success.*
import kotlinx.android.synthetic.main.cell_appbar.*
import org.bitwa.wallet.R
import org.bitwa.wallet.base.BaseActivity
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.startActivityForResult
import org.jetbrains.anko.support.v4.startActivityForResult

class SuccessActivity : BaseActivity() {
    override val mRootLayoutResId: Int = R.layout.activity_success

    override fun initWidget(savedInstanceState: Bundle?) {
        super.initWidget(savedInstanceState)
        mTitleTv.text = intent.getStringExtra("title")
        mSuccessInfoTv.text = intent.getStringExtra("info")
        onConfirmBtn.onClick { close() }
    }

    private fun close() {
        setResult(Activity.RESULT_OK)
        finish()
    }

    override fun onBackPressed() {
        setResult(Activity.RESULT_OK)
        super.onBackPressed()
    }

    companion object {
        fun show(activity: Activity, title: String, info: String) {
            activity.startActivity<SuccessActivity>("title" to title, "info" to info)
        }

        fun showForResult(activity: Activity, requestCode: Int, title: String, info: String) {
            activity.startActivityForResult<SuccessActivity>(requestCode, "title" to title, "info" to info)
        }

        fun showForResult(fragment: Fragment, requestCode: Int, title: String, info: String) {
            fragment.startActivityForResult<SuccessActivity>(requestCode, "title" to title, "info" to info)
        }
    }
}
