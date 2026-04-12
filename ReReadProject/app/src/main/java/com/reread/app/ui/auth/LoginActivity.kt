package com.reread.app.ui.auth

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.reread.app.R
import com.reread.app.viewmodel.AuthViewModel
import com.reread.app.ui.home.HomeActivity

class LoginActivity : AppCompatActivity() {

    private val viewModel: AuthViewModel by viewModels()

    private lateinit var etUsernameOrEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var tvGoToRegister: TextView
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        etUsernameOrEmail = findViewById(R.id.et_username_or_email)
        etPassword        = findViewById(R.id.et_password)
        btnLogin          = findViewById(R.id.btn_login)
        tvGoToRegister    = findViewById(R.id.tv_go_to_register)
        progressBar       = findViewById(R.id.progress_bar)

        btnLogin.setOnClickListener {
            viewModel.login(
                etUsernameOrEmail.text.toString(),
                etPassword.text.toString()
            )
        }

        tvGoToRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        observeState()
    }

    private fun observeState() {
        viewModel.loginState.observe(this) { state ->
            when (state) {
                is AuthViewModel.LoginState.Idle -> setLoading(false)
                is AuthViewModel.LoginState.Loading -> setLoading(true)
                is AuthViewModel.LoginState.Success -> {
                    setLoading(false)
                    Toast.makeText(this, "Welcome back, ${state.user.username}!", Toast.LENGTH_SHORT).show()
                    navigateByRole(state.user.role)
                    viewModel.resetLoginState()
                }
                is AuthViewModel.LoginState.Error -> {
                    setLoading(false)
                    Toast.makeText(this, state.message, Toast.LENGTH_LONG).show()
                    viewModel.resetLoginState()
                }
            }
        }
    }

    private fun setLoading(loading: Boolean) {
        progressBar.visibility = if (loading) View.VISIBLE else View.GONE
        btnLogin.isEnabled = !loading
    }

    private fun navigateByRole(role: String) {
        val intent = Intent(this, HomeActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}
