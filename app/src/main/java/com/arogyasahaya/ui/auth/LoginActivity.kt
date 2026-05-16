package com.arogyasahaya.ui.auth

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.arogyasahaya.R
import com.arogyasahaya.databinding.ActivityLoginBinding
import com.arogyasahaya.ui.MainActivity
import com.arogyasahaya.ui.onboarding.OnboardingActivity
import com.arogyasahaya.viewmodel.AuthViewModel
import com.arogyasahaya.viewmodel.ProfileViewModel
import dagger.hilt.android.AndroidEntryPoint

/**
 * Login screen — supports:
 *   • Email + Password login/register
 *   • Mobile number login/register
 * All data stored locally in Room DB (offline-first).
 */
@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val authViewModel: AuthViewModel   by viewModels()
    private val profileViewModel: ProfileViewModel by viewModels()

    // Toggle between Email and Mobile login modes
    private var isEmailMode = true
    // Toggle between Login and Register modes
    private var isLoginMode = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUI()
        observeAuthState()
    }

    private fun setupUI() {
        // Default: Email login mode
        showEmailMode()

        // ── Toggle Email / Mobile ────────────────────────────────────────────
        binding.btnSwitchToEmail.setOnClickListener {
            if (!isEmailMode) {
                isEmailMode = true
                showEmailMode()
            }
        }
        binding.btnSwitchToMobile.setOnClickListener {
            if (isEmailMode) {
                isEmailMode = false
                showMobileMode()
            }
        }

        // ── Toggle Login / Register ──────────────────────────────────────────
        binding.tvToggleLoginRegister.setOnClickListener {
            isLoginMode = !isLoginMode
            updateLoginRegisterUI()
        }

        // ── Main action button ────────────────────────────────────────────────
        binding.btnLogin.setOnClickListener {
            if (isEmailMode) {
                handleEmailAction()
            } else {
                handleMobileAction()
            }
        }

        // ── Skip login (for testing / offline use) ───────────────────────────
        binding.tvSkipLogin.setOnClickListener {
            goToNextScreen(isOnboardingDone = false)
        }
    }

    private fun showEmailMode() {
        binding.layoutEmailPassword.visibility = View.VISIBLE
        binding.layoutMobile.visibility        = View.GONE
        binding.btnSwitchToEmail.setBackgroundColor(ContextCompat.getColor(this, R.color.primary))
        binding.btnSwitchToEmail.setTextColor(ContextCompat.getColor(this, R.color.white))
        binding.btnSwitchToMobile.setBackgroundColor(ContextCompat.getColor(this, R.color.grey_light))
        binding.btnSwitchToMobile.setTextColor(ContextCompat.getColor(this, R.color.text_primary))
        updateLoginRegisterUI()
    }

    private fun showMobileMode() {
        binding.layoutEmailPassword.visibility = View.GONE
        binding.layoutMobile.visibility        = View.VISIBLE
        binding.btnSwitchToEmail.setBackgroundColor(ContextCompat.getColor(this, R.color.grey_light))
        binding.btnSwitchToEmail.setTextColor(ContextCompat.getColor(this, R.color.text_primary))
        binding.btnSwitchToMobile.setBackgroundColor(ContextCompat.getColor(this, R.color.primary))
        binding.btnSwitchToMobile.setTextColor(ContextCompat.getColor(this, R.color.white))
        
        // Mobile always shows "Send OTP" style button
        binding.btnLogin.text = getString(R.string.mobile_login_button)
        binding.layoutConfirmPassword.visibility = View.GONE
        binding.tvToggleLoginRegister.text = getString(R.string.new_user_text)
        binding.tvTitle.text = getString(R.string.welcome_back)
    }

    private fun updateLoginRegisterUI() {
        if (!isEmailMode) return
        if (isLoginMode) {
            binding.btnLogin.text                    = getString(R.string.login_button)
            binding.layoutConfirmPassword.visibility = View.GONE
            binding.tvToggleLoginRegister.text       = getString(R.string.new_user_text)
            binding.tvTitle.text                     = getString(R.string.welcome_back)
        } else {
            binding.btnLogin.text                    = getString(R.string.register_button)
            binding.layoutConfirmPassword.visibility = View.VISIBLE
            binding.tvToggleLoginRegister.text       = getString(R.string.existing_user_text)
            binding.tvTitle.text                     = getString(R.string.register_button)
        }
    }

    private fun handleEmailAction() {
        val email    = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString()

        if (email.isEmpty()) {
            binding.etEmail.error = getString(R.string.required)
            return
        }
        if (password.isEmpty()) {
            binding.etPassword.error = getString(R.string.required)
            return
        }

        if (isLoginMode) {
            authViewModel.loginWithEmail(email, password)
        } else {
            val confirm = binding.etConfirmPassword.text.toString()
            if (confirm.isEmpty()) {
                binding.etConfirmPassword.error = getString(R.string.required)
                return
            }
            authViewModel.registerWithEmail(email, password, confirm)
        }
    }

    private fun handleMobileAction() {
        val mobile = binding.etMobile.text.toString().trim()
        if (mobile.length < 10) {
            binding.etMobile.error = getString(R.string.required)
            return
        }
        // Mobile login: register if new, login if existing
        authViewModel.registerWithMobile(mobile)
    }

    private fun observeAuthState() {
        authViewModel.authState.observe(this) { state ->
            when (state) {
                is AuthViewModel.AuthState.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.btnLogin.isEnabled     = false
                }
                is AuthViewModel.AuthState.Success -> {
                    binding.progressBar.visibility = View.GONE
                    binding.btnLogin.isEnabled     = true
                    Toast.makeText(this, state.message, Toast.LENGTH_SHORT).show()
                    // Check if onboarding is needed
                    profileViewModel.checkOnboarding { isDone ->
                        authViewModel.resetState()
                        goToNextScreen(isOnboardingDone = isDone)
                    }
                }
                is AuthViewModel.AuthState.Error -> {
                    binding.progressBar.visibility = View.GONE
                    binding.btnLogin.isEnabled     = true
                    Toast.makeText(this, "⚠ ${state.message}", Toast.LENGTH_LONG).show()
                }
                else -> {
                    binding.progressBar.visibility = View.GONE
                    binding.btnLogin.isEnabled     = true
                }
            }
        }
    }

    private fun goToNextScreen(isOnboardingDone: Boolean) {
        val destination = if (isOnboardingDone) {
            Intent(this, MainActivity::class.java)
        } else {
            Intent(this, OnboardingActivity::class.java)
        }
        startActivity(destination)
        finish()
    }
}
