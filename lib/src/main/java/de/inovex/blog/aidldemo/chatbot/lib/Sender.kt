package de.inovex.blog.aidldemo.chatbot.lib

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
enum class Sender : Parcelable {
    BOT,
    USER
}