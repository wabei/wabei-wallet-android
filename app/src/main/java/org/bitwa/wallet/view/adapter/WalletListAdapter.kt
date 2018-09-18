package org.bitwa.wallet.view.adapter

import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import org.bitwa.wallet.R
import org.bitwa.wallet.ext.parseToWab
import org.bitwa.wallet.repository.model.WabeiWallet
import java.math.BigInteger

/**
 * @author jijunpeng created on 2018/7/10.
 */
class WalletListAdapter(init: Listener.() -> Unit) : BaseQuickAdapter<WabeiWallet, BaseViewHolder>(R.layout.item_wallet) {
    private val mListener by lazy { Listener().apply(init) }
    private val mBalanceMap = hashMapOf<Int, BigInteger>()

    override fun convert(helper: BaseViewHolder, item: WabeiWallet) {
        val balance = mBalanceMap[item.id]
        val balanceStr = if (balance == null) {
            mListener.onRequestBalance?.invoke(this, item)
            "0 WAB"
        } else {
            balance.parseToWab().number.toPlainString() + " WAB"
        }
        helper.setText(R.id.mWalletNameTv, item.name)
                .setText(R.id.mAddressTv, item.address)
                .setText(R.id.mBalanceTv, balanceStr)
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        val context = recyclerView.context
        recyclerView.layoutManager = LinearLayoutManager(context)
        setOnItemClickListener { _, _, position ->
            val wallet = getItem(position) ?: return@setOnItemClickListener
            mListener.onWalletClick?.invoke(wallet)
        }
        setOnLoadMoreListener({
            val lastOne = data.last()
            mListener.onLoadMore?.invoke(lastOne)
        }, recyclerView)
    }

    override fun setNewData(data: MutableList<WabeiWallet>?) {
        super.setNewData(data)
        mBalanceMap.clear()
    }

    fun addWalletBalance(walletId: Int, balance: BigInteger): WalletListAdapter {
        mBalanceMap[walletId] = balance
        val index = data.indexOfFirst { walletId == it.id }
        notifyItemChanged(index)
        return this
    }

    class Listener {
        var onRequestBalance: ((adapter: WalletListAdapter, wallet: WabeiWallet) -> Unit)? = null
        var onLoadMore: ((lastWallet: WabeiWallet) -> Unit)? = null
        var onWalletClick: ((wallet: WabeiWallet) -> Unit)? = null
    }
}