package org.bitwa.wallet.view.fragment

import kotlinx.android.synthetic.main.fragment_wallet_import_key.*
import org.bitwa.wallet.R
import org.bitwa.wallet.presenter.WalletPresenter
import org.jetbrains.anko.support.v4.toast

/**
 * 导入密钥生成钱包
 * @author jijunpeng created on 2018/7/3.
 */
class WalletImportKeyFragment : BaseWalletImportFragment() {
    override val mRootLayoutResId: Int = R.layout.fragment_wallet_import_key

    override fun startImport() {
        val privateKey = mPrivateKeyEt.text.toString().also {
            if (it.isEmpty()) {
                toast("请输入密钥")
                return
            }
        }
        val password = mPasswordEt.text.toString().also {
            if (it.isEmpty()) {
                toast("请输入密码")
                return
            }
        }
        val repeatPassword = mRepeatPasswordEt.text.toString().also {
            if (it.isEmpty()) {
                toast("请输入确认密码")
                return
            }
            if (it != password) {
                toast("两次输入的密码不一致，请重新输入")
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
        WalletPresenter.importPrivateKey(privateKey, password, prompt) {
            this.onAll = { hideWaitDialog() }
            this.onSuccess = { gotoSuccess() }
            this.onFail = mCommonFailCallback
        }
    }
}