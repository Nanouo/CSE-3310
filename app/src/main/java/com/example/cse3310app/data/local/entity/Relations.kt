package com.example.cse3310app.data.local.entity

import androidx.room.Embedded

// Projection used by cart and checkout UI to show listing details with quantity.
data class CartItemWithListing(
    @Embedded val cartItem: CartItemEntity,
    val title: String,
    val author: String,
    val price: Double,
    val imageUri: String?
)

// Projection for admin dashboard joins.
data class ListingWithSeller(
    @Embedded val listing: ListingEntity,
    val sellerUsername: String
)
