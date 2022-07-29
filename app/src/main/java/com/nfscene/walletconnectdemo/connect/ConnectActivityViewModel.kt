package com.nfscene.walletconnectdemo.connect


import android.app.Application
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import java.io.File
import java.util.Random
import java.util.UUID
import okhttp3.OkHttpClient
import org.komputing.khex.extensions.toNoPrefixHexString
import org.walletconnect.Session
import org.walletconnect.impls.FileWCSessionStore
import org.walletconnect.impls.MoshiPayloadAdapter
import org.walletconnect.impls.OkHttpTransport
import org.walletconnect.impls.WCSession
import org.walletconnect.impls.WCSessionStore
import org.walletconnect.nullOnThrow

class ConnectActivityViewModel(application: Application): AndroidViewModel(application), Session.Callback {
    private val isDAppConnected = MutableLiveData<Boolean>()
    private val isWalletConnected = MutableLiveData<Boolean>()
    private val accounts = MutableLiveData<List<String>>()

    fun isDAppConnected(): LiveData<Boolean> {
        return isDAppConnected
    }

    fun isWalletConnected(): LiveData<Boolean> {
        return isWalletConnected
    }

    fun getAccounts(): MutableLiveData<List<String>> {
        return accounts
    }

    init {
        isWalletConnected.value = false
        isDAppConnected.value = false
        accounts.value = mutableListOf<String>()
        initMoshi()
        initClient()
        initSessionStorage()
        resetSession(this)
    }

    fun fetchAccounts() {
        accounts.postValue(session.approvedAccounts())
    }

    fun signIn() {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(config.toWCUri()))
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        getContext().startActivity(intent)
    }

    fun signOut() {
        session.kill()
    }

    private fun initClient() {
        client = OkHttpClient.Builder().build()
    }

    private fun initMoshi() {
        moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
    }

    private fun initSessionStorage() {
        storage = FileWCSessionStore(
            File(
                getContext().cacheDir,
                "session_store.json"
            ).apply { createNewFile() }, moshi
        )
    }

    fun resetSession(callback: Session.Callback) {
        nullOnThrow { session }?.clearCallbacks()
        val key = ByteArray(32).also { Random().nextBytes(it) }.toNoPrefixHexString()
        config =
            Session.Config(UUID.randomUUID().toString(), bridgeUrl, key)
        session = WCSession(
            config,
            MoshiPayloadAdapter(moshi),
            storage,
            OkHttpTransport.Builder(client, moshi),
            metaData
        )
        session.addCallback(callback)
        session.offer()
    }

    private fun getContext(): Context {
        return this.getApplication<Application>().applicationContext
    }

    override fun onMethodCall(call: Session.MethodCall) {
        Log.d("Wallet Connect Debug", call.toString())
    }

    override fun onStatus(status: Session.Status) {
        when(status) {
            Session.Status.Connected -> isDAppConnected.postValue(true)
            Session.Status.Approved -> {
                fetchAccounts()
                isWalletConnected.postValue(true)
            }
            else -> Log.e("Wallet Connect Error", status.toString())
        }
    }

    companion object {
        private lateinit var client: OkHttpClient
        private lateinit var moshi: Moshi
        private lateinit var storage: WCSessionStore
        private lateinit var config: Session.Config
        private lateinit var session: Session
        private val bridgeUrl = "https://bridge.walletconnect.org"
        private val metaData = Session.PeerMeta(
            name = "nfscene",
            url = "nfscene.com",
            description = "nfscene WalletConnect demo",
            icons = listOf("data:image/svg+xml;base64,PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHZpZXdCb3g9IjAgMCAyMS41ODMgMTQuNTU3IiBoZWlnaHQ9IjU1LjAxOSIgd2lkdGg9IjgxLjU3NCIgeG1sbnM6dj0iaHR0cHM6Ly92ZWN0YS5pby9uYW5vIj48ZGVmcz48Y2xpcFBhdGggaWQ9IkEiPjxjaXJjbGUgcj0iMy4xOTIiIGN5PSItMzYzLjI2NyIgY3g9IjE4Ni42NTMiIGZpbGw9IiMyYzg5YTAiIGZpbGwtb3BhY2l0eT0iMSIgc3Ryb2tlPSJub25lIiBzdHJva2Utd2lkdGg9Ii41NjkiIHN0cm9rZS1saW5lY2FwPSJyb3VuZCIgc3Ryb2tlLWxpbmVqb2luPSJyb3VuZCIgc3Ryb2tlLW1pdGVybGltaXQ9IjQiIHN0cm9rZS1kYXNoYXJyYXk9Im5vbmUiIHN0cm9rZS1vcGFjaXR5PSIxIi8+PC9jbGlwUGF0aD48Y2xpcFBhdGggaWQ9IkIiPjxjaXJjbGUgcj0iMy4xOTIiIGN5PSItMzYzLjI2NyIgY3g9IjE4Ni42NTMiIGZpbGw9IiMyYzg5YTAiIGZpbGwtb3BhY2l0eT0iMSIgc3Ryb2tlPSJub25lIiBzdHJva2Utd2lkdGg9Ii41NjkiIHN0cm9rZS1saW5lY2FwPSJyb3VuZCIgc3Ryb2tlLWxpbmVqb2luPSJyb3VuZCIgc3Ryb2tlLW1pdGVybGltaXQ9IjQiIHN0cm9rZS1kYXNoYXJyYXk9Im5vbmUiIHN0cm9rZS1vcGFjaXR5PSIxIi8+PC9jbGlwUGF0aD48L2RlZnM+PGcgdHJhbnNmb3JtPSJ0cmFuc2xhdGUoLTI2Ljc4NiAtMTguOTg1KSI+PGcgdHJhbnNmb3JtPSJyb3RhdGUoOTApIj48Y2lyY2xlIHI9IjUuMjUiIGN5PSItMzIuMDM2IiBjeD0iMjYuMTkiIGZpbGw9IiM1ZmQzYmMiLz48Y2lyY2xlIHI9IjUuMjUiIGN5PSItNDMuMTE5IiBjeD0iMjYuMTQ5IiBmaWxsPSIjMmNhMDg5Ii8+PC9nPjxjaXJjbGUgY2xpcC1wYXRoPSJ1cmwoI0EpIiB0cmFuc2Zvcm09Im1hdHJpeCgwIDEuNjQ0NTc3IC0xLjY0NDU3NyAwIC01NTQuMzQ4MjYgLTI4MC44MDgwNikiIHI9IjQuMDY0IiBjeT0iLTM1OS44OTkiIGN4PSIxODYuNzIzIiBmaWxsPSIjNWZkM2JjIi8+PGNpcmNsZSBjbGlwLXBhdGg9InVybCgjQikiIHRyYW5zZm9ybT0ibWF0cml4KDAgLTEuNjQ0NTc3IDEuNjQ0NTc3IDAgNjI5LjUwNDcgMzMzLjE3NDk0KSIgcj0iNC4wNjQiIGN5PSItMzU5Ljg5OSIgY3g9IjE4Ni43MjMiIGZpbGw9IiMyY2EwODkiLz48Y2lyY2xlIHRyYW5zZm9ybT0icm90YXRlKDkwKSIgcj0iNi42ODMiIGN5PSItMzcuNTgiIGN4PSIyNi4yNjQiIGZpbGw9Im5vbmUiIHN0cm9rZT0iI2FmZTlkZCIgc3Ryb2tlLXdpZHRoPSIxLjE5MSIgc3Ryb2tlLWxpbmVqb2luPSJyb3VuZCIvPjwvZz48L3N2Zz4=")
        )
    }
}