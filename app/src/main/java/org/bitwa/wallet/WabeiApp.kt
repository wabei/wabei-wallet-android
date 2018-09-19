package org.bitwa.wallet

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.support.multidex.MultiDex
import android.util.Log
import androidx.core.content.edit
import com.raizlabs.android.dbflow.annotation.Database
import com.raizlabs.android.dbflow.config.DatabaseConfig
import com.raizlabs.android.dbflow.config.FlowConfig
import com.raizlabs.android.dbflow.config.FlowManager
import com.uuzuche.lib_zxing.activity.ZXingLibrary
import org.jetbrains.anko.doAsync
import org.web3j.crypto.MnemonicUtils
import org.web3j.protocol.Web3j
import org.web3j.protocol.Web3jFactory
import org.web3j.protocol.http.HttpService
import java.io.BufferedReader
import java.io.InputStreamReader
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * @author jijunpeng created on 2018/7/2.
 */
class WabeiApp : Application() {

    val config: Config by lazy { Config() }

    override fun onCreate() {
        super.onCreate()
        INSTANCE = this
        initDbFlow()
        initMnemonicUtils()
        initWeb3j()
        initZXing()
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        // 支持多dex
        MultiDex.install(this)

    }

    private fun initDbFlow() {
        FlowManager.init(FlowConfig.builder(this)
                .addDatabaseConfig(
                        DatabaseConfig.builder(AppDatabase::class.java)
                                .databaseName(AppDatabase.NAME)
                                .build())
                .build())
    }

    private fun initWeb3j() {
        doAsync {
            Log.d("WalletApp", web3j.web3ClientVersion().send().web3ClientVersion)
        }
    }

    private fun initMnemonicUtils() {
        val clazz = MnemonicUtils::class.java
        val field = clazz.getDeclaredField("WORD_LIST")
        field.isAccessible = true
        val inputStream = assets.open("wordList/en-mnemonic-word-list.txt")
        val reader = BufferedReader(InputStreamReader(inputStream))
        val data = reader.readLines()
        field.set(null, data)
    }

    private fun initZXing() {
        ZXingLibrary.initDisplayOpinion(this)
    }

    val sharedPreferences: SharedPreferences
        get() = this.getSharedPreferences("Config", android.content.Context.MODE_PRIVATE)

    companion object {
        private const val url = "http://132.232.53.82:8899" //瓦贝公链
        //        private const val url = "https://rinkeby.infura.io/wpvAXa1yEbMp8efQT1CR" // 测试链infura

        val web3j: Web3j by lazy { Web3jFactory.build(HttpService(url)) }
        var INSTANCE: WabeiApp by initOnce()
    }

    inner class Config {
        private val KEY_CURRENT_WALLET_ID = "KEY_CURRENT_WALLET_ID"
        var currentWalletId: Int
            get() = sharedPreferences.getInt(KEY_CURRENT_WALLET_ID, -1)
            set(value) = sharedPreferences.edit { putInt(KEY_CURRENT_WALLET_ID, value) }
    }
}

private fun initOnce(): ReadWriteProperty<WabeiApp.Companion, WabeiApp> = NotNullOnceInitVar()

private class NotNullOnceInitVar<T : Any> : ReadWriteProperty<Any?, T> {
    private var value: T? = null

    override fun getValue(thisRef: Any?, property: KProperty<*>): T {
        return value
                ?: throw IllegalStateException("Property ${property.name} should be initialized before get.")
    }

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        if (this.value != null) {
            throw IllegalStateException("Property ${property.name} already init.")
        }
        this.value = value
    }
}

@Database(version = AppDatabase.VERSION)
object AppDatabase {
    const val NAME = "AppDataBase"
    const val VERSION = 1
}