package org.bitwa.wallet.view.fragment

import kotlinx.android.synthetic.main.fragment_wallet_import_official.*
import org.bitwa.wallet.R
import org.bitwa.wallet.presenter.WalletPresenter
import org.jetbrains.anko.support.v4.toast

/**
 * @author jijunpeng created on 2018/7/3.
 */
//TODO JiJunpeng created. 导入官方钱包文件
class WalletImportOfficialFragment : BaseWalletImportFragment() {
    override val mRootLayoutResId: Int = R.layout.fragment_wallet_import_official

    override fun startImport() {
        val keyStore = mKeyStoreEt.text.toString().also {
            if (it.isEmpty()) {
                toast("请输入keyStore信息")
                return
            }
        }
        val password = mPasswordEt.text.toString()
        val agree = mAgreeCb.isChecked.also {
            if (!it) {
                toast("请选择同意服务及隐私条款")
                return
            }
        }
        showWaitingDialog()
        WalletPresenter.importKeyStore(keyStore, password) {
            this.onAll = { hideWaitDialog() }
            this.onSuccess = { gotoSuccess() }
            this.onFail = mCommonFailCallback
        }
    }
}