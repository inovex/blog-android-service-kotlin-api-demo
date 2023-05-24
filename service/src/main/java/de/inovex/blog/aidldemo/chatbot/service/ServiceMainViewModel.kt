package de.inovex.blog.aidldemo.chatbot.service

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

class ServiceMainViewModel(private val localBotRepository: LocalBotRepository) : ViewModel() {

    private val _botDetails = MutableStateFlow(BotDetails("", false))
    val botDetails: StateFlow<BotDetails> = _botDetails.asStateFlow()

    val messages = localBotRepository.getMessages()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    init {
        viewModelScope.launch {
            localBotRepository.getBotDetails()?.let {
                _botDetails.value = it
            }
        }
    }

    fun sendMessage(message: Message) {
        viewModelScope.launch {
            localBotRepository.sendMessage(message)
        }
    }

}
