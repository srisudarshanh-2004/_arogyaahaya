package com.arogyasahaya.notifications

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.*
import com.arogyasahaya.data.repository.MedicineRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first
import java.util.concurrent.TimeUnit

/**
 * WorkManager task that runs daily to reschedule all medicine alarms.
 * This ensures alarms survive device reboot and Doze mode.
 */
@HiltWorker
class RescheduleWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val medicineRepository: MedicineRepository,
    private val alarmScheduler: AlarmScheduler
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            // Get all active medicines and reschedule their alarms
            val medicines = medicineRepository.getAllActiveMedicines().first()
            medicines.forEach { medicine ->
                alarmScheduler.cancelMedicineAlarms(medicine)
                alarmScheduler.scheduleMedicineAlarms(medicine)
            }
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }

    companion object {
        const val WORK_NAME = "reschedule_medicine_alarms"

        /**
         * Schedule this worker to run once a day.
         * Call this when the app starts or when medicines are changed.
         */
        fun schedule(context: Context) {
            val request = PeriodicWorkRequestBuilder<RescheduleWorker>(
                repeatInterval = 1,
                repeatIntervalTimeUnit = TimeUnit.DAYS
            )
                .setConstraints(
                    Constraints.Builder()
                        .setRequiresBatteryNotLow(false)
                        .build()
                )
                .build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                request
            )
        }
    }
}
