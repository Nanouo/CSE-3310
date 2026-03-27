package com.example.cse3310app.ui.listing

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
import com.example.cse3310app.data.local.entity.ListingEntity
import com.example.cse3310app.ui.viewmodel.ListingViewModel

@Composable
fun ListingFormScreen(userId: Long, listingViewModel: ListingViewModel, onDone: () -> Unit) {
    var title by remember { mutableStateOf("") }
    var author by remember { mutableStateOf("") }
    var isbn by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var condition by remember { mutableStateOf("Good") }
    var category by remember { mutableStateOf("General") }
    var subcategory by remember { mutableStateOf("Books") }
    var imageUri by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text("Create Listing", style = MaterialTheme.typography.headlineSmall)
        OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Title") })
        OutlinedTextField(value = author, onValueChange = { author = it }, label = { Text("Author") })
        OutlinedTextField(value = isbn, onValueChange = { isbn = it }, label = { Text("ISBN") })
        OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Description") })
        OutlinedTextField(value = price, onValueChange = { price = it }, label = { Text("Price") })
        OutlinedTextField(value = condition, onValueChange = { condition = it }, label = { Text("Condition") })
        OutlinedTextField(value = category, onValueChange = { category = it }, label = { Text("Category") })
        OutlinedTextField(value = subcategory, onValueChange = { subcategory = it }, label = { Text("Subcategory") })
        OutlinedTextField(value = imageUri, onValueChange = { imageUri = it }, label = { Text("Image URI") })
        Button(
            onClick = {
                val parsedPrice = price.toDoubleOrNull() ?: 0.0
                listingViewModel.createListing(
                    ListingEntity(
                        sellerId = userId,
                        title = title,
                        author = author,
                        isbn = isbn,
                        description = description,
                        price = parsedPrice,
                        condition = condition,
                        category = category,
                        subcategory = subcategory,
                        imageUri = imageUri.ifBlank { null }
                    )
                )
                onDone()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Save Listing")
        }
    }
}

@Composable
fun MyListingsScreen(userId: Long, listingViewModel: ListingViewModel, onCreate: () -> Unit) {
    val listings by listingViewModel.myListings(userId).collectAsStateWithLifecycle()

    Column(modifier = Modifier.fillMaxSize().padding(12.dp)) {
        Row(modifier = Modifier.fillMaxWidth()) {
            Button(onClick = onCreate, modifier = Modifier.fillMaxWidth()) {
                Text("Create New Listing")
            }
        }
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(top = 10.dp)) {
            items(listings) { listing ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(listing.title, style = MaterialTheme.typography.titleMedium)
                        Text("${listing.author} • $${listing.price}")
                        Button(onClick = { listingViewModel.deactivateListing(listing.listingId) }) {
                            Text("Deactivate")
                        }
                    }
                }
            }
        }
    }
}
