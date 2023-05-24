package de.inovex.blog.aidldemo.chatbot.lib;

import de.inovex.blog.aidldemo.chatbot.lib.Message;

/**
* Callback inteface which passes the complete list of messages to the consumer.
*/
interface MessagesCallback {
    oneway void valueChanged(in Message[] messages);
}

