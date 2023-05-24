package de.inovex.blog.aidldemo.chatbot.service

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import de.inovex.blog.aidldemo.chatbot.lib.BotDetails
import de.inovex.blog.aidldemo.chatbot.lib.Message
import de.inovex.blog.aidldemo.chatbot.lib.Sender
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import timber.log.Timber
import java.time.Instant

/**
 * This repository is a dummy implementation which stores all data into jetpack datastore and
 * automatically gives an "Answer" when sending a message after 2 seconds of delay.
 */
class LocalBotRepository(private val dataStore: DataStore<Preferences>) {

    fun getMessages(): Flow<List<Message>> {
        Timber.d("getMessages() called")
        return dataStore.data.map {
            it[messagesKey]?.let { json -> Json.decodeFromString(json) } ?: emptyList()
        }
    }

    suspend fun sendMessage(message: Message) {
        Timber.d("sendMessage() called with: message = [$message]")
        appendMessage(message)
        delay(2000)
        appendMessage(Message("The Ansewer", Sender.BOT, Instant.now().epochSecond))
    }

    private suspend fun appendMessage(message: Message) {
        dataStore.edit {
            val json = it[messagesKey]
            val messages = if (json != null) {
                Json.decodeFromString<MutableList<Message>>(json)
            } else {
                mutableListOf()
            }
            messages.add(message)
            it[messagesKey] = Json.encodeToString(messages)
        }
    }

    suspend fun getBotDetails(): BotDetails {
        Timber.d("getBotDetails() called")
        return dataStore.data.map {
            it[botDetailsKey]?.let { json ->
                Json.decodeFromString<BotDetails>(json)
            }
        }.firstOrNull() ?: BotDetails("inovex Bot", true)
    }

    suspend fun newSession() {
        Timber.d("newSession() called")
        dataStore.edit {
            it[messagesKey] = "[]"
        }
    }

    companion object {
        val messagesKey = stringPreferencesKey("messagesKey")
        val botDetailsKey = stringPreferencesKey("botDetailsKey")
    }
}