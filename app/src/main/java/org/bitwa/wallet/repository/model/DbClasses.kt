package org.bitwa.wallet.repository.model

import com.raizlabs.android.dbflow.annotation.Column
import com.raizlabs.android.dbflow.annotation.ForeignKey
import com.raizlabs.android.dbflow.annotation.PrimaryKey
import com.raizlabs.android.dbflow.annotation.Table
import org.bitwa.wallet.AppDatabase
import java.io.Serializable
import java.math.BigInteger
import java.util.*

/**
 *@author jijunpeng created on 2018/7/5.
 */

@Table(database = AppDatabase::class, allFields = true)
data class WabeiWallet(
        @PrimaryKey(autoincrement = true)
        var id: Int = 0,
        @Column(defaultValue = "Wallet")
        var name: String? = null,
        var pubKey: String = "",
        var encryptedPriKey: String = "",
        var address: String = "",
        var mnemonic: String? = null,
        var prompt: String? = null
)

@Table(database = AppDatabase::class, allFields = true)
data class TransRecord(
        @PrimaryKey(autoincrement = true)
        var id: Long = 0,
        @ForeignKey(stubbedRelationship = true)
        var wallet: WabeiWallet? = null,
        var address: String = "",
        var date: Date = Date(),
        var amount: BigInteger = BigInteger.ZERO,
        /**
         * 1 转入，2转出，0 无效状态
         */
        var state: Int = 0,
        var transHash: String = ""
) : Serializable