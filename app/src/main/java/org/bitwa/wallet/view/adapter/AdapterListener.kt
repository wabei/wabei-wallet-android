package org.bitwa.wallet.view.adapter

import android.view.View
import com.chad.library.adapter.base.BaseQuickAdapter

/**
 * @author jijunpeng created on 2018/7/20.
 */
open class AdapterListener<T> {
    var onLoadMore: ((adapter: BaseQuickAdapter<*, *>, size: Int, lastItem: T?) -> Unit)? = null
    var onItemClick: ((adapter: BaseQuickAdapter<*, *>, itemView: View, position: Int, item: T) -> Unit)? = null
    var onItemChildClick: ((adapter: BaseQuickAdapter<*, *>, childView: View, position: Int, item: T) -> Unit)? = null
}