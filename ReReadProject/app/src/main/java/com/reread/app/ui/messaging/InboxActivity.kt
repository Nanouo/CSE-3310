package com.reread.app.ui.messaging

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.reread.app.R
import com.reread.app.data.MessagingRepository
import com.reread.app.utils.SessionManager

class InboxActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inbox)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Inbox"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { finish() }

        val session       = SessionManager(this)
        val repo          = MessagingRepository(this)
        val conversations = repo.getConversationsForUser(session.userId)

        val recyclerView = findViewById<RecyclerView>(R.id.rv_conversations)
        val tvEmpty      = findViewById<TextView>(R.id.tv_empty)

        if (conversations.isEmpty()) {
            tvEmpty.visibility      = View.VISIBLE
            recyclerView.visibility = View.GONE
        } else {
            tvEmpty.visibility      = View.GONE
            recyclerView.visibility = View.VISIBLE
            recyclerView.layoutManager = LinearLayoutManager(this)
            recyclerView.adapter = ConversationAdapter(conversations) { conversation ->
                val intent = Intent(this, ChatActivity::class.java)
                intent.putExtra("conversation_id", conversation.id)
                intent.putExtra("book_title",      conversation.bookTitle)
                intent.putExtra("other_username",
                    if (session.userId == conversation.buyerId)
                        conversation.sellerUsername
                    else
                        conversation.buyerUsername
                )
                startActivity(intent)
            }
        }
    }
}