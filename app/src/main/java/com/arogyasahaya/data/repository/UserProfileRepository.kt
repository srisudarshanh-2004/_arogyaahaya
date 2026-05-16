package com.arogyasahaya.data.repository

import com.arogyasahaya.data.dao.UserProfileDao
import com.arogyasahaya.data.entity.UserProfileEntity
import kotlinx.coroutines.flow.Flow
import java.security.MessageDigest
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserProfileRepository @Inject constructor(
    private val profileDao: UserProfileDao,
) {

    fun getUserProfile(): Flow<UserProfileEntity?> = profileDao.getUserProfile()

    suspend fun getUserProfileOnce(): UserProfileEntity? = profileDao.getUserProfileOnce()

    suspend fun saveProfile(profile: UserProfileEntity) =
        profileDao.insertOrUpdateProfile(profile)

    suspend fun isOnboardingComplete(): Boolean = profileDao.isOnboardingComplete() ?: false

    /**
     * Update basic profile details.
     */
    suspend fun updateProfileDetails(
        name: String,
        age: Int,
        gender: String,
        bloodGroup: String,
        chronicConditions: String
    ) {
        val current = profileDao.getUserProfileOnce() ?: UserProfileEntity()
        val updated = current.copy(
            name = name,
            age = age,
            gender = gender,
            bloodGroup = bloodGroup,
            chronicConditions = chronicConditions
        )
        profileDao.insertOrUpdateProfile(updated)
    }

    /**
     * Update emergency contact information.
     */
    suspend fun updateEmergencyContact(name: String, phone: String) {
        val current = profileDao.getUserProfileOnce() ?: UserProfileEntity()
        val updated = current.copy(
            emergencyContactName = name,
            emergencyContactPhone = phone
        )
        profileDao.insertOrUpdateProfile(updated)
    }

    suspend fun markOnboardingComplete() = profileDao.markOnboardingComplete()

    suspend fun updateDarkMode(isDarkMode: Boolean) {
        val current = profileDao.getUserProfileOnce()
        if (current != null) {
            profileDao.insertOrUpdateProfile(current.copy(isDarkMode = isDarkMode))
        }
    }

    suspend fun updateLanguage(languageCode: String) {
        val current = profileDao.getUserProfileOnce()
        if (current != null) {
            profileDao.insertOrUpdateProfile(current.copy(languageCode = languageCode))
        }
    }

    // ── LOGIN / REGISTER ──────────────────────────────────────────────────────

    /**
     * Register a new user with email + password.
     */
    suspend fun registerWithEmail(
        email: String,
        password: String,
        name: String = "",
        mobile: String = ""
    ): RegisterResult {
        if (email.isBlank() || password.isBlank())
            return RegisterResult.Error("Email and password cannot be empty")
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches())
            return RegisterResult.Error("Invalid email address")
        if (password.length < 6)
            return RegisterResult.Error("Password must be at least 6 characters")

        val existing = profileDao.findByEmail(email)
        if (existing != null) return RegisterResult.Error("Email is already registered")

        val current = profileDao.getUserProfileOnce()
        val updated = (current ?: UserProfileEntity()).copy(
            email       = email.trim().lowercase(),
            mobile      = mobile.trim(),
            passwordHash = sha256(password),
            name        = name.ifBlank { current?.name ?: "" },
            loginType   = "EMAIL",
            isLoggedIn  = true,
            isOnboardingComplete = current?.isOnboardingComplete ?: false
        )
        profileDao.insertOrUpdateProfile(updated)
        return RegisterResult.Success
    }

    /**
     * Register / login with mobile number.
     */
    suspend fun registerWithMobile(mobile: String, name: String = ""): RegisterResult {
        if (mobile.isBlank() || (mobile.length < 10))
            return RegisterResult.Error("Please enter a valid 10-digit mobile number")

        val current = profileDao.getUserProfileOnce()
        val updated = (current ?: UserProfileEntity()).copy(
            mobile      = mobile.trim(),
            name        = name.ifBlank { current?.name ?: "" },
            loginType   = "MOBILE",
            isLoggedIn  = true,
            isOnboardingComplete = current?.isOnboardingComplete ?: false
        )
        profileDao.insertOrUpdateProfile(updated)
        return RegisterResult.Success
    }

    /**
     * Login with email + password.
     */
    suspend fun loginWithEmail(email: String, password: String): LoginResult {
        if (email.isBlank() || password.isBlank())
            return LoginResult.Error("Email and password cannot be empty")

        val profile = profileDao.findByEmail(email.trim().lowercase())
            ?: return LoginResult.Error("No account found with this email")

        if (profile.passwordHash != sha256(password))
            return LoginResult.Error("Incorrect password")

        profileDao.setLoggedIn(loggedIn = true)
        return LoginResult.Success(profile)
    }

    /**
     * Login with mobile.
     */
    suspend fun loginWithMobile(mobile: String): LoginResult {
        if (mobile.isBlank() || (mobile.length < 10))
            return LoginResult.Error("Please enter a valid mobile number")

        val profile = profileDao.findByMobile(mobile.trim())
            ?: return LoginResult.Error("No account found with this mobile number")

        profileDao.setLoggedIn(loggedIn = true)
        return LoginResult.Success(profile)
    }

    suspend fun logout() {
        profileDao.setLoggedIn(false)
    }

    suspend fun isLoggedIn(): Boolean = profileDao.isLoggedIn() ?: false

    private fun sha256(input: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(input.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }

    sealed class RegisterResult {
        object Success : RegisterResult()
        data class Error(val message: String) : RegisterResult()
    }

    sealed class LoginResult {
        data class Success(val profile: UserProfileEntity? = null) : LoginResult()
        data class Error(val message: String) : LoginResult()
    }
}
