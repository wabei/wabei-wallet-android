package org.bitwa.wallet.view.fragment

import kotlinx.android.synthetic.main.fragment_wallet_import_mnemonic.*
import org.bitwa.wallet.R
import org.bitwa.wallet.presenter.WalletPresenter
import org.jetbrains.anko.support.v4.toast

/**
 * @author jijunpeng created on 2018/7/3.
 */
class WalletImportMnemonicFragment : BaseWalletImportFragment() {
    override val mRootLayoutResId: Int = R.layout.fragment_wallet_import_mnemonic

    override fun startImport() {
        val mnemonic = mMnemonicEt.text.toString()
        val password = mPasswordEt.text.toString()
        val prompt = mPromptEt.text.toString()
        val boolean = mAgreeCb.isChecked
        if (mnemonic.isEmpty()) {
            toast("请输入助记词")
            return
        }
        if (password.isEmpty()) {
            toast("请输入密码")
            return
        }
        if (!boolean) {
            toast("请选择同意服务及隐私条款")
            return
        }
        showWaitingDialog()
        WalletPresenter.importMnemonic(mnemonic, password, prompt) {
            this.onAll = { hideWaitDialog() }
            this.onSuccess = { gotoSuccess() }
            this.onFail = mCommonFailCallback
        }
    }
}