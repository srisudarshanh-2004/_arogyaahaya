package com.arogyasahaya.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserProfileEntity(
    @PrimaryKey
    val id: Int = 1,   // Single user — always id=1
    val name: String = "",
    val age: Int = 0,
    val gender: String = "",
    val chronicConditions: String = "",
    val emergencyContactName: String = "",
    val emergencyContactPhone: String = "",
    val bloodGroup: String = "",
    val isOnboardingComplete: Boolean = false,
    // ── LOGIN FIELDS ──────────────────────────────
    val email: String = "",
    val mobile: String = "",
    val passwordHash: String = "",   // SHA-256 hash of password
    val isLoggedIn: Boolean = false,
    val loginType: String = "NONE",  // "EMAIL", "MOBILE", "NONE"
    
    // ── SETTINGS ─────────────────────────────────
    val isDarkMode: Boolean = false, // false = Light, true = Dark
    val languageCode: String = "en"  // "en", "hi", "kn"
)
