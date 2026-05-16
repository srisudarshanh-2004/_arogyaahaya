package com.arogyasahaya.data.dao

import androidx.room.*
import com.arogyasahaya.data.entity.MedicineDoseLogEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MedicineDoseLogDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: MedicineDoseLogEntity): Long

    @Query("SELECT * FROM medicine_dose_logs ORDER BY scheduledTime DESC")
    fun getAllLogs(): Flow<List<MedicineDoseLogEntity>>

    @Query("""
        SELECT * FROM medicine_dose_logs 
        WHERE scheduledTime >= :startOfDay AND scheduledTime <= :endOfDay
        ORDER BY scheduledTime ASC
    """)
    fun getLogsForDay(startOfDay: Long, endOfDay: Long): Flow<List<MedicineDoseLogEntity>>

    @Query("""
        SELECT * FROM medicine_dose_logs 
        WHERE scheduledTime >= :from
        ORDER BY scheduledTime DESC
        LIMIT 50
    """)
    fun getRecentLogs(from: Long): Flow<List<MedicineDoseLogEntity>>

    @Query("DELETE FROM medicine_dose_logs WHERE medicineId = :medicineId")
    suspend fun deleteLogsForMedicine(medicineId: Int)

    @Query("""
        SELECT COUNT(*) FROM medicine_dose_logs 
        WHERE status = 'TAKEN' AND scheduledTime >= :from
    """)
    suspend fun countTakenDoses(from: Long): Int

    @Query("""
        SELECT COUNT(*) FROM medicine_dose_logs 
        WHERE scheduledTime >= :from
    """)
    suspend fun countTotalDoses(from: Long): Int
}
