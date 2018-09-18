package org.bitwa.wallet.view.adapter

import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import org.bitwa.wallet.R

/**
 * @author jijunpeng created on 2018/7/8.
 */
class MainCoinTypeAdapter : BaseQuickAdapter<Int, BaseViewHolder>(R.layout.item_main_wallet_coin_type) {
    override fun convert(helper: BaseViewHolder, item: Int) {

    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        val context = recyclerView.context
        recyclerView.layoutManager = GridLayoutManager(context, 2)
    }
}