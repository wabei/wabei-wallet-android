package org.bitwa.wallet.ext

import org.web3j.crypto.Hash
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

/**
 * @author jijunpeng created on 2018/7/5.
 */
fun generateSalt(): ByteArray {
    return Hash.sha256(SecureRandom.getSeed(32))
}

fun sha256(byteArray: ByteArray): ByteArray {
    return Hash.sha256(byteArray)
}

/**
 * @return hexString
 */
fun aesEncrypt(input: ByteArray, password: ByteArray): ByteArray {
    //创建cipher对象
    val cipher = Cipher.getInstance("AES")
    //初始化cipher
    //通过秘钥工厂生产秘钥
    val passwordSha256 = sha256(password)
    val keySpec = SecretKeySpec(passwordSha256, "AES")
    cipher.init(Cipher.ENCRYPT_MODE, keySpec)
    //加密、解密
    return cipher.doFinal(input)
}

/**
 * @param input hexString
 */
fun aesDecrypt(input: ByteArray, password: ByteArray): ByteArray {
    //创建cipher对象
    val cipher = Cipher.getInstance("AES")
    //初始化cipher
    //通过秘钥工厂生产秘钥
    val passwordSha256 = sha256(password)
    val keySpec = SecretKeySpec(passwordSha256, "AES")
    cipher.init(Cipher.DECRYPT_MODE, keySpec)
    //加密、解密
    return cipher.doFinal(input)
}