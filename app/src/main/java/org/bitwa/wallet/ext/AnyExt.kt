package org.bitwa.wallet.ext

import android.util.Log
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.async
import org.bitwa.wallet.CodeMessageEnum
import org.bitwa.wallet.CodeMessageEnum.COMMON_EXE_SIMPLE_SUSPEND
import org.bitwa.wallet.CodeMessageEnum.SUCCESS
import org.bitwa.wallet.WabeiAppException
import org.bitwa.wallet.presenter.PresenterResponse

/**
 * @author jijunpeng created on 2018/7/3.
 */
val Any.TAG
    get() = this::class.java.simpleName

suspend fun <T> exeSimpleSuspend(defErrCodeMsg: CodeMessageEnum = COMMON_EXE_SIMPLE_SUSPEND, block: () -> T): PresenterResponse<T> {
    return async(CommonPool) {
        try {
            val result = block()
            PresenterResponse(SUCCESS.code, SUCCESS.message, result)
        } catch (e: Exception) {
            Log.e(TAG, "exeSimpleSuspend throw error.", e)
            if (e !is WabeiAppException) {
                PresenterResponse(defErrCodeMsg.code, defErrCodeMsg.message)
            } else {
                PresenterResponse<T>(e.codeMessage.code, e.message)
            }
        }
    }.await()
}

suspend fun <T> exeSimpSuspend(block: () -> T): T {
    return async(CommonPool) { block() }.await()
}