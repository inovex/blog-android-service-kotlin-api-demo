package de.inovex.blog.aidldemo.chatbot.service


import android.app.Service
import android.content.Intent
import android.os.IBinder
import de.inovex.blog.aidldemo.chatbot.lib.BotDetailsCallback
import de.inovex.blog.aidldemo.chatbot.lib.IBotService
import de.inovex.blog.aidldemo.chatbot.lib.Message
import de.inovex.blog.aidldemo.chatbot.lib.MessagesCallback
import org.koin.android.ext.android.inject
import timber.log.Timber

/**
 * Bound Android service which implements our [IBotService].aidl interface and delegates all
 * calls to [BotServiceDelegate] for better readability.
 */
class BotService : Service() {

    val botServiceDelegate: BotServiceDelegate by inject()

    override fun onBind(p0: Intent?): IBinder {
        Timber.d("onBind() called")
        return object : IBotService.Stub(), IBinder {

            override fun getVersion() = 1

            override fun sendMessage(message: Message) =
                botServiceDelegate.sendMessage(message)

            override fun newSession() =
                botServiceDelegate.newSession()

            override fun getBotDetails(callback: BotDetailsCallback) =
                botServiceDelegate.getBotDetails(callback)

            override fun registerForMessages(callback: MessagesCallback) =
                botServiceDelegate.registerForMessages(callback)

            override fun unregisterForMessages(callback: MessagesCallback) =
                botServiceDelegate.unregisterForMessages(callback)
        }
    }

    override fun onDestroy() {
        Timber.d("onDestroy() called")
        super.onDestroy()
    }
}


