package org.bitwa.wallet

/**
 * @author jijunpeng created on 2018/7/18.
 */
class WabeiAppException(
        val codeMessage: CodeMessageEnum,
        customMessage: String? = null,
        throwable: Throwable? = null
) : RuntimeException(customMessage ?: codeMessage.message, throwable)

//@formatter:off
enum class CodeMessageEnum(val code: String, val message: String) {
    SUCCESS                                                 ("00000", "操作成功"),
    /********
     *  通用错误码：10XXX
     *********/
    COMMON_EXE_SIMPLE_SUSPEND                               ("10001", "系统错误，请联系厂商"),
    COMMON_PASSWORD_INVALID                                 ("10002", "密码错误"),

    /********
     *  导入钱包错误码：30XXX
     *********/
    IMPORT_WALLET_ERROR                                     ("30001", "导入钱包失败"),
    IMPORT_MNEMONIC_FORMAT_ERROR                            ("30002", "助记词格式不正确"),
    IMPORT_KEY_STORE_FORMAT_ERROR                           ("30003", "KeyStore格式不正确"),
    IMPORT_KEY_STORE_PASSWORD_ERROR                         ("30004", "KeyStore密码错误"),
    IMPORT_PRIVATE_KEY_FORMAT_ERROR                         ("30005", "私钥格式不正确"),
    IMPORT_REPEAT_ADDRESS_ERROR                             ("30006", "导入钱包失败，已经存在相同地址的钱包"),
    IMPORT_KEY_STORE_ERROR                                  ("30007", "导入Keystore文件发生未知错误"),

    /********
     *  导出钱包错误码：31XXX
     *********/
    EXPORT_PRIVATE_KEY_ERROR                                ("31001", "导出私钥失败"),
    EXPORT_KEY_STORE_ERROR                                  ("31002", "导出Keystore失败"),
    EXPORT_MNEMONIC_ERROR                                   ("31003", "导出助记词失败"),

    /********
     *  修改钱包信息错误码：32XXX
     *********/
    WALLET_UPDATE_PASSWORD_ERROR                            ("32001", "修改钱包密码失败"),
    WALLET_UPDATE_NAME_ERROR                                ("32002", "修改钱包名称失败"),
    WALLET_DELETE_ERROR                                     ("32003", "删除钱包失败"),
    WALLET_NOT_FOUND_ERROR                                  ("32004", "未找到钱包"),

    /********
     *  上链交易错误码：33XXX
     *********/
    ETH_TRANS_SEND_ERROR                                     ("33001", "发送交易失败"),

    /********
     *  本地交易记录错误码：34XXX
     *********/
    TRANS_RECORD_SELECT_ERROR                               ("34001", "请求交易详情失败"),
}