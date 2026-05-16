package com.arogyasahaya.notifications

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.arogyasahaya.ArogyaApp
import com.arogyasahaya.R
import com.arogyasahaya.ui.MainActivity

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            Intent.ACTION_BOOT_COMPLETED -> {
                // Reschedule all alarms after device reboot
                // WorkManager handles this via RescheduleWorker
            }
            else -> {
                val medicineId = intent.getIntExtra(EXTRA_MEDICINE_ID, -1)
                val medicineName = intent.getStringExtra(EXTRA_MEDICINE_NAME) ?: "Medicine"
                val dosage = intent.getStringExtra(EXTRA_MEDICINE_DOSAGE) ?: ""
                val slot = intent.getStringExtra(EXTRA_SLOT) ?: "MORNING"

                if (medicineId != -1) {
                    showMedicineNotification(context, medicineId, medicineName, dosage, slot)

                    // Reschedule for the next day
                    rescheduleForNextDay(context, intent, medicineId, slot)
                }
            }
        }
    }

    private fun showMedicineNotification(
        context: Context,
        medicineId: Int,
        medicineName: String,
        dosage: String,
        slot: String
    ) {
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Open app intent
        val openAppIntent = PendingIntent.getActivity(
            context, medicineId,
            Intent(context, MainActivity::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Mark TAKEN action
        val takenIntent = Intent(context, NotificationActionReceiver::class.java).apply {
            action = NotificationActionReceiver.ACTION_TAKEN
            putExtra(EXTRA_MEDICINE_ID, medicineId)
            putExtra(EXTRA_MEDICINE_NAME, medicineName)
            putExtra(EXTRA_SLOT, slot)
        }
        val takenPendingIntent = PendingIntent.getBroadcast(
            context, medicineId * 100,
            takenIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Mark SKIPPED action
        val skippedIntent = Intent(context, NotificationActionReceiver::class.java).apply {
            action = NotificationActionReceiver.ACTION_SKIPPED
            putExtra(EXTRA_MEDICINE_ID, medicineId)
            putExtra(EXTRA_MEDICINE_NAME, medicineName)
            putExtra(EXTRA_SLOT, slot)
        }
        val skippedPendingIntent = PendingIntent.getBroadcast(
            context, medicineId * 100 + 1,
            skippedIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val slotEmoji = when (slot) {
            "MORNING" -> "🌅 Morning"
            "AFTERNOON" -> "☀️ Afternoon"
            "NIGHT" -> "🌙 Night"
            else -> slot
        }

        val notification = NotificationCompat.Builder(context, ArogyaApp.CHANNEL_MEDICINE)
            .setSmallIcon(R.drawable.ic_medicine)
            .setContentTitle("Time for your $slotEmoji medicine")
            .setContentText("$medicineName — $dosage")
            .setStyle(NotificationCompat.BigTextStyle()
                .bigText("It's time to take $medicineName ($dosage). Tap ✓ Taken when done."))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(openAppIntent)
            .addAction(R.drawable.ic_check, "✓ Taken", takenPendingIntent)
            .addAction(R.drawable.ic_skip, "Skip", skippedPendingIntent)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .build()

        manager.notify(medicineId, notification)
    }

    private fun rescheduleForNextDay(
        context: Context,
        originalIntent: Intent,
        medicineId: Int,
        slot: String
    ) {
        // The AlarmScheduler will be called from the ViewModel when medicine is updated
        // For now we rely on WorkManager's daily reschedule worker
    }

    companion object {
        const val EXTRA_MEDICINE_ID = "extra_medicine_id"
        const val EXTRA_MEDICINE_NAME = "extra_medicine_name"
        const val EXTRA_MEDICINE_DOSAGE = "extra_medicine_dosage"
        const val EXTRA_SLOT = "extra_slot"
    }
}
