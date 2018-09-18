package org.bitwa.wallet.view.activity

import android.app.Activity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_wallet_manage.*
import org.bitwa.wallet.R
import org.bitwa.wallet.base.BaseActivity
import org.bitwa.wallet.presenter.TransactionPresenter
import org.bitwa.wallet.presenter.WalletPresenter
import org.bitwa.wallet.view.adapter.WalletListAdapter
import org.jetbrains.anko.act
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.support.v4.onRefresh

class WalletManageActivity : BaseActivity() {
    override val mTitleText: String = "钱包管理"
    override val mRootLayoutResId: Int = R.layout.activity_wallet_manage

    private val mWalletAdapter by lazy {
        WalletListAdapter {
            this.onLoadMore = { requestWalletList(it.id) }
            this.onWalletClick = {
                // 跳转到钱包详情页
                WalletDetailActivity.show(act, it.id)
            }
            this.onRequestBalance = { adapter, wallet ->
                //请求钱包余额，然后将余额放到adapter中
                TransactionPresenter.requestBalance(wallet.address) {
                    this.onSuccess = { adapter.addWalletBalance(wallet.id, it) }
                    this.onFail = mCommonFailCallback
                }
            }
        }
    }

    override fun initWidget(savedInstanceState: Bundle?) {
        super.initWidget(savedInstanceState)
        mWalletAdapter.bindToRecyclerView(mWalletRv)
        mSwipe.onRefresh { requestWalletList() }
        requestWalletList()
        mImportWalletBtn.onClick { WalletImportActivity.show(act) }
        mNewWalletBtn.onClick { WalletCreateActivity.show(act) }
    }

    override fun onStart() {
        super.onStart()
        requestWalletList()
    }

    private fun requestWalletList(lastId: Int? = null) {
        WalletPresenter.listWallet(lastId) {
            this.onAll = { mSwipe.isRefreshing = false }
            this.onSuccess = {
                if (lastId == null) {
                    mWalletAdapter.setNewData(it.toMutableList())
                } else {
                    mWalletAdapter.addData(it)
                }
                if (it.isEmpty()) {
                    mWalletAdapter.loadMoreEnd()
                } else {
                    mWalletAdapter.loadMoreComplete()
                }
            }
            this.onFail = mCommonFailCallback
        }
    }

    companion object {
        fun show(activity: Activity) {
            activity.startActivity<WalletManageActivity>()
        }
    }
}
