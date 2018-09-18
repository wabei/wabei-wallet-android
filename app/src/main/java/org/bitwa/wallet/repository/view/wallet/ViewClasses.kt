package org.bitwa.wallet.repository.view.wallet

/**
 * @author jijunpeng created on 2018/7/5.
 */
data class CreateInfo(
        val walletName: String? = null,
        val content: String? = null,
        val password: String,
        val prompt: String? = null,
        val type: Type
) {
    enum class Type {
        NEW, MNEMONIC, OFFICIAL, PRIVATE_KEY
    }
}