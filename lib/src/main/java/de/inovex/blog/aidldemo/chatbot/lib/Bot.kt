package de.inovex.blog.aidldemo.chatbot.lib

import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import timber.log.Timber
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/**
 * Kotlin Bot Service API Abstractions. To create a clean Kotlin API for customers.
 */
class Bot(private val service: IBotService) : IBot {

    /**
     * Start a new chat session.
     */
    override fun newSession() {
        Timber.d("newSession() called")
        service.newSession()
    }

    /**
     * Send a message to the bot service.
     */
    override fun sendMessage(message: Message) {
        Timber.d("sendMessage() called")
        service.sendMessage(message)
    }

    /**
     * Asynchronous get of bot details with [suspendCoroutine].
     */
override suspend fun getBotDetails() = suspendCoroutine { continuation ->
    Timber.d("getBotDetails() called with: continuation = [$continuation]")
    service.getBotDetails(object : BotDetailsCallback.Stub() {
        override fun valueChanged(botDetails: BotDetails) {
            continuation.resume(botDetails)
        }
    })
}

    /**
     * Asynchronous get of messages with [callbackFlow] as flow.
     */
override fun getMessages() = callbackFlow {
    Timber.d("getMessages() called")
    val callback = object : MessagesCallback.Stub() {
        override fun valueChanged(messages: Array<out Message>) {
            Timber.d("valueChanged() called with: messages = [$messages]")
            trySend(messages.asList())
        }
    }
    Timber.d("register messages callback")
    service.registerForMessages(callback)
    awaitClose {
        Timber.d("unregister messages callback")
        service.unregisterForMessages(callback)
    }
}

}