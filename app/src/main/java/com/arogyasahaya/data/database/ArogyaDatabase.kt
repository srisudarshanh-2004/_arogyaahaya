package com.arogyasahaya.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.arogyasahaya.data.dao.*
import com.arogyasahaya.data.entity.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Calendar

@Database(
    entities = [
        MedicineEntity::class,
        MedicineDoseLogEntity::class,
        VitalLogEntity::class,
        UserProfileEntity::class,
        AshaEventEntity::class
    ],
    version = 8, // Incrementing to ensure clean state after robustness fixes
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class ArogyaDatabase : RoomDatabase() {

    abstract fun medicineDao(): MedicineDao
    abstract fun medicineDoseLogDao(): MedicineDoseLogDao
    abstract fun vitalLogDao(): VitalLogDao
    abstract fun userProfileDao(): UserProfileDao
    abstract fun ashaEventDao(): AshaEventDao

    companion object {
        @Volatile
        private var INSTANCE: ArogyaDatabase? = null

        fun getDatabase(context: Context): ArogyaDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ArogyaDatabase::class.java,
                    "arogya_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                
                // Seed data in a background scope if empty
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val dao = instance.ashaEventDao()
                        if (dao.getEventCount() == 0) {
                            seedAshaEvents(dao)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                instance
            }
        }

        private suspend fun seedAshaEvents(dao: AshaEventDao) {
            val events = buildList {
                val cal = Calendar.getInstance()
                repeat(12) { weekOffset ->
                    cal.timeInMillis = System.currentTimeMillis()
                    cal.add(Calendar.WEEK_OF_YEAR, weekOffset + 1)
                    when (weekOffset % 4) {
                        0 -> add(AshaEventEntity(title = "Health Camp", description = "Free checkup by PHC doctor.", eventDate = cal.timeInMillis, location = "Village Panchayat Hall", eventType = AshaEventType.HEALTH_CAMP))
                        1 -> add(AshaEventEntity(title = "ASHA Visit", description = "ASHA worker will visit for wellness check.", eventDate = cal.timeInMillis, location = "Door-to-door visit", eventType = AshaEventType.ASHA_VISIT))
                        2 -> add(AshaEventEntity(title = "Wellness Check", description = "Free BP monitoring at the centre.", eventDate = cal.timeInMillis, location = "Sub Health Centre", eventType = AshaEventType.WELLNESS_CHECK))
                        3 -> add(AshaEventEntity(title = "Vaccination", description = "Influenza vaccines available.", eventDate = cal.timeInMillis, location = "Primary Health Centre", eventType = AshaEventType.VACCINATION))
                    }
                }
            }
            dao.insertEvents(events)
        }
    }
}
