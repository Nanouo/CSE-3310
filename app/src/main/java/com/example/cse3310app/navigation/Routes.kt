package com.example.cse3310app.navigation

object Routes {
    const val Splash = "splash"
    const val Login = "login"
    const val Register = "register"
    const val GenrePreference = "genre"
    const val Home = "home"
    const val Search = "search"
    const val MyListings = "my_listings"
    const val ListingForm = "listing_form"
    const val BookDetail = "book_detail/{listingId}"
    const val Cart = "cart"
    const val Checkout = "checkout"
    const val Inbox = "inbox"
    const val Chat = "chat/{conversationId}"
    const val Admin = "admin"
    const val Account = "account"

    fun bookDetail(listingId: Long) = "book_detail/$listingId"
    fun chat(conversationId: Long) = "chat/$conversationId"
}

data class BottomNavItem(val route: String, val label: String)

val bottomNavItems = listOf(
    BottomNavItem(Routes.Home, "Home"),
    BottomNavItem(Routes.Inbox, "Inbox"),
    BottomNavItem(Routes.MyListings, "My Listings"),
    BottomNavItem(Routes.Account, "Account")
)
