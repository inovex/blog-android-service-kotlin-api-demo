package de.inovex.blog.aidldemo.chatbot.service

import de.inovex.blog.aidldemo.chatbot.lib.BotDetailsCallback
import de.inovex.blog.aidldemo.chatbot.lib.Message
import de.inovex.blog.aidldemo.chatbot.lib.MessagesCallback
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


/**
 * Actual BotService implementation which directly queries to [localBotRepository] or indirectly queries
 * it with an instance of [SubscribingRemoteCallbackList] to asynchronously handle callback subscriptions.
 */
class BotServiceDelegate(
    private val localBotRepository: LocalBotRepository,
    private val coroutineScope: CoroutineScope
) {

private val messagesCallbackList =
    SubscribingRemoteCallbackList<MessagesCallback, List<Message>>(
        localBotRepository.getMessages(),
        coroutineScope
    ) { callback, value -> callback.valueChanged(value.toTypedArray()) }

    fun sendMessage(message: Message) {
        coroutineScope.launch {
            localBotRepository.sendMessage(message)
        }
    }

    fun newSession() {
        coroutineScope.launch {
            localBotRepository.newSession()
        }
    }

    fun getBotDetails(callback: BotDetailsCallback) {
        coroutineScope.launch {
            callback.valueChanged(localBotRepository.getBotDetails())
        }
    }

    fun registerForMessages(callback: MessagesCallback) {
        coroutineScope.launch {
            messagesCallbackList.registerAndCollect(callback)
        }
    }

    fun unregisterForMessages(callback: MessagesCallback) {
        coroutineScope.launch {
            messagesCallbackList.unregisterAndCancel(callback)
        }
    }
}


