package org.bitwa.wallet.presenter

import com.raizlabs.android.dbflow.kotlinextensions.delete
import com.raizlabs.android.dbflow.kotlinextensions.update
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import org.bitwa.wallet.CodeMessageEnum.*
import org.bitwa.wallet.WabeiApp
import org.bitwa.wallet.WabeiAppException
import org.bitwa.wallet.ext.*
import org.bitwa.wallet.repository.model.WabeiWallet
import org.bitwa.wallet.repository.view.wallet.CreateInfo
import org.bitwa.wallet.service.WalletService
import org.web3j.crypto.ECKeyPair
import org.web3j.crypto.Wallet
import org.web3j.protocol.ObjectMapperFactory

/**
 * @author jijunpeng created on 2018/7/4.
 */
object WalletPresenter {

    fun listWallet(lastWalletId: Int? = null, listener: (PresenterListener<List<WabeiWallet>>.() -> Unit)? = null) = launch(UI) {
        val result = exeSimpleSuspend { WalletService.listWallet(lastWalletId) }
        result.invokeByListener(listener)
    }

    fun createWallet(name: String, password: String, prompt: String, listener: (PresenterListener<Unit>.() -> Unit)? = null) = launch(UI) {
        val result = exeSimpleSuspend { WalletService.saveWallet(CreateInfo(walletName = name, password = password, prompt = prompt, type = CreateInfo.Type.NEW)) }
        result.invokeByListener(listener)
    }

    fun importMnemonic(mnemonic: String, password: String, prompt: String, listener: (PresenterListener<Unit>.() -> Unit)? = null) = launch(UI) {
        val result = exeSimpleSuspend { WalletService.saveWallet(CreateInfo(type = CreateInfo.Type.MNEMONIC, content = mnemonic, password = password, prompt = prompt)) }
        result.invokeByListener(listener)
    }

    fun importKeyStore(fileContent: String, password: String, listener: (PresenterListener<Unit>.() -> Unit)? = null) = launch(UI) {
        val result = exeSimpleSuspend { WalletService.saveWallet(CreateInfo(type = CreateInfo.Type.OFFICIAL, content = fileContent, password = password)) }
        result.invokeByListener(listener)
    }

    fun importPrivateKey(privateKey: String, password: String, prompt: String, listener: (PresenterListener<Unit>.() -> Unit)? = null) = launch(UI) {
        val result = exeSimpleSuspend { WalletService.saveWallet(CreateInfo(type = CreateInfo.Type.PRIVATE_KEY, content = privateKey, password = password, prompt = prompt)) }
        result.invokeByListener(listener)
    }

    fun currentWallet(listener: (PresenterListener<WabeiWallet?>.() -> Unit)? = null) = launch(UI) {
        val result = exeSimpleSuspend {
            val defWalletId = defaultWalletId
            val wallet = WalletService.selectWalletByIdOrNull(defWalletId)
            if (defWalletId == -1 && wallet != null) {
                defaultWalletId = wallet.id
            }
            wallet
        }
        result.invokeByListener(listener)
    }

    fun obtainWallet(walletId: Int, listener: (PresenterListener<WabeiWallet>.() -> Unit)? = null) = launch(UI) {
        val result = exeSimpleSuspend {
            WalletService.selectWalletByIdOrNull(walletId)
                    ?: throw WabeiAppException(WALLET_NOT_FOUND_ERROR)
        }
        result.invokeByListener(listener)
    }

    fun validWalletPassword(walletId: Int, password: String, init: PresenterListener<Unit>.() -> Unit) = launch(UI) {
        val result = exeSimpleSuspend(COMMON_PASSWORD_INVALID) {
            val wallet = WalletService.selectWalletById(walletId)
            wallet.obtainPrivateKey(password)
            Unit
        }
        result.invokeByListener(init)
    }

    fun updateWalletPassword(walletId: Int, oldPassword: String, newPassword: String, init: PresenterListener<Unit>.() -> Unit) = launch(UI) {
        val result = exeSimpleSuspend(WALLET_UPDATE_PASSWORD_ERROR) {
            val wallet = WalletService.selectWalletById(walletId)
            val priKey = wallet.obtainPrivateKey(oldPassword)
            val mnemonic = wallet.obtainMnemonic(oldPassword)
            val encryptedPrivateKey = aesEncrypt(priKey, newPassword.toByteArray()).toHexString()
            val encryptedMnemonic = mnemonic?.let { aesEncrypt(it.toByteArray(), newPassword.toByteArray()).toHexString() }
            val newWallet = wallet.copy(encryptedPriKey = encryptedPrivateKey, mnemonic = encryptedMnemonic)
            newWallet.update()
            Unit
        }
        result.invokeByListener(init)
    }

    fun exportPrivateKey(walletId: Int, password: String, init: PresenterListener<String>.() -> Unit) = launch(UI) {
        val result = exeSimpleSuspend(EXPORT_PRIVATE_KEY_ERROR) {
            val wallet = WalletService.selectWalletById(walletId)
            val privateKey = wallet.obtainPrivateKey(password)
            "0x" + privateKey.toHexString()
        }
        result.invokeByListener(init)
    }

    fun exportKeystore(walletId: Int, password: String, init: PresenterListener<String>.() -> Unit) = launch(UI) {
        val result = exeSimpleSuspend(EXPORT_KEY_STORE_ERROR) {
            val wallet = WalletService.selectWalletById(walletId)
            val privateKey = wallet.obtainPrivateKey(password)
            val walletFile = Wallet.createLight(password, ECKeyPair.create(privateKey))
            val json = ObjectMapperFactory.getObjectMapper().writeValueAsString(walletFile)
            json
        }
        result.invokeByListener(init)
    }

    fun exportMnemonic(walletId: Int, password: String, init: PresenterListener<String>.() -> Unit) = launch(UI) {
        val result = exeSimpleSuspend(EXPORT_MNEMONIC_ERROR) {
            val wallet = WalletService.selectWalletById(walletId)
            val mnemonic = wallet.obtainMnemonic(password)
            mnemonic!!
        }
        result.invokeByListener(init)
    }

    fun updateWalletName(walletId: Int, walletName: String, init: PresenterListener<String>.() -> Unit) = launch(UI) {
        val result = exeSimpleSuspend(WALLET_UPDATE_NAME_ERROR) {
            val wallet = WalletService.selectWalletById(walletId)
            wallet.copy(name = walletName).update()
            walletName
        }
        result.invokeByListener(init)
    }

    fun deleteWallet(walletId: Int, password: String, init: PresenterListener<WabeiWallet>.() -> Unit) = launch(UI) {
        val result = exeSimpleSuspend(WALLET_DELETE_ERROR) {
            val wallet = WalletService.selectWalletById(walletId)
            wallet.obtainPrivateKey(password)
            wallet.delete()
            defaultWalletId = -1
            wallet
        }
        result.invokeByListener(init)
    }

    var defaultWalletId: Int
        set(value) {
            WabeiApp.INSTANCE.config.currentWalletId = value
        }
        get() = WabeiApp.INSTANCE.config.currentWalletId
}