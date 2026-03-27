package com.example.cse3310app.ui.search

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
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.cse3310app.ui.viewmodel.SearchViewModel

@Composable
fun SearchScreen(searchViewModel: SearchViewModel, onOpenListing: (Long) -> Unit) {
    val query by searchViewModel.query.collectAsStateWithLifecycle()
    val resultsFlow = remember(query) { searchViewModel.searchNow() }
    val results by resultsFlow.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier.fillMaxSize().padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        OutlinedTextField(
            value = query,
            onValueChange = { searchViewModel.updateQuery(it) },
            label = { Text("Search by title / author / ISBN") },
            modifier = Modifier.fillMaxWidth()
        )
        Button(onClick = {}, modifier = Modifier.fillMaxWidth()) { Text("Results") }
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(results) { listing ->
                Card(modifier = Modifier.fillMaxWidth().clickable { onOpenListing(listing.listingId) }) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(listing.title, style = MaterialTheme.typography.titleMedium)
                        Text("${listing.author} • ${listing.isbn}")
                        Text("$${listing.price}")
                    }
                }
            }
        }
    }
}
