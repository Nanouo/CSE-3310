package com.example.cse3310app.ui.inbox

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.cse3310app.ui.viewmodel.InboxViewModel

@Composable
fun InboxScreen(userId: Long, inboxViewModel: InboxViewModel, onOpenChat: (Long) -> Unit) {
    val conversations by inboxViewModel.conversations(userId).collectAsStateWithLifecycle()

    Column(modifier = Modifier.fillMaxSize().padding(12.dp)) {
        Text("Inbox", style = MaterialTheme.typography.headlineSmall)
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(conversations) { convo ->
                Card(modifier = Modifier.fillMaxWidth().clickable { onOpenChat(convo.conversationId) }) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("Conversation #${convo.conversationId}")
                        Text("Listing ${convo.listingId}")
                    }
                }
            }
        }
    }
}

@Composable
fun ChatScreen(
    conversationId: Long,
    inboxViewModel: InboxViewModel,
    fallbackBuyerId: Long,
    fallbackSellerId: Long,
    fallbackListingId: Long,
    currentUserId: Long
) {
    val messages by inboxViewModel.messages(conversationId).collectAsStateWithLifecycle()
    var text by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize().padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text("Chat", style = MaterialTheme.typography.headlineSmall)
        LazyColumn(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            items(messages) { msg ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text(if (msg.senderId == currentUserId) "You" else "User ${msg.senderId}")
                        Text(msg.text)
                    }
                }
            }
        }
        OutlinedTextField(
            value = text,
            onValueChange = { text = it },
            label = { Text("Message") },
            modifier = Modifier.fillMaxWidth()
        )
        Button(
            onClick = {
                inboxViewModel.sendMessage(
                    buyerId = fallbackBuyerId,
                    sellerId = fallbackSellerId,
                    listingId = fallbackListingId,
                    senderId = currentUserId,
                    receiverId = if (currentUserId == fallbackBuyerId) fallbackSellerId else fallbackBuyerId,
                    text = text
                )
                text = ""
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Send")
        }
    }
}
