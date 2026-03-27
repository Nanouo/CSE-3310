package com.example.cse3310app.ui.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.cse3310app.ui.viewmodel.HomeViewModel

@Composable
fun HomeScreen(
    homeViewModel: HomeViewModel,
    onOpenCategories: () -> Unit,
    onOpenSearch: () -> Unit,
    onOpenCart: () -> Unit,
    onOpenListing: (Long) -> Unit
) {
    val listings by homeViewModel.listings.collectAsStateWithLifecycle()

    Column(modifier = Modifier.fillMaxSize().padding(12.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
            Button(onClick = onOpenCategories, modifier = Modifier.weight(1f)) { Text("Categories") }
            Button(onClick = onOpenSearch, modifier = Modifier.weight(1f)) { Text("Search") }
            Button(onClick = onOpenCart, modifier = Modifier.weight(1f)) { Text("Cart") }
        }
        Text(
            text = "Recent Listings",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(vertical = 12.dp)
        )
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(listings) { listing ->
                Card(modifier = Modifier.fillMaxWidth().clickable { onOpenListing(listing.listingId) }) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(listing.title, style = MaterialTheme.typography.titleMedium)
                        Text("${listing.author} • $${listing.price}")
                        Text("${listing.category} / ${listing.subcategory}")
                    }
                }
            }
        }
    }
}
