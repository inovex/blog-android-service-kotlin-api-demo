package de.inovex.blog.aidldemo.chatbot.lib

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
data class BotDetails(
    val name: String,
    val online: Boolean
) : Parcelable