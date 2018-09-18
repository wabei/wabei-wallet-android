package org.bitwa.wallet.view.fragment

import android.os.Bundle
import android.util.Log
import android.view.View
import kotlinx.android.synthetic.main.fragment_wallet_main.*
import org.bitwa.wallet.R
import org.bitwa.wallet.base.BaseFragment
import org.bitwa.wallet.ext.TAG
import org.bitwa.wallet.ext.parseToWab
import org.bitwa.wallet.presenter.TransactionPresenter
import org.bitwa.wallet.presenter.WalletPresenter
import org.bitwa.wallet.repository.model.WabeiWallet
import org.bitwa.wallet.view.activity.AssetDetailActivity
import org.bitwa.wallet.view.activity.WalletInitActivity
import org.bitwa.wallet.view.adapter.WalletMainAdapter
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jetbrains.anko.support.v4.act
import org.jetbrains.anko.support.v4.onRefresh
import java.math.BigInteger

/**
 * @author jijunpeng created on 2018/7/8.
 */
class WalletMainFragment : BaseFragment() {
    override val mRootLayoutResId: Int = R.layout.fragment_wallet_main

    private val mWalletAdapter by lazy {
        WalletMainAdapter {
            this.onItemClick = { _, _, _, item -> gotoAssetDetail(item) }
            this.onRequestBalance = { _, wallet -> updateWalletBalance(wallet) }
        }
    }

    override fun initWidget(view: View, bundle: Bundle?) {
        super.initWidget(view, bundle)
        mSwipe.onRefresh { updateWalletList() }
        mWalletAdapter.bindToRecyclerView(mWalletRv)
        mAddWalletBtn.onClick { gotoWalletInit() }
    }

    override fun initData(view: View, bundle: Bundle?) {
        super.initData(view, bundle)
        updateWalletList()
    }

    override fun onStart() {
        super.onStart()
        updateWalletList()
    }

    /**
     * 更新钱包列表
     */
    private fun updateWalletList(wabeiWallet: WabeiWallet? = null) {
        Log.d(TAG, "请求钱包列表")
        mSwipe.isRefreshing = true
        //更新当前钱包列表, 更新余额
        WalletPresenter.listWallet(wabeiWallet?.id) {
            this.onAll = { mSwipe.isRefreshing = false }
            this.onSuccess = {
                if (wabeiWallet == null) {
                    mWalletAdapter.setNewData(it)
                } else {
                    mWalletAdapter.addData(it)
                }
                if (it.isNotEmpty()) {
                    updateWalletList(it.last())
                } else {
                    mWalletAdapter.loadMoreEnd(true)
                }
            }
            this.onFail = mCommonFailCallback
        }
    }

    /**
     * 更新单个钱包余额
     */
    private fun updateWalletBalance(wallet: WabeiWallet) {
        TransactionPresenter.requestBalance(wallet.address) {
            this.onSuccess = {
                mWalletAdapter.addWalletBalance(wallet.id, it)
                updateTotalBalance()
            }
            this.onFail = mCommonFailCallback
        }
    }

    /**
     * 修改总余额
     */
    private fun updateTotalBalance() {
        var balance = BigInteger.ZERO
        mWalletAdapter.mBalanceMap.forEach {
            balance += it.value
        }
        mTotalBalanceTv.text = balance.parseToWab().number.toString()
    }

    private fun gotoAssetDetail(wabeiWallet: WabeiWallet) {
        AssetDetailActivity.show(act, wabeiWallet.id)
    }

    private fun gotoWalletInit() {
        WalletInitActivity.show(act)
    }
}