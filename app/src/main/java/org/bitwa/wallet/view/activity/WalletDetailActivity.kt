package org.bitwa.wallet.view.activity

import android.app.Activity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_wallet_detail.*
import kotlinx.android.synthetic.main.cell_appbar.*
import org.bitwa.wallet.R
import org.bitwa.wallet.base.BaseActivity
import org.bitwa.wallet.ext.parseToWab
import org.bitwa.wallet.presenter.TransactionPresenter
import org.bitwa.wallet.presenter.WalletPresenter
import org.bitwa.wallet.repository.model.WabeiWallet
import org.bitwa.wallet.view.dialog.*
import org.jetbrains.anko.act
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast

/**
 * 钱包详情
 */
class WalletDetailActivity : BaseActivity() {
    override val mRootLayoutResId: Int = R.layout.activity_wallet_detail
    private var mWallet: WabeiWallet? = null
    private val mWalletId: Int?
        get() {
            val walletId = mWallet?.id
            if (walletId == null) {
                toast("正在加载钱包")
                return null
            }
            return walletId
        }
    private val mPasswordInputDialog by lazy { PasswordInputDialog(act) }
    private val mNewPasswordInputDialog by lazy { NewPasswordInputDialog(act) }
    private val mExportTextDialog by lazy { TextWithCopyDialog(act) }
    private val mTextInputDialog by lazy { TextInputDialog(act) }
    private val mTextShowDialog by lazy { TextShowDialog(act) }

    override fun initWidget(savedInstanceState: Bundle?) {
        super.initWidget(savedInstanceState)
        val walletId = intent.getIntExtra(KEY_WALLET_ID, -1)
        mChangeToDefaultBtn.isEnabled = WalletPresenter.defaultWalletId != walletId
    }

    override fun initData(savedInstanceState: Bundle?) {
        super.initData(savedInstanceState)
        val walletId = intent.getIntExtra(KEY_WALLET_ID, -1)
        WalletPresenter.obtainWallet(walletId) {
            this.onSuccess = {
                mWallet = it
                updateWalletInfo(it)
            }
            this.onFail = {
                finish()
                toast(it)
            }
        }
        mChangeToDefaultBtn.onClick {
            WalletPresenter.defaultWalletId = mWalletId ?: return@onClick
            mChangeToDefaultBtn.isEnabled = false
        }
        mUpdateWalletNameBtn.onClick {
            // 修改钱包名称
            updateWalletName(mWalletId ?: return@onClick)
        }
        mUpdatePasswordBtn.onClick {
            // 修改密码
            startUpdatePassword(mWalletId ?: return@onClick)
        }
        mPromptBtn.onClick {
            //显示提示信息
            showPrompt()
        }
        mExportPriKeyBtn.onClick {
            // 导出密钥
            exportPrivateKey(mWalletId ?: return@onClick)
        }
        mExportKeystoreBtn.onClick {
            // 导出keystore
            exportKeystore(mWalletId ?: return@onClick)
        }
        mExportMnemonicBtn.onClick {
            // 导出助记词
            exportMnemonic(mWalletId ?: return@onClick)
        }
        mRemoveWalletBtn.onClick {
            // 移除钱包
            deleteWallet(walletId ?: return@onClick)
        }
    }

    private fun updateWalletInfo(wallet: WabeiWallet) {
        mTitleTv.text = wallet.name
        mWalletNameTv.text = wallet.name
        mAddressTv.text = wallet.address
        mExportMnemonicBtn.isEnabled = wallet.mnemonic != null
        TransactionPresenter.requestBalance(wallet.address) {
            this.onSuccess = {
                mBalanceTv.text = "${it.parseToWab()} WAB"
            }
            this.onFail = mCommonFailCallback
        }
    }

    private fun updateWalletName(walletId: Int) {
        mTextInputDialog.configTitle("更改钱包名称")
                .configHintAndPrefill("请输入钱包名称", mWalletNameTv.text.toString())
                .show {
                    WalletPresenter.updateWalletName(walletId, it) {
                        this.onFail = { toast(it) }
                        this.onSuccess = {
                            mWalletNameTv.text = it
                            toast("钱包名称更新成功")
                        }
                    }
                }
    }

    private fun startUpdatePassword(walletId: Int) {
        mPasswordInputDialog.configTitle("请输入旧密码").show {
            val oldPassword = it
            WalletPresenter.validWalletPassword(walletId, it) {
                this.onFail = { toast(it) }
                this.onSuccess = {
                    updatePassword(walletId, oldPassword)
                }
            }
        }
    }

    private fun updatePassword(walletId: Int, password: String) {
        mNewPasswordInputDialog.configTitle("请输入新密码")
                .onRepeatWrong { toast("两次输入的密码不一致，请重新输入") }
                .show {
                    WalletPresenter.updateWalletPassword(walletId, password, it) {
                        this.onFail = { toast(it) }
                        this.onSuccess = { toast("密码修改成功") }
                    }
                }
    }

    private fun showPrompt() {
        val wallet = mWallet
        if (wallet == null) {
            toast("正在加载钱包")
            return
        }
        val prompt = wallet.prompt
        if (prompt.isNullOrBlank()) {
            mTextShowDialog.configTitle("无提示信息").show()
        } else {
            mTextShowDialog.configTitle("提示信息").configContent(prompt!!).show()
        }
    }

    private fun exportPrivateKey(walletId: Int) {
        mPasswordInputDialog.show {
            WalletPresenter.exportPrivateKey(walletId, it) {
                this.onFail = { toast(it) }
                this.onSuccess = {
                    // 显示私钥
                    mExportTextDialog.configTitle("私钥").configContent(it).show {
                        toast("私钥已经复制")
                    }
                }
            }
        }
    }

    private fun exportKeystore(walletId: Int) {
        mPasswordInputDialog.show {
            showWaitingDialog()
            WalletPresenter.exportKeystore(walletId, it) {
                this.onAll = { hideWaitDialog() }
                this.onFail = { toast(it) }
                this.onSuccess = {
                    // 显示Keystore信息
                    mExportTextDialog.configTitle("Keystore信息").configContent(it).show {
                        toast("Keystore信息已经复制")
                    }
                }
            }
        }
    }

    private fun exportMnemonic(walletId: Int) {
        mPasswordInputDialog.show {
            WalletPresenter.exportMnemonic(walletId, it) {
                this.onFail = { toast(it) }
                this.onSuccess = {
                    // 显示助记词
                    mExportTextDialog.configTitle("助记词").configContent(it).show {
                        toast("助记词已经复制")
                    }
                }
            }
        }
    }

    private fun deleteWallet(walletId: Int) {
        mPasswordInputDialog.show {
            WalletPresenter.deleteWallet(walletId, it) {
                this.onFail = { toast(it) }
                this.onSuccess = {
                    toast("已经删除钱包：${it.name}")
                    finish()
                }
            }
        }
    }

    companion object {
        private const val KEY_WALLET_ID = "KEY_WALLET_ID"
        fun show(activity: Activity, walletId: Int) {
            activity.startActivity<WalletDetailActivity>(KEY_WALLET_ID to walletId)
        }
    }
}
