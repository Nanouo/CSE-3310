package com.reread.app.ui.messaging

import android.os.Bundle
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.reread.app.R
import com.reread.app.data.MessagingRepository
import com.reread.app.utils.SessionManager

class ChatActivity : AppCompatActivity() {

    private lateinit var adapter: MessageAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var etMessage: EditText
    private lateinit var btnSend: ImageButton
    private lateinit var repo: MessagingRepository
    private var conversationId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        conversationId    = intent.getIntExtra("conversation_id", -1)
        val bookTitle     = intent.getStringExtra("book_title") ?: ""
        val otherUsername = intent.getStringExtra("other_username") ?: ""

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title    = otherUsername
        supportActionBar?.subtitle = bookTitle
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { finish() }

        repo         = MessagingRepository(this)
        recyclerView = findViewById(R.id.rv_messages)
        etMessage    = findViewById(R.id.et_message)
        btnSend      = findViewById(R.id.btn_send)

        val session = SessionManager(this)

        adapter = MessageAdapter(emptyList(), session.userId)
        recyclerView.layoutManager = LinearLayoutManager(this).also {
            it.stackFromEnd = true
        }
        recyclerView.adapter = adapter

        loadMessages()

        btnSend.setOnClickListener {
            val content = etMessage.text.toString().trim()
            if (content.isBlank()) return@setOnClickListener
            if (conversationId == -1) {
                Toast.makeText(this, "Invalid conversation", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            repo.sendMessage(conversationId, session.userId, session.username, content)
            etMessage.setText("")
            loadMessages()
        }
    }

    private fun loadMessages() {
        val messages = repo.getMessages(conversationId)
        adapter.updateMessages(messages)
        if (adapter.itemCount > 0) {
            recyclerView.scrollToPosition(adapter.itemCount - 1)
        }
    }
}