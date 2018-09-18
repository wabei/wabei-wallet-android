package org.bitwa.wallet.presenter

import android.util.Log
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import org.bitwa.wallet.CodeMessageEnum.*
import org.bitwa.wallet.WabeiApp
import org.bitwa.wallet.WabeiAppException
import org.bitwa.wallet.ext.TAG
import org.bitwa.wallet.ext.exeSimpleSuspend
import org.bitwa.wallet.ext.obtainPrivateKey
import org.bitwa.wallet.ext.toHexString
import org.bitwa.wallet.repository.model.TransRecord
import org.bitwa.wallet.service.TransRecordService
import org.bitwa.wallet.service.WalletService
import org.web3j.crypto.Credentials
import org.web3j.crypto.RawTransaction
import org.web3j.crypto.TransactionEncoder
import org.web3j.protocol.core.DefaultBlockParameterName
import org.web3j.protocol.core.methods.request.Transaction
import java.math.BigInteger
import org.web3j.protocol.core.methods.response.Transaction as ResponseTransaction

/**
 * @author jijunpeng created on 2018/7/9.
 */
object TransactionPresenter {
    fun requestTransRecord(walletId: Int, lastRecordId: Long? = null, listener: PresenterListener<List<TransRecord>>.() -> Unit) = launch(UI) {
        val result = exeSimpleSuspend { TransRecordService.listTransRecord(walletId, lastRecordId) }
        result.invokeByListener(listener)
    }

    fun requestTransRecordByTransHash(transHash: String, init: PresenterListener<ResponseTransaction>.() -> Unit) = launch(UI) {
        val result = exeSimpleSuspend(TRANS_RECORD_SELECT_ERROR) { WabeiApp.web3j.ethGetTransactionByHash(transHash).send().transaction }
        result.invokeByListener(init)
    }

    fun requestBalance(address: String, init: PresenterListener<BigInteger>.() -> Unit) = launch(UI) {
        val balance = exeSimpleSuspend { WabeiApp.web3j.ethGetBalance(address, DefaultBlockParameterName.LATEST).send().balance }
        balance.invokeByListener(init)
    }

    fun requestGasPrice(init: PresenterListener<BigInteger>.() -> Unit) = launch(UI) {
        val gasPrice = exeSimpleSuspend { WabeiApp.web3j.ethGasPrice().send().gasPrice }
        gasPrice.invokeByListener(init)
    }

    fun requestGasLimit(fromAddress: String, toAddress: String, value: BigInteger, init: PresenterListener<BigInteger>.() -> Unit) = launch(UI) {
        val gasLimit = exeSimpleSuspend {
            val transaction = Transaction.createEtherTransaction(fromAddress, null, null, null, toAddress, value)
            WabeiApp.web3j.ethEstimateGas(transaction).send().amountUsed
        }
        gasLimit.invokeByListener(init)
    }

    fun sendTransaction(walletId: Int, toAddress: String, amount: BigInteger, gasPrice: BigInteger, gasLimit: BigInteger, password: String, init: PresenterListener<String>.() -> Unit) = launch(UI) {
        val transaction = exeSimpleSuspend {
            val wallet = WalletService.selectWalletById(walletId)
            val nonce = WabeiApp.web3j.ethGetTransactionCount(wallet.address, DefaultBlockParameterName.PENDING).send().transactionCount
            val rawTransaction = RawTransaction.createEtherTransaction(nonce, gasPrice, gasLimit, toAddress, amount)
            val privateKey: ByteArray =
                    try {
                        wallet.obtainPrivateKey(password)
                    } catch (e: Exception) {
                        Log.e(TAG, "账户密码错误", e)
                        throw WabeiAppException(COMMON_PASSWORD_INVALID)
                    }
            val credentials = Credentials.create(privateKey.toHexString())
            val signMessage = "0x" + TransactionEncoder.signMessage(rawTransaction, credentials).toHexString()
            val ethSendTransaction = WabeiApp.web3j.ethSendRawTransaction(signMessage).send()
            if (ethSendTransaction.hasError()) {
                Log.e(TAG, "发送交易失败, ${ethSendTransaction.error.code} - ${ethSendTransaction.error.message} - ${ethSendTransaction.error.data}")
                throw WabeiAppException(ETH_TRANS_SEND_ERROR)
            }
            val transHash = ethSendTransaction.transactionHash
            TransRecordService.insertTransRecord(walletId, -amount, toAddress, transHash)
            transHash
        }
        transaction.invokeByListener(init)
    }
}