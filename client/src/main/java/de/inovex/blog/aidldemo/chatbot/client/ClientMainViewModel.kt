package de.inovex.blog.aidldemo.chatbot.client

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.inovex.blog.aidldemo.chatbot.lib.BotDetails
import de.inovex.blog.aidldemo.chatbot.lib.Message
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ClientMainViewModel(private val remoteBotRepository: RemoteBotRepository) : ViewModel() {
    private val _botDetails = MutableStateFlow(BotDetails("", false))
    val botDetails: StateFlow<BotDetails> = _botDetails.asStateFlow()

    val messages = remoteBotRepository.getMessages()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    init {
        viewModelScope.launch {
            remoteBotRepository.getBotDetails()?.let {
                _botDetails.value = it
            }
        }
    }

    fun sendMessage(message: Message) {
        viewModelScope.launch {
            remoteBotRepository.sendMessage(message)
        }
    }

    fun newSession() {
        viewModelScope.launch {
            remoteBotRepository.newSession()
        }
    }

}
