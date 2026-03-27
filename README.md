You are an expert Android Kotlin developer. Use the following SRA/project requirements to build a complete Android Studio app. Generate Kotlin code for Room entities, DAOs, repositories, ViewModels, Jetpack Compose screens, navigation graph, and utility classes according to MVVM. Include comments explaining key parts.  

Here is the SRA / project description:

[Use the following additional SRA requirements to add more context and details to the Android app:

---

**4.5 Checkout and Payment**
- Sequence No: 5
- Short description: Shows users their visual order process and confirms the order for the buyer.
- Description: After a user searches for a book and adds it to their cart, the app should display the user's information in a visual table or form, and allow the user to confirm the order.
- Pre-Conditions: User must fill out all required information to purchase the book, such as card information, payment method, etc.
- Post-Conditions: User proceeds through the checkout process; order is stored locally in the Orders table.
- Other attributes: Include a simulated payment flow; validate form inputs; store order details in the local SQLite/Room database.

---

**4.6 Admin Management**
- Sequence No: 6
- Short description: Enables admins to manage user information.
- Description: Admins can access the database and manage/view listings and user account information.
- Pre-Conditions: Admin login verified.
- Post-Conditions: Admin changes are reflected in the database.
- Other attributes: All admin actions must be logged in an AdminLogs table.

---

**4.7 Data Management**
- Sequence No: 7
- Short description: Ensures the SQLite database is structured properly and accessible/maintainable.
- Description: All user information, listings, and order data are stored in SQLite tables. Tables include Users, Orders, Listings, CartItems, etc. Primary and foreign keys are used to uniquely identify records and relationships.
- Pre-Conditions: SQLite database initialized and organized properly.
- Post-Conditions: Database is accessible and manageable by the app and by admins as needed.
- Other attributes: Maintain proper relationships using primary/foreign keys; enforce integrity constraints.]

---

Here is the full blueprint/spec to implement the project:

1. App Platform & Stack
- Android only
- Kotlin + Jetpack Compose UI
- Local database with SQLite via Room
- Navigation Compose
- MVVM architecture with ViewModel + StateFlow
- Repository pattern
- Coil for image loading
- DataStore for session/login persistence
- Target SDK 34, min SDK 24
- No internet required

2. Database Schema
- Users(userId, username, email, passwordHash, role, isDisabled, preferredGenres)
- Listings(listingId, sellerId, title, author, isbn, description, price, condition, category, subcategory, imageUri, isActive, createdAt)
- CartItems(cartItemId, userId, listingId, quantity)
- Orders(orderId, buyerId, totalAmount, paymentMethod, status, createdAt)
- OrderItems(orderItemId, orderId, listingId, priceAtPurchase)
- Messages(messageId, conversationId, senderId, receiverId, listingId, text, timestamp)
- Conversations(conversationId, buyerId, sellerId, listingId, lastUpdated)
- AdminLogs(logId, adminId, action, targetType, targetId, timestamp)
- GenrePreferences(prefId, userId, genre)

3. App Modules / Screens
- Auth: RegisterScreen, LoginScreen, GenrePreferenceScreen
- Home: HomeScreen
- Listings: ListingFormScreen, MyListingsScreen
- Book Details: BookDetailScreen
- Search: SearchScreen
- Cart & Checkout: CartScreen, CheckoutScreen (simulate payment)
- Inbox / Messaging: InboxScreen, ChatScreen (plain text, local storage)
- Admin: AdminDashboardScreen (view users/listings, disable/remove, logs)
- Account: AccountScreen

4. Navigation
- Bottom navigation: Home, Inbox, My Listings, Account
- Top actions: Categories, Search, Cart
- Splash screen checks session → redirect to main app or auth
- Navigation graph with composable routes for each screen

5. Functionality
- Registration: hash passwords, store locally
- Login: validate hashed password, store session with DataStore
- Listings CRUD with image picker and Coil display
- Search by title/author/ISBN with case-insensitive query and category/subcategory filters
- Cart: add/remove items, checkout with fake payment form
- Orders: store locally
- Messaging: one conversation per listing, plain text, local storage, optional notifications
- Admin: seed admin account, disable/remove users/listings, log actions
- User roles: normal user can buy & sell, admin special role
- Validation: forms and empty states, error messages

6. Project Structure
app/
 ├─ data/
 │   ├─ local/dao/
 │   ├─ local/entity/
 │   ├─ local/db/
 │   └─ repository/
 ├─ domain/model/
 ├─ ui/
 │   ├─ auth/
 │   ├─ home/
 │   ├─ search/
 │   ├─ listing/
 │   ├─ details/
 │   ├─ cart/
 │   ├─ inbox/
 │   ├─ admin/
 │   └─ account/
 ├─ navigation/
 ├─ components/
 ├─ theme/
 └─ util/

7. Build Order
- Phase 1: Project setup, Compose, Navigation, Room, DataStore
- Phase 2: Database entities, DAOs, seed admin & sample listings
- Phase 3: Auth flow (register/login/session)
- Phase 4: Listings CRUD
- Phase 5: Home & Search
- Phase 6: Cart & Checkout
- Phase 7: Messaging
- Phase 8: Admin
- Phase 9: Polish & validation

8. MVP
- Registration & login
- Buyer home feed
- Seller listing CRUD
- Search & filter
- Book detail page
- Cart & fake checkout
- Admin review page
- Messaging optional

9. Important Implementation Notes
- Use SHA-256 or similar with salt for password hashing
- Use Coil to display images from URI
- Simulate payments only; no external payment gateway
- Seed one admin user on first launch (username: admin, password: hashed version of admin123)
- Keep messaging simple: one conversation per listing, plain text
- Validate forms and handle empty states

  This is my prompt
  This is the answer
  You’re asking for a full MVVM Android app scaffold from this SRA, so I’m going to implement a compile-ready foundation in your current project: data layer (Room + DAOs + repositories), session/DataStore, ViewModels, core Compose screens, and navigation wiring first.

Created 8 todos

I’ll update build configuration next so Room, Navigation, Coil, and DataStore are available and SDK levels match your spec, which is required before adding code files.

I’ve finished the dependency setup; next I’m generating the data layer and utility classes (entities, DAOs, database, repositories, hashing/session helpers), then I’ll wire UI screens and navigation on top.

I’m now adding the Room schema in one pass (all entities plus relation/projection models), so the rest of the app can compile against a stable data contract.

The schema is in place; I’ll add DAOs now for auth, listings/search, cart/checkout, messaging, and admin logging so repositories can stay clean.

Data entities and DAOs are now generated. I’m moving to the database/repository/util layer next, including admin seeding, checkout transaction logic, and session persistence with DataStore.

Repository layer is now done, including checkout/order storage and admin action logging. Next I’ll add the app container and ViewModels, then hook the screens to those state flows.

I’m starting the UI layer now: I’ll generate all required Compose screens (auth, home, listings, search, details, cart/checkout, inbox/chat, admin, account) and then wire them into a single navigation graph with splash/session routing and bottom nav.

Core screens are in place for auth/home/search/listings. I’m now adding book detail, cart/checkout, inbox/chat, admin/account, and then the nav graph that ties every route together.

I’m doing a compile pass next and fixing any integration issues; first I need one lifecycle-compose dependency so the ViewModel state collection APIs resolve correctly.

The app scaffold is now wired end-to-end in code. I’ll do a quick repository-wide file check, then summarize exactly what’s implemented and what remains blocked by local JDK setup.

Implemented a full MVVM scaffold for your Android bookstore app in this project, including Room schema, DAOs, repositories, ViewModels, Compose screens, and Navigation Compose wiring.

What was built

Build setup and SDK targets
Updated app config to min SDK 24 / target SDK 34 in build.gradle.kts
Added dependencies for Room, Navigation Compose, DataStore, Coil, lifecycle compose/viewmodel in:
build.gradle.kts
libs.versions.toml
Room database layer (SQLite via Room)
Entities and relationships (Users, Listings, CartItems, Orders, OrderItems, Messages, Conversations, AdminLogs, GenrePreferences):
Entities.kt
Projections for joined UI data:
Relations.kt
DAOs for auth/listings/cart/orders/messaging/admin logs/genre prefs:
UserDao.kt
ListingDao.kt
CartDao.kt
OrderDao.kt
MessageDao.kt
AdminLogDao.kt
GenrePreferenceDao.kt
Database class:
AppDatabase.kt
Repositories + utilities (MVVM data layer)
Auth with password hashing + admin seed:
AuthRepository.kt
Listings/search:
ListingRepository.kt
Cart + checkout simulation + local order persistence:
CartRepository.kt
Messaging:
MessageRepository.kt
Admin actions and logging:
AdminRepository.kt
Session persistence with DataStore:
SessionDataStore.kt
SessionRepository.kt
Password hashing utility (salted SHA-256):
PasswordUtils.kt
App container (manual DI):
AppContainer.kt
ViewModels and UI state with StateFlow
Auth, Home, Search, Listing, Cart, Inbox, Admin, Account ViewModels + factory:
AppViewModels.kt
Compose screens generated per your module list
Auth:
AuthScreens.kt
Home:
HomeScreen.kt
Search:
SearchScreen.kt
Listings:
ListingScreens.kt
Book details:
BookDetailScreen.kt
Cart/checkout:
CartScreens.kt
Inbox/chat:
InboxScreens.kt
Admin:
AdminDashboardScreen.kt
Account:
AccountScreen.kt
Navigation graph and app entry
Routes and bottom-nav model:
Routes.kt
Full nav host with splash session check, auth flow, app screens:
AppNavGraph.kt
Main activity now launches the full app graph:
MainActivity.kt
Important validation note

I could not run a full Gradle compile because your machine environment is missing Java setup:
Build command failed with JAVA_HOME not set.
Once Java is configured, run:
gradlew.bat :app:assembleDebug
Sync in Android Studio
Next steps I recommend

Set JAVA_HOME to your JDK (Android Studio bundled JDK is fine), then run a build.
I can do a second pass to fix any compile-time issues that appear after your first sync/build.
I can also add sample listing seeding and stronger form validators (card expiry/CVV/email formats) if you want to move this from scaffold to polished MVP quickly.
GPT-5.3-Codex • 1x
