package de.inovex.blog.aidldemo.chatbot.lib;

import de.inovex.blog.aidldemo.chatbot.lib.Message;
import de.inovex.blog.aidldemo.chatbot.lib.BotDetailsCallback;
import de.inovex.blog.aidldemo.chatbot.lib.MessagesCallback;

/**
* Full BotService API
*/
interface IBotService {

    // aidl version to support backward compatibility
    int getVersion();

    // Send a message to the bot service.
    oneway void sendMessage(in Message message);

    // Send a message to the bot service.
    oneway void newSession();

    // Asynchronous get of bot details
    void getBotDetails(in BotDetailsCallback callback);

    // Asynchronous get of messages
void registerForMessages(in MessagesCallback callback);
void unregisterForMessages(in MessagesCallback callback);

}