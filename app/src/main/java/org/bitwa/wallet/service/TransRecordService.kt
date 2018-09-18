package org.bitwa.wallet.service

import com.raizlabs.android.dbflow.kotlinextensions.*
import com.raizlabs.android.dbflow.sql.language.OrderBy
import org.bitwa.wallet.repository.model.TransRecord
import org.bitwa.wallet.repository.model.TransRecord_Table
import java.math.BigInteger

/**
 * @author jijunpeng created on 2018/7/9.
 */
object TransRecordService {
    fun insertTransRecord(walletId: Int, amount: BigInteger, address: String, transHash: String) {
        val wallet = WalletService.selectWalletById(walletId)
        val state = if (amount >= BigInteger.ZERO) 1 else 1
        val record = TransRecord(
                wallet = wallet,
                address = address,
                amount = amount,
                state = state,
                transHash = transHash
        )
        record.insert()
    }

    fun listTransRecord(walletId: Int, lastRecordId: Long? = null, pageSize: Int = 10): List<TransRecord> {
        val lastRecord: TransRecord?
        if (lastRecordId != null) {
            val list = (select from TransRecord::class where TransRecord_Table.id.eq(lastRecordId) limit 1).list
            lastRecord = if (list.isEmpty()) null else list[0]
        } else {
            lastRecord = null
        }

        return if (lastRecord == null) {
            (select
                    from TransRecord::class
                    where TransRecord_Table.wallet_id.eq(walletId)
                    orderBy OrderBy.fromProperty(TransRecord_Table.date)
                    limit pageSize)
        } else {
            (select
                    from TransRecord::class
                    where TransRecord_Table.wallet_id.eq(walletId) and TransRecord_Table.date.lessThan(lastRecord.date)
                    orderBy OrderBy.fromProperty(TransRecord_Table.date)
                    limit pageSize)
        }.list
    }
}
