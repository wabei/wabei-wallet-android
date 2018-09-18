package org.bitwa.wallet.service

import android.util.Log
import com.fasterxml.jackson.core.io.JsonEOFException
import com.fasterxml.jackson.databind.ObjectMapper
import com.quincysx.crypto.CoinTypes
import com.quincysx.crypto.bip32.ExtendedKey
import com.quincysx.crypto.bip44.AddressIndex
import com.quincysx.crypto.bip44.BIP44
import com.quincysx.crypto.bip44.CoinPairDerive
import com.raizlabs.android.dbflow.kotlinextensions.*
import com.raizlabs.android.dbflow.sql.language.OrderBy
import com.raizlabs.android.dbflow.sql.language.SQLite
import org.bitwa.wallet.CodeMessageEnum
import org.bitwa.wallet.WabeiAppException
import org.bitwa.wallet.ext.TAG
import org.bitwa.wallet.ext.aesEncrypt
import org.bitwa.wallet.ext.toHexString
import org.bitwa.wallet.repository.model.WabeiWallet
import org.bitwa.wallet.repository.model.WabeiWallet_Table
import org.bitwa.wallet.repository.view.wallet.CreateInfo
import org.web3j.crypto.*
import java.security.SecureRandom


/**
 * @author jijunpeng created on 2018/7/5.
 */
object WalletService {

    /**
     * 保存钱包
     */
    fun saveWallet(info: CreateInfo) {
        try {
            val addressIndex = BIP44.m().purpose44()
                    .coinType(CoinTypes.Ethereum)
                    .account(0)
                    .external()
                    .address(0)
            val mnemonic = when (info.type) {
                CreateInfo.Type.NEW -> createMnemonicByName()
                CreateInfo.Type.MNEMONIC -> info.content!!
                else -> null
            }
            val credentials = when (info.type) {
                CreateInfo.Type.NEW -> createCredentialsByMnemonic(mnemonic!!, addressIndex)
                CreateInfo.Type.MNEMONIC -> createCredentialsByMnemonic(mnemonic!!, addressIndex)
                CreateInfo.Type.OFFICIAL -> createCredentialsByOfficialContent(info.content!!, info.password)
                CreateInfo.Type.PRIVATE_KEY -> createCredentialByPrivateKey(info.content!!)
            }

            //验证钱包是否已经存在
            val shouldBeNull = selectWalletByAddress(credentials.address)
            if (shouldBeNull != null) {
                throw WabeiAppException(CodeMessageEnum.IMPORT_REPEAT_ADDRESS_ERROR)
            }

            val priKey = credentials.ecKeyPair.privateKey.toByteArray()
            val password = info.password.toByteArray()
            val encryptedPrivateKey = aesEncrypt(priKey, password)
            val encryptedMnemonic = mnemonic?.let { aesEncrypt(it.toByteArray(), password).toHexString() }
            val wallet = WabeiWallet(
                    name = info.walletName ?: "Wallet",
                    pubKey = credentials.ecKeyPair.publicKey.toString(16),
                    encryptedPriKey = encryptedPrivateKey.toHexString(),
                    address = credentials.address,
                    mnemonic = encryptedMnemonic,
                    prompt = info.prompt
            )
            wallet.insert()
        } catch (e: Exception) {
            if (e is WabeiAppException) {
                throw e
            } else {
                Log.e(TAG, "创建钱包失败", e)
                throw WabeiAppException(CodeMessageEnum.IMPORT_WALLET_ERROR)
            }
        }
    }

    /**
     * 获取指定钱包
     */
    fun selectWalletById(walletId: Int = -1): WabeiWallet {
        return if (walletId != -1) {
            (select from WabeiWallet::class where WabeiWallet_Table.id.eq(walletId)).list[0]
        } else {
            (select from WabeiWallet::class limit 1).list[0]
        }
    }

    /**
     * 根据地址查询钱包
     */
    fun selectWalletByAddress(address: String): WabeiWallet? {
        val list = (select from WabeiWallet::class where WabeiWallet_Table.address.eq(address)).list
        return list.takeIf { it.isNotEmpty() }?.get(0)
    }

    /**
     *  获取指定钱包，结果可能为空
     */
    fun selectWalletByIdOrNull(walletId: Int = -1): WabeiWallet? {
        return if (walletId != -1) {
            (select from WabeiWallet::class where WabeiWallet_Table.id.eq(walletId)).list[0]
        } else {
            (select from WabeiWallet::class limit 1).list.takeIf { it.isNotEmpty() }?.get(0)
        }
    }

    fun hasWallet(): Boolean {
        return SQLite.selectCountOf(WabeiWallet_Table.id).from(WabeiWallet::class).longValue() > 0L
    }

    private fun createMnemonicByName(): String {
        val initialEntropy = ByteArray(16)
        SecureRandom().nextBytes(initialEntropy)
        return MnemonicUtils.generateMnemonic(initialEntropy)
    }

    private fun createCredentialsByMnemonic(mnemonic: String, addressIndex: AddressIndex): Credentials {
        val seed = MnemonicUtils.generateSeed(mnemonic, null)
        val rootKey = ExtendedKey.create(seed)
        val coinPairDerive = CoinPairDerive(rootKey)
        val ecKeyPair = coinPairDerive.derive(addressIndex)
        val privateKey = ecKeyPair.rawPrivateKey
        return Credentials.create(ECKeyPair.create(privateKey))
    }

    private fun createCredentialsByOfficialContent(fileContent: String, password: String): Credentials {
        try {
            val walletFile = ObjectMapper().readValue(fileContent, WalletFile::class.java)
            try {
                //当keystore文件中的n值过大时，Wallet.decrypt会报OOM的错误
                val decrypt = MyWallet.decrypt(password, walletFile)
                return Credentials.create(decrypt)
            } catch (e: Throwable) {
                Log.e(TAG, "解密Keystore报错, fileContent=${fileContent}", e)
                if (e is CipherException) {
                    throw WabeiAppException(CodeMessageEnum.IMPORT_KEY_STORE_PASSWORD_ERROR)
                } else {
                    throw Exception(e)
                }
            }
        } catch (e: Exception) {
            if (e is WabeiAppException) {
                throw e
            }
            if (e is JsonEOFException) {
                Log.e(TAG, "ObjectMapper.readValue报错", e)
                throw WabeiAppException(CodeMessageEnum.IMPORT_KEY_STORE_FORMAT_ERROR)
            }
            Log.e(TAG, "导入官方keystore报错", e)
            throw WabeiAppException(CodeMessageEnum.IMPORT_KEY_STORE_ERROR)
        }
    }

    private fun createCredentialByPrivateKey(privateKey: String): Credentials {
        return Credentials.create(privateKey)
    }

    fun listWallet(lastWalletId: Int?, pageSize: Int = 10): List<WabeiWallet> {
        return if (lastWalletId == null) {
            (select from WabeiWallet::class orderBy OrderBy.fromProperty(WabeiWallet_Table.id) limit pageSize).list
        } else {
            (select from WabeiWallet::class where WabeiWallet_Table.id.lessThan(lastWalletId) orderBy OrderBy.fromProperty(WabeiWallet_Table.id) limit pageSize).list
        }
    }
}
