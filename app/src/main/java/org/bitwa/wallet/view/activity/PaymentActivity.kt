package org.bitwa.wallet.view.activity

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import com.uuzuche.lib_zxing.activity.CaptureActivity
import com.uuzuche.lib_zxing.activity.CodeUtils
import kotlinx.android.synthetic.main.activity_payment.*
import org.bitwa.wallet.R
import org.bitwa.wallet.base.BaseActivity
import org.bitwa.wallet.ext.*
import org.bitwa.wallet.presenter.TransactionPresenter
import org.bitwa.wallet.presenter.WalletPresenter
import org.bitwa.wallet.repository.model.WabeiWallet
import org.bitwa.wallet.view.dialog.PasswordInputDialog
import org.jetbrains.anko.*
import org.jetbrains.anko.sdk25.coroutines.onClick
import pub.devrel.easypermissions.EasyPermissions
import java.math.BigDecimal
import java.math.BigInteger


class PaymentActivity : BaseActivity(), EasyPermissions.PermissionCallbacks {

    override val mRootLayoutResId: Int = R.layout.activity_payment
    override val mTitleText: String = "付款"

    private val mWalletId by lazy { intent.getIntExtra(KEY_WALLET_ID, -1) }
    private var mWallet: WabeiWallet? = null
    private val REQUEST_CODE_SCAN_QR = 1
    private val REQUEST_PERMISSION_CODE_SCAN_ADDRESS = 1

    private val mFromAddress: String?
        get() = mWallet?.address
    private val mToAddress: String?
        get() = mAddressEt.text.toString().takeIf { it.isValidWalletAddress() }
    private val mGasPrice: BigInteger?
        get() = mGasPriceEt.text.toString().toBigDecimalOrNull()?.parseToGWei()?.toWeiBigInteger()
    private val mGasLimit: BigInteger?
        get() = mGasLimitEt.text.toString().toBigDecimalOrNull()?.toBigInteger()
    private val mAmount: BigInteger?
        get() = mAmountEt.text.toString().toBigDecimalOrNull()?.parseToWab()?.toWeiBigInteger()

    private val mPasswordInputDialog by lazy { PasswordInputDialog(act) }

    override fun initWidget(savedInstanceState: Bundle?) {
        super.initWidget(savedInstanceState)
        mScanBtn.onClick { startScan() }
        val textWatcher = MyTextWatcher()
        mAmountEt.addTextChangedListener(textWatcher)
        mGasPriceEt.addTextChangedListener(textWatcher)
        mSubmitBtn.onClick { submitTransaction() }
    }

    override fun initData(savedInstanceState: Bundle?) {
        super.initData(savedInstanceState)
        requestPayPreInfo()
        mGasLimitEt.setText(21000.toString())
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        // 接受扫描结果
        if (requestCode == REQUEST_CODE_SCAN_QR) {
            //处理扫描结果（在界面上显示）
            val bundle = data?.extras ?: return
            if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_SUCCESS) {
                val result = bundle.getString(CodeUtils.RESULT_STRING)
                Log.d(TAG, "扫描结果: $result")
                handleQrResult(result)
            } else if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_FAILED) {
                longToast("解析二维码失败")
            }
            return
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if (requestCode == REQUEST_PERMISSION_CODE_SCAN_ADDRESS) {
            toast("无法获取相机权限，不能进行二维码扫描")
        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        if (requestCode == REQUEST_PERMISSION_CODE_SCAN_ADDRESS) {
            callQrScanner()
        }
    }

    /**
     * 获取支付前的一些信息：账户余额、gas价格
     */
    private fun requestPayPreInfo() {
        WalletPresenter.obtainWallet(mWalletId) {
            this.onSuccess = {
                mWallet = it
                val fromAddress = it.address
                //余额
                TransactionPresenter.requestBalance(fromAddress) {
                    this.onSuccess = { mBalanceEt.setText(it.parseToWab().toString()) }
                    this.onFail = mCommonFailCallback
                }
                //gasPrice
                TransactionPresenter.requestGasPrice {
                    this.onSuccess = { mGasPriceEt.setText(it.parseToGWei().toString()) }
                    this.onFail = mCommonFailCallback
                }
            }
            this.onFail = mCommonFailCallback
        }
    }

    private fun updateGasFeeAndTotalAmount() {
        val amount = mAmountEt.text.toString().toBigDecimalOrNull()?.parseToWab()
                ?: WabeiCoin(BigDecimal.ZERO, WabeiUnit.WAB)
        val gasLimit = mGasLimitEt.text.toString().toBigDecimalOrNull()?.toBigInteger()
                ?: BigInteger("21000")
        val gasPrice = mGasPriceEt.text.toString().toBigDecimalOrNull()?.parseToGWei()?.toWeiBigInteger()
                ?: WabeiCoin(BigDecimal.ONE, WabeiUnit.GWEI).toWeiBigInteger()

        val gasUse = (gasLimit * gasPrice).parseToWab()
        val totalAmount = gasUse.number + amount.number
        mGasUseEt.setText(gasUse.number.toPlainString())
        mTotalAmountEt.setText(totalAmount.toPlainString())
    }

    private fun submitTransaction() {
        val fromAddress = mFromAddress
        if (fromAddress == null) {
            toast("请稍候，正在加载钱包")
            return
        }
        val toAddress = mToAddress
        if (toAddress == null) {
            toast("请输入有效的收款地址")
            return
        }
        val amount = mAmount
        if (amount == null) {
            toast("请输入有效转账金额")
            return
        }
        val gasLimit = mGasLimit
        if (gasLimit == null) {
            toast("请输入Gas限制")
            return
        }
        val gasPrice = mGasPrice
        if (gasPrice == null) {
            toast("请输入gas价格")
            return
        }
        //显示输入交易密码
        mPasswordInputDialog.show {
            //完成交易信息
            showWaitingDialog()
            TransactionPresenter.sendTransaction(mWalletId, toAddress, amount, gasPrice, gasLimit, it) {
                this.onAll = { hideWaitDialog() }
                this.onSuccess = { gotoPaySuccess() }
                this.onFail = { toast(it) }
            }
        }
    }

    private fun gotoPaySuccess() {
        SuccessActivity.show(act, "支付成功", "恭喜您转账成功")
        finish()
    }

    private fun startScan() {
        val permission = Manifest.permission.CAMERA
        if (EasyPermissions.hasPermissions(act, permission)) {
            callQrScanner()
        } else {
            EasyPermissions.requestPermissions(this@PaymentActivity, "扫描二维码需要相机权限", REQUEST_PERMISSION_CODE_SCAN_ADDRESS, permission)
        }
    }

    private fun callQrScanner() {
        act.startActivityForResult<CaptureActivity>(REQUEST_CODE_SCAN_QR)
    }

    private fun handleQrResult(qrResult: String) {
        val split = qrResult.split(":").takeIf { it.size == 2 }
        if (split == null) {
            toast("错误的二维码信息")
            return
        }
        when (split[0]) {
            "ethereum" -> mAddressEt.setText(split[1])
            "wabei" -> mAddressEt.setText(split[1])
            else -> toast("错误的二维码信息")
        }
    }

    companion object {
        private val KEY_WALLET_ID = "KEY_WALLET_ID"
        fun show(activity: Activity, walletId: Int) {
            activity.startActivity<PaymentActivity>(KEY_WALLET_ID to walletId)
        }
    }

    inner class MyTextWatcher : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            updateGasFeeAndTotalAmount()
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        }
    }
}
