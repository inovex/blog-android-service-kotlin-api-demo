package de.inovex.blog.aidldemo.chatbot.service

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Card
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import de.inovex.blog.aidldemo.chatbot.lib.BotDetails
import de.inovex.blog.aidldemo.chatbot.lib.Message
import de.inovex.blog.aidldemo.chatbot.lib.Sender
import de.inovex.blog.aidldemo.chatbot.service.ui.theme.BotAppTheme
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.time.Instant
import java.util.Date
import java.util.Locale

class ServiceMainActivity : ComponentActivity() {

    private val viewModel by viewModel<ServiceMainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BotAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val messages by viewModel.messages.collectAsStateWithLifecycle()
                    val botDetails by viewModel.botDetails.collectAsStateWithLifecycle()
                    Chat(messages, botDetails) {
                        viewModel.sendMessage(it)
                    }
                }
            }
        }
    }
}

val ReceiverShape = RoundedCornerShape(8.dp, 8.dp, 0.dp, 8.dp)
val SenderShape = RoundedCornerShape(8.dp, 8.dp, 8.dp, 0.dp)

@Composable
fun Chat(messages: List<Message>, botDetails: BotDetails, sendMessage: (Message) -> Unit) {

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Row(Modifier.background(Color.Gray)) {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                text = "Service",
                textAlign = TextAlign.Center,
                color = Color.Green,
                fontSize = 27.sp
            )
        }
        ChatTitle(
            username = botDetails.name,
            profile = painterResource(id = R.drawable.ic_launcher_background),
            isOnline = botDetails.online
        )
        ChatMessages(Modifier.weight(1f), messages)
        ChatInput {
            sendMessage(it)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatInput(sendMessage: (message: Message) -> Unit) {
    var message by remember { mutableStateOf("") }

    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
    ) {
        OutlinedTextField(
            placeholder = { Text("Send a message ...") },
            value = message,
            onValueChange = { it ->
                message = it
            },
            shape = RoundedCornerShape(10.dp),
            trailingIcon = {
                Icon(
                    Icons.Default.Send,
                    contentDescription = "Send button",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.clickable {
                        sendMessage(Message(message, Sender.USER, Instant.now().epochSecond))
                        message = ""
                    }
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        )
    }
}

@Composable
fun ChatMessages(modifier: Modifier = Modifier, messages: List<Message>) {
    val simpleDateFormat = SimpleDateFormat("h:mm a", Locale.ENGLISH)
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        reverseLayout = true
    ) {
        items(messages.reversed()) {
            ChatMessageItem(
                messageText = it.text,
                time = simpleDateFormat.format(Date(it.time * 1000)),
                received = it.sender == Sender.BOT
            )
        }
    }
}

@Composable
fun ChatMessageItem(messageText: String, time: String, received: Boolean) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = if (received) Alignment.End else Alignment.Start
    ) {
        Box(
            modifier = Modifier
                .background(
                    if (received) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary,
                    shape = if (received) ReceiverShape else SenderShape
                )
                .padding(16.dp, 8.dp, 16.dp, 8.dp)
        ) {
            Text(text = messageText, color = Color.White)
        }
        Row {
            if (received) {
                Text(
                    modifier = Modifier.padding(8.dp),
                    text = time,
                    fontSize = 12.sp
                )
            }
            Icon(
                Icons.Default.Check,
                contentDescription = "Check Mark",
                modifier = Modifier
                    .padding(vertical = 8.dp)
                    .size(16.dp),
                tint = Color.Green
            )
            if (!received) {
                Text(
                    modifier = Modifier.padding(8.dp),
                    text = time,
                    fontSize = 12.sp
                )
            }
        }
    }
}

@Composable
fun ChatTitle(username: String, profile: Painter, isOnline: Boolean) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(70.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = profile,
                contentDescription = "Profile picture",
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
            )
            Spacer(modifier = Modifier.width(8.dp))

            Column {
                Text(text = username, fontWeight = FontWeight.SemiBold)
                Text(
                    text = if (isOnline) "online" else "offline",
                    fontSize = 12.sp
                )
            }
        }
    }
}