package de.inovex.blog.aidldemo.chatbot.lib

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
data class Message(
    val text: String,
    val sender: Sender,
    val time: Long
) : Parcelable