package com.arogyasahaya

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class ArogyaApp : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override fun getWorkManagerConfiguration(): Configuration =
        Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    override fun onCreate() {
        super.onCreate()
        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = getSystemService(NotificationManager::class.java)

            // Medicine reminder channel
            val medicineChannel = NotificationChannel(
                CHANNEL_MEDICINE,
                "Medicine Reminders",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Reminds you to take your medicines on time"
                enableVibration(true)
                setShowBadge(true)
            }

            // ASHA camp channel
            val ashaChannel = NotificationChannel(
                CHANNEL_ASHA,
                "ASHA Health Camp Alerts",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Upcoming health camp and ASHA worker visit alerts"
            }

            // SOS channel
            val sosChannel = NotificationChannel(
                CHANNEL_SOS,
                "Emergency SOS",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Emergency SOS notifications"
                enableVibration(true)
            }

            manager.createNotificationChannels(
                listOf(medicineChannel, ashaChannel, sosChannel)
            )
        }
    }

    companion object {
        const val CHANNEL_MEDICINE = "channel_medicine"
        const val CHANNEL_ASHA = "channel_asha"
        const val CHANNEL_SOS = "channel_sos"
    }
}
