package org.bitwa.wallet.view.adapter

import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration
import org.bitwa.wallet.R
import org.bitwa.wallet.ext.parseToWab
import org.bitwa.wallet.ext.toDateTimeStr
import org.bitwa.wallet.repository.model.TransRecord
import org.jetbrains.anko.dip

/**
 * @author jijunpeng created on 2018/7/9.
 */

class TransRecordAdapter(init: Listener.() -> Unit) : BaseQuickAdapter<TransRecord, BaseViewHolder>(R.layout.item_trans_record) {
    private val mListener by lazy { Listener().apply(init) }

    override fun convert(helper: BaseViewHolder, item: TransRecord) {
        val desc = when (item.state) {
            1 -> "已转入"
            2 -> "已转出"
            else -> ""
        }
        helper.setText(R.id.mAddressTv, item.address)
                .setText(R.id.mDateTv, item.date.toDateTimeStr())
                .setText(R.id.mAmountTv, "${item.amount.parseToWab()} WAB")
                .setText(R.id.mDescTv, desc)
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        val context = recyclerView.context
        recyclerView.addItemDecoration(HorizontalDividerItemDecoration.Builder(context)
                .size(context.dip(0.5f))
                .color(context.resources.getColor(R.color.primary_divider_bg))
                .margin(context.dip(16))
                .build())
        recyclerView.layoutManager = LinearLayoutManager(context)
        setEmptyView(R.layout.empty_view, recyclerView)

        setOnLoadMoreListener({ mListener.onLoadMore?.invoke(data.last().id) }, recyclerView)
        setOnItemClickListener { _, _, position ->
            mListener.onItemClick?.invoke(getItem(position) ?: return@setOnItemClickListener)
        }
    }

    class Listener {
        var onLoadMore: ((lastId: Long) -> Unit)? = null
        var onItemClick: ((record: TransRecord) -> Unit)? = null
    }
}