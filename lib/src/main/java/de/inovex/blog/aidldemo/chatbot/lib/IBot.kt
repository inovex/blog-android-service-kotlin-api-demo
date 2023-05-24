package de.inovex.blog.aidldemo.chatbot.lib

import kotlinx.coroutines.flow.Flow

interface IBot {
    /**
     * Start a new chat session.
     */
    fun newSession()

    /**
     * Send a message to the bot service.
     */
    fun sendMessage(message: Message)

    /**
     * Asynchronous get of bot details
     */
    suspend fun getBotDetails(): BotDetails

    /**
     * Asynchronous get of messages as flow.
     */
    fun getMessages(): Flow<List<Message>>
}
