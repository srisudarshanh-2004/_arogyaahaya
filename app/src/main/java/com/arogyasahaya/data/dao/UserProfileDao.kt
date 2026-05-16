package com.arogyasahaya.data.dao

import androidx.room.*
import com.arogyasahaya.data.entity.UserProfileEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserProfileDao {

    @Query("SELECT * FROM user_profile WHERE id = 1")
    fun getUserProfile(): Flow<UserProfileEntity?>

    @Query("SELECT * FROM user_profile WHERE id = 1")
    suspend fun getUserProfileOnce(): UserProfileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateProfile(profile: UserProfileEntity)

    @Query("UPDATE user_profile SET isOnboardingComplete = 1 WHERE id = 1")
    suspend fun markOnboardingComplete()

    @Query("SELECT isOnboardingComplete FROM user_profile WHERE id = 1")
    suspend fun isOnboardingComplete(): Boolean?

    // ── LOGIN QUERIES ─────────────────────────────────────────────────────────

    @Query("SELECT * FROM user_profile WHERE email = :email LIMIT 1")
    suspend fun findByEmail(email: String): UserProfileEntity?

    @Query("SELECT * FROM user_profile WHERE mobile = :mobile LIMIT 1")
    suspend fun findByMobile(mobile: String): UserProfileEntity?

    @Query("UPDATE user_profile SET isLoggedIn = :loggedIn WHERE id = 1")
    suspend fun setLoggedIn(loggedIn: Boolean)

    @Query("SELECT isLoggedIn FROM user_profile WHERE id = 1")
    suspend fun isLoggedIn(): Boolean?
}
