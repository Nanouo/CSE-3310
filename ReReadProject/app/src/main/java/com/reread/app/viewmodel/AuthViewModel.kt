package com.reread.app.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.reread.app.data.UserRepository
import com.reread.app.data.User
import com.reread.app.utils.SessionManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AuthViewModel(application: Application) : AndroidViewModel(application) {

    private val repo = UserRepository(application)
    private val session = SessionManager(application)

    // ─── Login state ─────────────────────────────────────────────────────────

    sealed class LoginState {
        object Idle : LoginState()
        object Loading : LoginState()
        data class Success(val user: User) : LoginState()
        data class Error(val message: String) : LoginState()
    }

    private val _loginState = MutableLiveData<LoginState>(LoginState.Idle)
    val loginState: LiveData<LoginState> = _loginState

    fun login(usernameOrEmail: String, password: String) {
        if (usernameOrEmail.isBlank()) {
            _loginState.value = LoginState.Error("Username or email is required")
            return
        }
        if (password.isBlank()) {
            _loginState.value = LoginState.Error("Password is required")
            return
        }

        _loginState.value = LoginState.Loading

        viewModelScope.launch(Dispatchers.IO) {
            val result = repo.login(usernameOrEmail.trim(), password)
            _loginState.postValue(
                when (result) {
                    is UserRepository.LoginResult.Success -> {
                        session.save(result.user)
                        LoginState.Success(result.user)
                    }
                    is UserRepository.LoginResult.InvalidCredentials ->
                        LoginState.Error("Invalid username/email or password")
                    is UserRepository.LoginResult.AccountDisabled ->
                        LoginState.Error("This account has been disabled")
                }
            )
        }
    }

    // ─── Register state ───────────────────────────────────────────────────────

    sealed class RegisterState {
        object Idle : RegisterState()
        object Loading : RegisterState()
        data class Success(val user: User) : RegisterState()
        data class Error(val message: String) : RegisterState()
    }

    private val _registerState = MutableLiveData<RegisterState>(RegisterState.Idle)
    val registerState: LiveData<RegisterState> = _registerState

    fun register(username: String, email: String, password: String, confirmPassword: String, role: String = "buyer") {
        val error = validateRegistration(username, email, password, confirmPassword)
        if (error != null) {
            _registerState.value = RegisterState.Error(error)
            return
        }

        _registerState.value = RegisterState.Loading

        viewModelScope.launch(Dispatchers.IO) {
            val result = repo.register(username.trim(), email.trim().lowercase(), password, role)
            _registerState.postValue(
                when (result) {
                    is UserRepository.RegisterResult.Success -> {
                        session.save(result.user)
                        RegisterState.Success(result.user)
                    }
                    is UserRepository.RegisterResult.UsernameTaken ->
                        RegisterState.Error("Username is already taken")
                    is UserRepository.RegisterResult.EmailTaken ->
                        RegisterState.Error("An account with this email already exists")
                    is UserRepository.RegisterResult.DatabaseError ->
                        RegisterState.Error("Registration failed. Please try again.")
                }
            )
        }
    }

    private fun validateRegistration(
        username: String, email: String,
        password: String, confirmPassword: String
    ): String? {
        if (username.isBlank()) return "Username is required"
        if (username.length < 3) return "Username must be at least 3 characters"
        if (!username.matches(Regex("[a-zA-Z0-9_]+"))) return "Username can only contain letters, numbers, and underscores"
        if (email.isBlank()) return "Email is required"
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) return "Enter a valid email address"
        if (password.isBlank()) return "Password is required"
        if (password.length < 6) return "Password must be at least 6 characters"
        if (password != confirmPassword) return "Passwords do not match"
        return null
    }

    fun resetLoginState() { _loginState.value = LoginState.Idle }
    fun resetRegisterState() { _registerState.value = RegisterState.Idle }
}
