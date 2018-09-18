package org.bitwa.wallet.view.activity

import android.app.Activity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_asset_detail.*
import org.bitwa.wallet.R
import org.bitwa.wallet.base.BaseActivity
import org.bitwa.wallet.presenter.TransactionPresenter
import org.bitwa.wallet.presenter.WalletPresenter
import org.bitwa.wallet.view.adapter.TransRecordAdapter
import org.jetbrains.anko.act
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.support.v4.onRefresh

class AssetDetailActivity : BaseActivity() {
    override val mTitleText: String = "资产"
    override val mRootLayoutResId: Int = R.layout.activity_asset_detail
    private val mWalletId by lazy { intent.getIntExtra(KEY_WALLET_ID, -1) }
    private val mTransRecordAdapter by lazy {
        TransRecordAdapter {
            this.onItemClick = {
                // 进入交易详情
                TransDetailActivity.show(act, it)
            }
            this.onLoadMore = { requestTransRecord(it) }
        }
    }

    override fun initWidget(savedInstanceState: Bundle?) {
        super.initWidget(savedInstanceState)
        mTransRecordAdapter.bindToRecyclerView(mTransRecordRv)
        mPayBtn.onClick { PaymentActivity.show(act, mWalletId) }
        mGatheringBtn.onClick {
            // 收款界面
            ReceiptActivity.show(act, mWalletId)
        }
        mSwipe.onRefresh { requestTransRecord() }
    }

    override fun initData(savedInstanceState: Bundle?) {
        super.initData(savedInstanceState)
        val walletId = intent.getIntExtra(KEY_WALLET_ID, -1)
        WalletPresenter.obtainWallet(walletId) {
            this.onSuccess = {
                mWalletNameTv.text = it.name
                mAddressTv.text = it.address
            }
            this.onFail = mCommonFailCallback
        }
        requestTransRecord()
    }

    private fun requestTransRecord(lastRecordId: Long? = null) {
        TransactionPresenter.requestTransRecord(mWalletId, lastRecordId) {
            this.onAll = { mSwipe.isRefreshing = false }
            this.onSuccess = {
                if (lastRecordId == null) {
                    mTransRecordAdapter.setNewData(it.toMutableList())
                } else {
                    mTransRecordAdapter.addData(it)
                }
                if (it.isEmpty()) {
                    mTransRecordAdapter.loadMoreEnd()
                } else {
                    mTransRecordAdapter.loadMoreComplete()
                }
            }
            this.onFail = mCommonFailCallback
        }
    }

    companion object {
        const val KEY_WALLET_ID = "KEY_WALLET_ID"
        fun show(activity: Activity, walletId: Int) {
            activity.startActivity<AssetDetailActivity>(KEY_WALLET_ID to walletId)
        }
    }
}
