package org.bitwa.wallet.presenter

/**
 * @author jijunpeng created on 2018/7/9.
 */
class PresenterListener<T> {
    /**
     * 无论回调success还是fail 都会调用onAll。该操作已经在PresenterResponse中实现。在具体调用onSuccess，和onFail中无需进行改操作
     */
    var onAll: (() -> Unit)? = null
    var onSuccess: ((T) -> Unit)? = null
    var onFail: ((errMsg: String) -> Unit)? = null
}