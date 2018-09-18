package org.bitwa.wallet.view.adapter

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import org.bitwa.wallet.view.fragment.WalletImportKeyFragment
import org.bitwa.wallet.view.fragment.WalletImportMnemonicFragment
import org.bitwa.wallet.view.fragment.WalletImportOfficialFragment

/**
 * @author jijunpeng created on 2018/7/3.
 */
class WalletImportPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {
    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> WalletImportMnemonicFragment()
            1 -> WalletImportOfficialFragment()
            else -> WalletImportKeyFragment()
        }
    }

    override fun getCount(): Int {
        return 3
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return when (position) {
            0 -> "助记词"
            1 -> "官方钱包"
            else -> "私钥"
        }
    }
}