package org.bitwa.wallet.presenter

import org.bitwa.wallet.CodeMessageEnum.SUCCESS

/**
 * @author jijunpeng created on 2018/7/9.
 */
data class PresenterResponse<T>(
        val code: String,
        val message: String?,
        val result: T? = null
) {
    fun invokeByListener(listener: PresenterListener<T>) {
        listener.onAll?.invoke()
        if (code == SUCCESS.code) {
            listener.onSuccess?.invoke(result!!)
        } else {
            listener.onFail?.invoke(message!!)
        }
    }

    fun invokeByListener(init: (PresenterListener<T>.() -> Unit)? = null) {
        if (init == null) {
            return
        }
        val listener = PresenterListener<T>().apply(init)
        listener.onAll?.invoke()
        if (code == SUCCESS.code) {
            listener.onSuccess?.invoke(result!!)
        } else {
            listener.onFail?.invoke(message!!)
        }
    }
}