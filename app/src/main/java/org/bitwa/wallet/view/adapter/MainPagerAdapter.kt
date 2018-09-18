package org.bitwa.wallet.view.adapter

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import org.bitwa.wallet.view.fragment.UserCenterFragment
import org.bitwa.wallet.view.fragment.WalletMainFragment

/**
 * @author jijunpeng created on 2018/7/8.
 */
class MainPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {
    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> WalletMainFragment()
            else -> UserCenterFragment()
        }
    }

    override fun getCount(): Int {
        return 2
    }
}