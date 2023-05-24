package de.inovex.blog.aidldemo.chatbot.client

import android.content.Context
import de.inovex.blog.aidldemo.chatbot.lib.Bot
import de.inovex.blog.aidldemo.chatbot.lib.BotDetails
import de.inovex.blog.aidldemo.chatbot.lib.BotManager
import de.inovex.blog.aidldemo.chatbot.lib.IBotService
import de.inovex.blog.aidldemo.chatbot.lib.Message
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.retry
import kotlinx.coroutines.flow.shareIn
import timber.log.Timber


/**
 * Repository to consume our API from the client side. Utilizes SharedFlow to have
 * a common connection for all calls and automatically handles retries, connection losses
 * and tear-downs when no subscriber are registered anymore.
 */
class RemoteBotRepository(
    private val context: Context,
    coroutineScope: CoroutineScope
) {

    private val sharedConnectedBot = callbackFlow<Bot?> {
        BotManager.connect(context, object : BotManager.BotServiceCallback {
            override fun onConnected(service: IBotService) {
                Timber.d("onConnected() called with: service = [$service]")
                trySend(Bot(service))
            }

            override fun onConnectionLost() {
                Timber.d("onConnectionLost() called")
                cancel("connection to bot service lost")
            }

            override fun onConnectionFailed(e: Exception) {
                Timber.d("onConnectionFailed() called with: e = [$e]")
                cancel("connection to bot service failed", e)
            }
        })
        awaitClose {
            Timber.d("disconnecting from bot service")
            BotManager.disconnect(context)
        }
    }
        .retry(3) {
            Timber.d("retry: will retry connection after delay")
            delay(1_000L)
            return@retry true
        }
        .catch { emit(null) }
        .shareIn(
            coroutineScope,
            SharingStarted.WhileSubscribed(1_000L, 0),
            1
        )

    @OptIn(ExperimentalCoroutinesApi::class)
    fun getMessages(): Flow<List<Message>> =
        sharedConnectedBot.flatMapLatest { it?.getMessages() ?: emptyFlow() }

    suspend fun getBotDetails(): BotDetails? =
        sharedConnectedBot.first()?.getBotDetails()

    suspend fun sendMessage(message: Message) {
        sharedConnectedBot.first()?.sendMessage(message)
    }

    suspend fun newSession() {
        sharedConnectedBot.first()?.newSession()
    }

}
