package org.bitwa.wallet.ext

import org.bitwa.wallet.CodeMessageEnum
import org.bitwa.wallet.WabeiAppException
import org.bitwa.wallet.repository.model.WabeiWallet
import org.web3j.utils.Convert
import java.math.BigDecimal
import java.math.BigInteger

/**
 * @author jijunpeng created on 2018/7/9.
 */
data class WabeiCoin(
        val number: BigDecimal,
        val unit: WabeiUnit
) {
    override fun toString(): String {
        return number.toPlainString()
    }

    fun toWeiBigInteger(): BigInteger {
        val unit = when (unit) {
            WabeiUnit.WEI -> Convert.Unit.WEI
            WabeiUnit.GWEI -> Convert.Unit.GWEI
            WabeiUnit.WAB -> Convert.Unit.ETHER
        }
        return Convert.toWei(number, unit).toBigInteger()
    }
}

enum class WabeiUnit(val unit: String) {
    WEI("WEI"), GWEI("GWEI"), WAB("WAB")
}

fun BigInteger.parseToWab(): WabeiCoin {
    val decimal = Convert.fromWei(this.toBigDecimal(), Convert.Unit.ETHER)
    return WabeiCoin(decimal, WabeiUnit.WAB)
}

fun BigInteger.parseToGWei(): WabeiCoin {
    val wei = Convert.fromWei(this.toBigDecimal(), Convert.Unit.GWEI)
    return WabeiCoin(wei, WabeiUnit.WEI)
}

fun BigDecimal.parseToGWei(): WabeiCoin {
    return WabeiCoin(this, WabeiUnit.GWEI)
}

fun BigDecimal.parseToWab(): WabeiCoin {
    return WabeiCoin(this, WabeiUnit.WAB)
}

@Throws(WabeiAppException::class)
fun WabeiWallet.obtainPrivateKey(password: String): ByteArray {
    val encrypt = this.encryptedPriKey.parseToByteArray()
    try {
        return aesDecrypt(encrypt, password.toByteArray())
    } catch (e: Exception) {
        throw WabeiAppException(CodeMessageEnum.COMMON_PASSWORD_INVALID)
    }
}

fun WabeiWallet.obtainMnemonic(password: String): String? {
    val encrypt = this.mnemonic?.parseToByteArray()
    if (encrypt != null) {
        val aesDecrypt = aesDecrypt(encrypt, password.toByteArray())
        return String(aesDecrypt)
    }
    return null
}
