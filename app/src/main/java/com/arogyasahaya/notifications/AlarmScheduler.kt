package com.arogyasahaya.notifications

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.arogyasahaya.data.entity.MedicineEntity
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.Calendar
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AlarmScheduler @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    fun scheduleMedicineAlarms(medicine: MedicineEntity) {
        if (medicine.isMorning) {
            scheduleAlarm(medicine, "MORNING", medicine.morningTime)
        }
        if (medicine.isAfternoon) {
            scheduleAlarm(medicine, "AFTERNOON", medicine.afternoonTime)
        }
        if (medicine.isNight) {
            scheduleAlarm(medicine, "NIGHT", medicine.nightTime)
        }
    }

    fun cancelMedicineAlarms(medicine: MedicineEntity) {
        listOf("MORNING", "AFTERNOON", "NIGHT").forEach { slot ->
            val requestCode = getRequestCode(medicine.id, slot)
            val intent = createAlarmIntent(medicine, slot)
            val pendingIntent = PendingIntent.getBroadcast(
                context, requestCode, intent,
                PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
            )
            pendingIntent?.let { alarmManager.cancel(it) }
        }
    }

    private fun scheduleAlarm(medicine: MedicineEntity, slot: String, timeString: String) {
        val (hour, minute) = timeString.split(":").map { it.toInt() }
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            // If time has passed today, schedule for tomorrow
            if (timeInMillis <= System.currentTimeMillis()) {
                add(Calendar.DAY_OF_YEAR, 1)
            }
        }

        val requestCode = getRequestCode(medicine.id, slot)
        val intent = createAlarmIntent(medicine, slot)
        val pendingIntent = PendingIntent.getBroadcast(
            context, requestCode, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S &&
            alarmManager.canScheduleExactAlarms()
        ) {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                pendingIntent
            )
        } else {
            alarmManager.setAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                pendingIntent
            )
        }
    }

    private fun createAlarmIntent(medicine: MedicineEntity, slot: String): Intent {
        return Intent(context, AlarmReceiver::class.java).apply {
            putExtra(AlarmReceiver.EXTRA_MEDICINE_ID, medicine.id)
            putExtra(AlarmReceiver.EXTRA_MEDICINE_NAME, medicine.name)
            putExtra(AlarmReceiver.EXTRA_MEDICINE_DOSAGE, medicine.dosage)
            putExtra(AlarmReceiver.EXTRA_SLOT, slot)
        }
    }

    private fun getRequestCode(medicineId: Int, slot: String): Int {
        val slotCode = when (slot) {
            "MORNING" -> 0
            "AFTERNOON" -> 1
            "NIGHT" -> 2
            else -> 0
        }
        return medicineId * 10 + slotCode
    }
}
