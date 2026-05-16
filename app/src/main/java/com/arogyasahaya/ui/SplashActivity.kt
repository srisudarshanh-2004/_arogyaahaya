package com.arogyasahaya.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import com.arogyasahaya.ui.auth.LoginActivity
import com.arogyasahaya.ui.onboarding.OnboardingActivity
import com.arogyasahaya.viewmodel.AuthViewModel
import com.arogyasahaya.viewmodel.ProfileViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Entry point after splash screen.
 * Handles theme and language initialization before navigating.
 */
@SuppressLint("CustomSplashScreen")
@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {

    private val authViewModel: AuthViewModel by viewModels()
    private val profileViewModel: ProfileViewModel by viewModels()
    private var isReady = false
    private var isNavigating = false

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        splashScreen.setKeepOnScreenCondition { !isReady }
        super.onCreate(savedInstanceState)

        // Observe profile to get user preferences
        profileViewModel.userProfile.observe(this) { profile ->
            if (isNavigating) return@observe
            
            val targetDarkMode = profile?.isDarkMode ?: false
            val targetLangCode = profile?.languageCode ?: "en"

            // Apply Preferences
            val themeChanged = applyThemePreference(targetDarkMode)
            val langChanged  = applyLocalePreference(targetLangCode)
            
            // If theme or language changed, the activity might be recreating.
            // We only proceed to navigation if NO changes were needed.
            if (!themeChanged && !langChanged) {
                checkNavigation()
            }
        }

        // Fallback: If observer doesn't fire (e.g. empty DB), force check navigation
        lifecycleScope.launch {
            delay(1500)
            if (!isNavigating) {
                checkNavigation()
            }
        }
    }

    private fun applyThemePreference(isDarkMode: Boolean): Boolean {
        val targetMode = if (isDarkMode) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
        if (AppCompatDelegate.getDefaultNightMode() != targetMode) {
            AppCompatDelegate.setDefaultNightMode(targetMode)
            return true
        }
        return false
    }

    private fun applyLocalePreference(langCode: String): Boolean {
        val currentLocales = AppCompatDelegate.getApplicationLocales()
        if (currentLocales.toLanguageTags() != langCode) {
            AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(langCode))
            return true
        }
        return false
    }

    private fun checkNavigation() {
        if (isNavigating) return
        isNavigating = true

        authViewModel.checkLoginStatus { isLoggedIn ->
            if (!isLoggedIn) {
                isReady = true
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            } else {
                profileViewModel.checkOnboarding { isOnboardingDone ->
                    isReady = true
                    val destination = if (isOnboardingDone) {
                        Intent(this, MainActivity::class.java)
                    } else {
                        Intent(this, OnboardingActivity::class.java)
                    }
                    startActivity(destination)
                    finish()
                }
            }
        }
    }
}
