package org.bitwa.wallet.view.adapter

import android.graphics.Color
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration
import org.bitwa.wallet.R
import org.bitwa.wallet.ext.parseToWab
import org.bitwa.wallet.repository.model.WabeiWallet
import org.jetbrains.anko.dip
import java.math.BigInteger

/**
 * @author jijunpeng created on 2018/7/20.
 */
class WalletMainAdapter(init: (Listener.() -> Unit)? = null) : BaseQuickAdapter<WabeiWallet, BaseViewHolder>(R.layout.item_wallet_main) {
    private val mListener by lazy { Listener().apply { init?.invoke(this) } }
    val mBalanceMap = hashMapOf<Int, BigInteger>()

    override fun convert(helper: BaseViewHolder, item: WabeiWallet) {
        val balance = mBalanceMap[item.id]
        val balanceStr = if (balance == null) {
            mListener.onRequestBalance?.invoke(this, item)
            "0 WAB"
        } else {
            balance.parseToWab().number.toPlainString() + " WAB"
        }
        helper.setText(R.id.mWalletNameTv, item.name)
                .setText(R.id.mBalanceTv, balanceStr)
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        val context = recyclerView.context
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.addItemDecoration(HorizontalDividerItemDecoration.Builder(context)
                .color(Color.parseColor("#DDDDDD"))
                .size(context.dip(1))
                .showLastDivider()
                .build())
        setOnItemClickListener { adapter, view, position ->
            val item = getItem(position) ?: return@setOnItemClickListener
            mListener.onItemClick?.invoke(adapter, view, position, item)
        }
        val adapter = this
        setOnLoadMoreListener({
            if (itemCount == 0) {
                mListener.onLoadMore?.invoke(adapter, adapter.itemCount, null)
            } else {
                mListener.onLoadMore?.invoke(adapter, adapter.itemCount, adapter.data.last())
            }
        }, recyclerView)
    }

    fun addWalletBalance(walletId: Int, balance: BigInteger): WalletMainAdapter {
        mBalanceMap[walletId] = balance
        val index = data.indexOfFirst { walletId == it.id }
        notifyItemChanged(index)
        return this
    }

    class Listener : AdapterListener<WabeiWallet>() {
        var onRequestBalance: ((adapter: WalletMainAdapter, wallet: WabeiWallet) -> Unit)? = null
    }
}