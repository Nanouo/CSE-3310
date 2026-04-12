package com.reread.app.ui.admin

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.reread.app.R
import com.reread.app.data.User

class AdminUsersAdapter(
    private val users: MutableList<User>,
    private val onToggleActive: (User) -> Unit,
    private val onDelete: (User) -> Unit
) : RecyclerView.Adapter<AdminUsersAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_admin_user, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(users[position])
    }

    override fun getItemCount() = users.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvUsername: TextView = itemView.findViewById(R.id.tv_admin_username)
        private val tvEmail: TextView    = itemView.findViewById(R.id.tv_admin_email)
        private val tvRole: TextView     = itemView.findViewById(R.id.tv_admin_role)
        private val tvStatus: TextView   = itemView.findViewById(R.id.tv_admin_status)
        private val btnToggle: Button    = itemView.findViewById(R.id.btn_toggle_active)
        private val btnDelete: Button    = itemView.findViewById(R.id.btn_delete_user)

        fun bind(user: User) {
            tvUsername.text = user.username
            tvEmail.text    = user.email
            tvRole.text     = user.role.replaceFirstChar { it.uppercase() }
            tvStatus.text   = if (user.isActive) "Active" else "Disabled"
            tvStatus.setTextColor(
                itemView.context.getColor(
                    if (user.isActive) android.R.color.holo_green_dark
                    else android.R.color.holo_red_dark
                )
            )
            btnToggle.text = if (user.isActive) "Disable" else "Enable"
            btnToggle.setOnClickListener { onToggleActive(user) }
            btnDelete.setOnClickListener { onDelete(user) }
        }
    }
}