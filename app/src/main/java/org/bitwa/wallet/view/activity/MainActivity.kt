package org.bitwa.wallet.view.activity

import android.app.Activity
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.support.annotation.DrawableRes
import android.support.design.widget.TabLayout
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_main.*
import org.bitwa.wallet.R
import org.bitwa.wallet.base.BaseActivity
import org.bitwa.wallet.view.adapter.MainPagerAdapter
import org.jetbrains.anko.find
import org.jetbrains.anko.startActivity

class MainActivity : BaseActivity() {
    override val mRootLayoutResId: Int = R.layout.activity_main
    //隐藏返回按键
    override val mHideBackBtn: Boolean = true

    override fun initWindow(savedInstanceState: Bundle?) {
        super.initWindow(savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                window.statusBarColor = Color.TRANSPARENT
            }
        }
    }

    override fun initWidget(savedInstanceState: Bundle?) {
        super.initWidget(savedInstanceState)
        mMainContainerVp.adapter = MainPagerAdapter(supportFragmentManager)

        mMainIndicatorTl.addTab(mMainIndicatorTl.newTab().apply { customView = generateIndicator("资产", R.drawable.ic_main_asset) })
        mMainIndicatorTl.addTab(mMainIndicatorTl.newTab().apply { customView = generateIndicator("我的", R.drawable.ic_main_user) })
        mMainIndicatorTl.addOnTabSelectedListener(TabLayout.ViewPagerOnTabSelectedListener(mMainContainerVp))
        mMainContainerVp.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(mMainIndicatorTl))
    }

    private fun generateIndicator(name: String, @DrawableRes iconResId: Int): View? {
        val view = layoutInflater.inflate(R.layout.item_indicator_main_pager, mMainIndicatorTl, false)
        val textView = view.find<TextView>(R.id.mTextView)
        textView.text = name
        textView.setCompoundDrawablesRelativeWithIntrinsicBounds(0, iconResId, 0, 0)
        return view
    }

    companion object {
        fun show(activity: Activity) = activity.startActivity<MainActivity>()
    }
}
