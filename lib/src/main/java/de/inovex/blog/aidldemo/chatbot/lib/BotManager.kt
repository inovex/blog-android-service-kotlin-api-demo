package de.inovex.blog.aidldemo.chatbot.lib

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import timber.log.Timber

/**
 * Connection manager which handles the binding of the bound BotService, checks for availability
 * and reduces callback complexity for the consumer side. A basic connection looks like this:
 */
object BotManager {

    private var serviceConnection: ServiceConnection? = null

    fun connect(context: Context, callback: BotServiceCallback) {
        try {
            check(serviceConnection == null) { "service is already connected" }

            Timber.i("connecting to BotService")
            serviceConnection = BotServiceConnection(callback)
            val intent = Intent().apply {
                component = ComponentName(
                    "de.inovex.blog.aidldemo.chatbot.service",
                    "de.inovex.blog.aidldemo.chatbot.service.BotService"
                )
            }
            val serviceBound = context.bindService(
                intent,
                serviceConnection!!,
                Context.BIND_AUTO_CREATE
            )
            check(serviceBound) { "Service was not found" }
        } catch (e: SecurityException) {
            callback.onConnectionFailed(e)
            Timber.w(e, "could not connect to BotService")
            serviceConnection = null
        } catch (e: IllegalStateException) {
            callback.onConnectionFailed(e)
            Timber.w(e, e.message)
        }
    }

    fun disconnect(context: Context) {
        if (serviceConnection != null) {
            context.unbindService(serviceConnection!!)
            serviceConnection = null
        } else {
            Timber.w("not yet connected to BotService")
        }
    }

    interface BotServiceCallback {
        fun onConnected(service: IBotService)
        fun onConnectionLost()
        fun onConnectionFailed(e: Exception)
    }

    internal class BotServiceConnection(
        private val callback: BotServiceCallback
    ) : ServiceConnection {

        override fun onServiceConnected(name: ComponentName?, service: IBinder) {
            Timber.i("connected to BotService")
            callback.onConnected(IBotService.Stub.asInterface(service))
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            Timber.i("disconnected from BotService")
            callback.onConnectionLost()
        }

        override fun onNullBinding(name: ComponentName?) {
            Timber.i("received null binding from BotService")
            callback.onConnectionFailed(NullPointerException("received null binding from BotService"))
        }

        override fun onBindingDied(name: ComponentName?) {
            Timber.i("connection died from BotService")
            callback.onConnectionLost()
        }
    }
}
