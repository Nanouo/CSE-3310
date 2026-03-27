package com.example.cse3310app.ui.admin

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.cse3310app.ui.viewmodel.AdminViewModel

@Composable
fun AdminDashboardScreen(adminId: Long, adminViewModel: AdminViewModel) {
    val users by adminViewModel.users.collectAsStateWithLifecycle()
    val listings by adminViewModel.listings.collectAsStateWithLifecycle()
    val logs by adminViewModel.logs.collectAsStateWithLifecycle()

    Column(modifier = Modifier.fillMaxSize().padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("Admin Dashboard")
        Text("Users")
        LazyColumn(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(users) { user ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text("${user.username} (${user.role})")
                        Button(onClick = {
                            adminViewModel.disableUser(adminId, user.userId, !user.isDisabled)
                        }) {
                            Text(if (user.isDisabled) "Enable" else "Disable")
                        }
                    }
                }
            }
        }
        Text("Listings")
        LazyColumn(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(listings) { listing ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text("${listing.listing.title} by ${listing.sellerUsername}")
                        Button(onClick = { adminViewModel.removeListing(adminId, listing.listing.listingId) }) {
                            Text("Remove")
                        }
                    }
                }
            }
        }
        Text("Recent Admin Logs")
        logs.take(5).forEach { log ->
            Text("${log.action} ${log.targetType}#${log.targetId}")
        }
    }
}
