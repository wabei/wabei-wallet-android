package org.bitwa.wallet.view.activity

import android.app.Activity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_trans_detail.*
import org.bitwa.wallet.R
import org.bitwa.wallet.base.BaseActivity
import org.bitwa.wallet.ext.parseToGWei
import org.bitwa.wallet.ext.parseToWab
import org.bitwa.wallet.ext.toDateTimeStr
import org.bitwa.wallet.presenter.TransactionPresenter
import org.bitwa.wallet.repository.model.TransRecord
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast
import java.math.BigInteger
import java.util.*

class TransDetailActivity : BaseActivity() {
    override val mRootLayoutResId: Int = R.layout.activity_trans_detail
    override val mTitleText: String = "交易明细"

    override fun initWidget(savedInstanceState: Bundle?) {
        super.initWidget(savedInstanceState)
    }

    override fun initData(savedInstanceState: Bundle?) {
        super.initData(savedInstanceState)
        val transHash = intent.getStringExtra(KEY_TRANS_HASH)
        val transAmount = intent.getSerializableExtra(KEY_TRANS_AMOUNT) as BigInteger
        val transDate = intent.getSerializableExtra(KEY_TRANS_DATE) as Date
        if (transAmount > BigInteger.valueOf(0L)) {
            mAmountTv.text = "+${transAmount.parseToWab()} WAB"
        } else {
            mAmountTv.text = "${transAmount.parseToWab()} WAB"
        }
        mDateTv.text = transDate.toDateTimeStr()
        TransactionPresenter.requestTransRecordByTransHash(transHash) {
            this.onSuccess = {
                mFromAddressTv.text = it.from
                mToAddressTv.text = it.to
                mTransHashTv.text = it.hash
                mGasPriceTv.text = it.gasPrice.parseToGWei().number.toPlainString()
                mGasLimitTv.text = it.gas.toBigDecimal().toPlainString()
                mGasUseTv.text = (it.gasPrice * it.gas).parseToWab().number.toPlainString()
                mNonceTv.text = it.nonceRaw
            }
            this.onFail = {
                hideWaitDialog()
                toast(it)
            }
        }
    }

    companion object {
        private const val KEY_TRANS_HASH = "KEY_TRANS_HASH"
        private const val KEY_TRANS_AMOUNT = "KEY_TRANS_AMOUNT"
        private const val KEY_TRANS_DATE = "KEY_TRANS_DATE"
        fun show(activity: Activity, transRecord: TransRecord) {
            activity.startActivity<TransDetailActivity>(
                    KEY_TRANS_HASH to transRecord.transHash,
                    KEY_TRANS_AMOUNT to transRecord.amount,
                    KEY_TRANS_DATE to transRecord.date
            )
        }
    }
}
