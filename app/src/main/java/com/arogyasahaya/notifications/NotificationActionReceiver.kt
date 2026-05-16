package com.arogyasahaya.notifications

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.arogyasahaya.data.database.ArogyaDatabase
import com.arogyasahaya.data.entity.DoseStatus
import com.arogyasahaya.data.entity.MedicineDoseLogEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Handles when the user taps "Taken" or "Skip" directly from the notification.
 * Saves the result to Room DB without opening the app.
 */
class NotificationActionReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val medicineId   = intent.getIntExtra(AlarmReceiver.EXTRA_MEDICINE_ID, -1)
        val medicineName = intent.getStringExtra(AlarmReceiver.EXTRA_MEDICINE_NAME) ?: return
        val slot         = intent.getStringExtra(AlarmReceiver.EXTRA_SLOT) ?: return
        val action       = intent.action ?: return

        if (medicineId == -1) return

        val status = when (action) {
            ACTION_TAKEN  -> DoseStatus.TAKEN
            ACTION_SKIPPED -> DoseStatus.SKIPPED
            else -> return
        }

        // Save to Room DB on a background thread
        CoroutineScope(Dispatchers.IO).launch {
            val db = ArogyaDatabase.getDatabase(context)
            db.medicineDoseLogDao().insertLog(
                MedicineDoseLogEntity(
                    medicineId   = medicineId,
                    medicineName = medicineName,
                    slot         = slot,
                    scheduledTime = System.currentTimeMillis(),
                    status       = status
                )
            )
        }

        // Dismiss the notification
        val notifManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notifManager.cancel(medicineId)
    }

    companion object {
        const val ACTION_TAKEN   = "com.arogyasahaya.ACTION_TAKEN"
        const val ACTION_SKIPPED = "com.arogyasahaya.ACTION_SKIPPED"
    }
}
