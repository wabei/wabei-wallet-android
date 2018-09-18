package org.bitwa.wallet.view.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_wallet_create.*
import org.bitwa.wallet.R
import org.bitwa.wallet.base.BaseActivity
import org.bitwa.wallet.ext.isValidPassword
import org.bitwa.wallet.presenter.WalletPresenter
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.startActivityForResult
import org.jetbrains.anko.toast

class WalletCreateActivity : BaseActivity() {
    override val mRootLayoutResId: Int = R.layout.activity_wallet_create
    override val mHideBackBtn: Boolean = true
    override val mTitleText: String = "创建钱包"

    override fun initWidget(savedInstanceState: Bundle?) {
        super.initWidget(savedInstanceState)
        mConfirmBtn.onClick { startCreateWallet() }
    }

    private fun startCreateWallet() {
        val walletName = mWalletNameTv.text.toString()
        val password = mPasswordEt.text.toString().also {
            if (!it.isValidPassword()) {
                toast("密码强度不够，请重新输入")
                return
            }
        }
        val repeatPassword = mRepeatPasswordEt.text.toString().also {
            if (it != password) {
                toast("两次密码输入不一致，请重新输入")
                return
            }
        }
        val prompt = mPromptEt.text.toString()
        val agree = mAgreeCb.isChecked.also {
            if (!it) {
                toast("请选择同意服务及隐私条款")
                return
            }
        }
        showWaitingDialog()
        WalletPresenter.createWallet(walletName, password, prompt) {
            this.onAll = { hideWaitDialog() }
            this.onSuccess = { gotoSuccess() }
            this.onFail = mCommonFailCallback
        }
    }

    private fun gotoSuccess() {
        SuccessActivity.showForResult(this, 1, "导入成功", "恭喜您成功导入钱包")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                setResult(Activity.RESULT_OK)
                finish()
            }
            return
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    companion object {
        fun show(activity: Activity) {
            activity.startActivity<WalletCreateActivity>()
        }

        fun showForResult(activity: Activity, requestCode: Int) {
            activity.startActivityForResult<WalletCreateActivity>(requestCode)
        }
    }
}
