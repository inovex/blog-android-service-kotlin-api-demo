package de.inovex.blog.aidldemo.chatbot.lib;

import de.inovex.blog.aidldemo.chatbot.lib.BotDetails;

/**
* Callback inteface which passes the BotDetails to the consumer.
*/
interface BotDetailsCallback {
    oneway void valueChanged(in BotDetails botDetails);
}

