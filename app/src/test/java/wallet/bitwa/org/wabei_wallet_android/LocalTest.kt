package wallet.bitwa.org.wabei_wallet_android

import android.util.Log
import com.fasterxml.jackson.databind.ObjectMapper
import org.bitwa.wallet.CodeMessageEnum
import org.bitwa.wallet.WabeiAppException
import org.bitwa.wallet.ext.TAG
import org.junit.Test
import org.web3j.crypto.Credentials
import org.web3j.crypto.Wallet
import org.web3j.crypto.WalletFile

/**
 * @author jijunpeng created on 2018/7/21.
 */
class LocalTest {
    @Test
    fun test() {
        println("hello")
        val content = "{\"address\":\"f290901b8c3af97689a54306891ef616e492f597\",\"crypto\":{\"cipher\":\"aes-128-ctr\",\"ciphertext\":\"fe3cab861a70844ce06d8b4af9ee7f5195da909669169df9b262fa6db36e7c3d\",\"cipherparams\":{\"iv\":\"fa66b255c731f799437df9234d3427b3\"},\"kdf\":\"scrypt\",\"kdfparams\":{\"dklen\":32,\"n\":262144,\"p\":1,\"r\":8,\"salt\":\"333ee956a4795342260a291347a07c63a34fee8ad205828baf98d09d7665cbbd\"},\"mac\":\"da609eaaa1316f9bcaed60372821289f8220f0060fa40dccb0fc5a39931ca1fb\"},\"id\":\"ad0d5bbe-16ca-4e4a-b78e-8dca6a82c450\",\"version\":3}"
        val password = "123456"
        val credentials = createCredentialsByOfficialContent(content, password)
        println(credentials.address)
        println(credentials.ecKeyPair.privateKey.toString(16))
        println(credentials.ecKeyPair.publicKey.toString(16))
    }

    private fun createCredentialsByOfficialContent(fileContent: String, password: String): Credentials {
        try {
            val walletFile = ObjectMapper().readValue(fileContent, WalletFile::class.java)
            try {
                return Credentials.create(Wallet.decrypt(password, walletFile))
            } catch (e: Exception) {
                Log.e(TAG, "Credentials.create(Wallet.decrypt(password, walletFile))报错", e)
                throw WabeiAppException(CodeMessageEnum.IMPORT_KEY_STORE_PASSWORD_ERROR)
            }
        } catch (e: Exception) {
            if (e is WabeiAppException) {
                throw e
            }
            Log.e(TAG, "ObjectMapper.readValue报错", e)
            throw WabeiAppException(CodeMessageEnum.IMPORT_KEY_STORE_FORMAT_ERROR)
        }
    }
}