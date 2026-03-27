package com.example.cse3310app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.cse3310app.data.local.entity.CartItemWithListing
import com.example.cse3310app.data.local.entity.ConversationEntity
import com.example.cse3310app.data.local.entity.ListingEntity
import com.example.cse3310app.data.local.entity.MessageEntity
import com.example.cse3310app.data.local.entity.UserEntity
import com.example.cse3310app.data.repository.AdminRepository
import com.example.cse3310app.data.repository.AuthRepository
import com.example.cse3310app.data.repository.CartRepository
import com.example.cse3310app.data.repository.ListingRepository
import com.example.cse3310app.data.repository.MessageRepository
import com.example.cse3310app.data.repository.SessionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class AuthUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val currentUserId: Long? = null,
    val isLoggedIn: Boolean = false
)

class AuthViewModel(
    private val authRepository: AuthRepository,
    private val sessionRepository: SessionRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            authRepository.seedAdminIfNeeded()
            sessionRepository.session.collect { (loggedIn, userId) ->
                _uiState.value = _uiState.value.copy(isLoggedIn = loggedIn, currentUserId = userId)
            }
        }
    }

    fun login(username: String, password: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            val result = authRepository.login(username.trim(), password)
            result.onSuccess {
                sessionRepository.login(it.userId)
            }.onFailure {
                _uiState.value = _uiState.value.copy(error = it.message)
            }
            _uiState.value = _uiState.value.copy(isLoading = false)
        }
    }

    fun register(username: String, email: String, password: String, genres: List<String>) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            if (username.isBlank() || email.isBlank() || password.length < 6) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Provide valid username, email, and password (6+ chars)."
                )
                return@launch
            }
            authRepository.register(username.trim(), email.trim(), password, genres)
                .onSuccess { sessionRepository.login(it) }
                .onFailure { _uiState.value = _uiState.value.copy(error = it.message) }
            _uiState.value = _uiState.value.copy(isLoading = false)
        }
    }

    fun logout() {
        viewModelScope.launch { sessionRepository.logout() }
    }
}

class HomeViewModel(private val listingRepository: ListingRepository) : ViewModel() {
    val listings = listingRepository.observeActiveListings()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
}

class SearchViewModel(private val listingRepository: ListingRepository) : ViewModel() {
    private val _query = MutableStateFlow("")
    private val _category = MutableStateFlow<String?>(null)
    private val _subcategory = MutableStateFlow<String?>(null)
    val query = _query.asStateFlow()
    val category = _category.asStateFlow()
    val subcategory = _subcategory.asStateFlow()

    val results = listingRepository.search("", null, null)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun updateQuery(query: String) { _query.value = query }
    fun updateCategory(category: String?) { _category.value = category }
    fun updateSubcategory(subcategory: String?) { _subcategory.value = subcategory }

    fun searchNow(): StateFlow<List<ListingEntity>> {
        return listingRepository.search(_query.value, _category.value, _subcategory.value)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
    }
}

class ListingViewModel(private val listingRepository: ListingRepository) : ViewModel() {
    fun myListings(userId: Long): StateFlow<List<ListingEntity>> =
        listingRepository.observeMyListings(userId)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun createListing(listing: ListingEntity) {
        viewModelScope.launch { listingRepository.createListing(listing) }
    }

    fun deactivateListing(listingId: Long) {
        viewModelScope.launch { listingRepository.deactivateListing(listingId) }
    }
}

class CartViewModel(private val cartRepository: CartRepository) : ViewModel() {
    private val _checkoutError = MutableStateFlow<String?>(null)
    private val _lastOrderId = MutableStateFlow<Long?>(null)
    val checkoutError = _checkoutError.asStateFlow()
    val lastOrderId = _lastOrderId.asStateFlow()

    fun cart(userId: Long): StateFlow<List<CartItemWithListing>> =
        cartRepository.observeCart(userId)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun addToCart(userId: Long, listingId: Long) {
        viewModelScope.launch { cartRepository.addToCart(userId, listingId) }
    }

    fun removeItem(cartItemId: Long) {
        viewModelScope.launch { cartRepository.removeItem(cartItemId) }
    }

    fun checkout(userId: Long, paymentMethod: String, cardNumber: String, items: List<CartItemWithListing>) {
        viewModelScope.launch {
            _checkoutError.value = null
            cartRepository.checkout(userId, paymentMethod, cardNumber, items)
                .onSuccess { _lastOrderId.value = it }
                .onFailure { _checkoutError.value = it.message ?: "Checkout failed" }
        }
    }
}

class InboxViewModel(private val messageRepository: MessageRepository) : ViewModel() {
    fun conversations(userId: Long): StateFlow<List<ConversationEntity>> =
        messageRepository.observeConversations(userId)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun messages(conversationId: Long): StateFlow<List<MessageEntity>> =
        messageRepository.observeMessages(conversationId)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun sendMessage(
        buyerId: Long,
        sellerId: Long,
        listingId: Long,
        senderId: Long,
        receiverId: Long,
        text: String
    ) {
        viewModelScope.launch {
            messageRepository.sendMessage(
                buyerId = buyerId,
                sellerId = sellerId,
                listingId = listingId,
                senderId = senderId,
                receiverId = receiverId,
                text = text
            )
        }
    }
}

class AdminViewModel(private val adminRepository: AdminRepository) : ViewModel() {
    val users = adminRepository.users
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
    val listings = adminRepository.listings
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
    val logs = adminRepository.logs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun disableUser(adminId: Long, userId: Long, disabled: Boolean) {
        viewModelScope.launch { adminRepository.disableUser(adminId, userId, disabled) }
    }

    fun removeListing(adminId: Long, listingId: Long) {
        viewModelScope.launch { adminRepository.removeListing(adminId, listingId) }
    }
}

class AccountViewModel(private val authRepository: AuthRepository) : ViewModel() {
    private val _user = MutableStateFlow<UserEntity?>(null)
    val user = _user.asStateFlow()

    fun load(userId: Long) {
        viewModelScope.launch {
            _user.value = authRepository.getUser(userId)
        }
    }
}

class AppViewModelFactory(
    private val authRepository: AuthRepository,
    private val listingRepository: ListingRepository,
    private val cartRepository: CartRepository,
    private val messageRepository: MessageRepository,
    private val adminRepository: AdminRepository,
    private val sessionRepository: SessionRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(AuthViewModel::class.java) ->
                AuthViewModel(authRepository, sessionRepository) as T
            modelClass.isAssignableFrom(HomeViewModel::class.java) ->
                HomeViewModel(listingRepository) as T
            modelClass.isAssignableFrom(SearchViewModel::class.java) ->
                SearchViewModel(listingRepository) as T
            modelClass.isAssignableFrom(ListingViewModel::class.java) ->
                ListingViewModel(listingRepository) as T
            modelClass.isAssignableFrom(CartViewModel::class.java) ->
                CartViewModel(cartRepository) as T
            modelClass.isAssignableFrom(InboxViewModel::class.java) ->
                InboxViewModel(messageRepository) as T
            modelClass.isAssignableFrom(AdminViewModel::class.java) ->
                AdminViewModel(adminRepository) as T
            modelClass.isAssignableFrom(AccountViewModel::class.java) ->
                AccountViewModel(authRepository) as T
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.simpleName}")
        }
    }
}
