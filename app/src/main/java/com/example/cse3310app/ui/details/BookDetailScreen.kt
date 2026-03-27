package com.example.cse3310app.ui.details

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.cse3310app.data.local.entity.ListingEntity
import com.example.cse3310app.data.repository.ListingRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun BookDetailScreen(
    listingId: Long,
    listingRepository: ListingRepository,
    onAddToCart: (Long) -> Unit
) {
    var listing by remember { mutableStateOf<ListingEntity?>(null) }

    LaunchedEffect(listingId) {
        listing = withContext(Dispatchers.IO) { listingRepository.getListing(listingId) }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        if (listing == null) {
            Text("Loading listing...")
            return@Column
        }
        val item = listing ?: return@Column
        AsyncImage(
            model = item.imageUri,
            contentDescription = item.title,
            modifier = Modifier.fillMaxWidth().height(180.dp)
        )
        Text(item.title, style = MaterialTheme.typography.headlineSmall)
        Text("by ${item.author}")
        Text("ISBN: ${item.isbn}")
        Text(item.description)
        Text("Condition: ${item.condition}")
        Text("$${item.price}", style = MaterialTheme.typography.titleLarge)
        Button(onClick = { onAddToCart(item.listingId) }, modifier = Modifier.fillMaxWidth()) {
            Text("Add to Cart")
        }
    }
}
