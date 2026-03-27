package com.example.cse3310app.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.cse3310app.data.repository.AppContainer
import com.example.cse3310app.ui.account.AccountScreen
import com.example.cse3310app.ui.admin.AdminDashboardScreen
import com.example.cse3310app.ui.auth.GenrePreferenceScreen
import com.example.cse3310app.ui.auth.LoginScreen
import com.example.cse3310app.ui.auth.RegisterScreen
import com.example.cse3310app.ui.auth.SplashScreen
import com.example.cse3310app.ui.cart.CartScreen
import com.example.cse3310app.ui.cart.CheckoutScreen
import com.example.cse3310app.ui.details.BookDetailScreen
import com.example.cse3310app.ui.home.HomeScreen
import com.example.cse3310app.ui.inbox.ChatScreen
import com.example.cse3310app.ui.inbox.InboxScreen
import com.example.cse3310app.ui.listing.ListingFormScreen
import com.example.cse3310app.ui.listing.MyListingsScreen
import com.example.cse3310app.ui.search.SearchScreen
import com.example.cse3310app.ui.viewmodel.AccountViewModel
import com.example.cse3310app.ui.viewmodel.AdminViewModel
import com.example.cse3310app.ui.viewmodel.AppViewModelFactory
import com.example.cse3310app.ui.viewmodel.AuthViewModel
import com.example.cse3310app.ui.viewmodel.CartViewModel
import com.example.cse3310app.ui.viewmodel.HomeViewModel
import com.example.cse3310app.ui.viewmodel.InboxViewModel
import com.example.cse3310app.ui.viewmodel.ListingViewModel
import com.example.cse3310app.ui.viewmodel.SearchViewModel

@Composable
fun AppNavGraph(container: AppContainer) {
    val factory = remember(container) {
        AppViewModelFactory(
            authRepository = container.authRepository,
            listingRepository = container.listingRepository,
            cartRepository = container.cartRepository,
            messageRepository = container.messageRepository,
            adminRepository = container.adminRepository,
            sessionRepository = container.sessionRepository
        )
    }

    val authViewModel: AuthViewModel = viewModel(factory = factory)
    val homeViewModel: HomeViewModel = viewModel(factory = factory)
    val searchViewModel: SearchViewModel = viewModel(factory = factory)
    val listingViewModel: ListingViewModel = viewModel(factory = factory)
    val cartViewModel: CartViewModel = viewModel(factory = factory)
    val inboxViewModel: InboxViewModel = viewModel(factory = factory)
    val adminViewModel: AdminViewModel = viewModel(factory = factory)
    val accountViewModel: AccountViewModel = viewModel(factory = factory)

    val authState by authViewModel.uiState.collectAsStateWithLifecycle()

    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    var pendingRegistration by remember { mutableStateOf<Triple<String, String, String>?>(null) }

    val showBottomBar = authState.isLoggedIn && currentRoute in setOf(
        Routes.Home,
        Routes.Inbox,
        Routes.MyListings,
        Routes.Account
    )

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                BottomAppBar {
                    bottomNavItems.forEach { item ->
                        NavigationBarItem(
                            selected = currentRoute == item.route,
                            onClick = { navController.navigate(item.route) },
                            icon = { Text(item.label.take(1)) },
                            label = { Text(item.label) }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Routes.Splash,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Routes.Splash) {
                SplashScreen(authViewModel) { loggedIn ->
                    navController.navigate(if (loggedIn) Routes.Home else Routes.Login) {
                        popUpTo(Routes.Splash) { inclusive = true }
                    }
                }
            }
            composable(Routes.Login) {
                LoginScreen(
                    authViewModel = authViewModel,
                    onRegisterClick = { navController.navigate(Routes.Register) }
                )
            }
            composable(Routes.Register) {
                RegisterScreen { username, email, password ->
                    pendingRegistration = Triple(username, email, password)
                    navController.navigate(Routes.GenrePreference)
                }
            }
            composable(Routes.GenrePreference) {
                val payload = pendingRegistration
                if (payload == null) {
                    navController.popBackStack()
                } else {
                    GenrePreferenceScreen(
                        authViewModel = authViewModel,
                        username = payload.first,
                        email = payload.second,
                        password = payload.third,
                        onRegistered = {
                            navController.navigate(Routes.Home) {
                                popUpTo(Routes.Login) { inclusive = true }
                            }
                        }
                    )
                }
            }

            composable(Routes.Home) {
                HomeScreen(
                    homeViewModel = homeViewModel,
                    onOpenCategories = { navController.navigate(Routes.Search) },
                    onOpenSearch = { navController.navigate(Routes.Search) },
                    onOpenCart = { navController.navigate(Routes.Cart) },
                    onOpenListing = { navController.navigate(Routes.bookDetail(it)) }
                )
            }
            composable(Routes.Search) {
                SearchScreen(searchViewModel) { navController.navigate(Routes.bookDetail(it)) }
            }
            composable(Routes.MyListings) {
                val userId = authState.currentUserId ?: return@composable
                MyListingsScreen(
                    userId = userId,
                    listingViewModel = listingViewModel,
                    onCreate = { navController.navigate(Routes.ListingForm) }
                )
            }
            composable(Routes.ListingForm) {
                val userId = authState.currentUserId ?: return@composable
                ListingFormScreen(
                    userId = userId,
                    listingViewModel = listingViewModel,
                    onDone = { navController.popBackStack() }
                )
            }
            composable(
                route = Routes.BookDetail,
                arguments = listOf(navArgument("listingId") { type = NavType.LongType })
            ) { backStack ->
                val userId = authState.currentUserId ?: return@composable
                val listingId = backStack.arguments?.getLong("listingId") ?: return@composable
                BookDetailScreen(
                    listingId = listingId,
                    listingRepository = container.listingRepository,
                    onAddToCart = {
                        cartViewModel.addToCart(userId, it)
                        navController.navigate(Routes.Cart)
                    }
                )
            }
            composable(Routes.Cart) {
                val userId = authState.currentUserId ?: return@composable
                CartScreen(userId, cartViewModel) { navController.navigate(Routes.Checkout) }
            }
            composable(Routes.Checkout) {
                val userId = authState.currentUserId ?: return@composable
                CheckoutScreen(userId, cartViewModel) {
                    navController.navigate(Routes.Home) { popUpTo(Routes.Home) { inclusive = true } }
                }
            }
            composable(Routes.Inbox) {
                val userId = authState.currentUserId ?: return@composable
                InboxScreen(userId, inboxViewModel) { navController.navigate(Routes.chat(it)) }
            }
            composable(
                route = Routes.Chat,
                arguments = listOf(navArgument("conversationId") { type = NavType.LongType })
            ) { backStack ->
                val userId = authState.currentUserId ?: return@composable
                val conversationId = backStack.arguments?.getLong("conversationId") ?: return@composable
                ChatScreen(
                    conversationId = conversationId,
                    inboxViewModel = inboxViewModel,
                    fallbackBuyerId = userId,
                    fallbackSellerId = 1L,
                    fallbackListingId = 1L,
                    currentUserId = userId
                )
            }
            composable(Routes.Admin) {
                val userId = authState.currentUserId ?: return@composable
                AdminDashboardScreen(adminId = userId, adminViewModel = adminViewModel)
            }
            composable(Routes.Account) {
                val userId = authState.currentUserId ?: return@composable
                AccountScreen(
                    userId = userId,
                    accountViewModel = accountViewModel,
                    authViewModel = authViewModel
                )
            }
        }
    }
}
