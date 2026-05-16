package com.arogyasahaya.data.dao

import androidx.room.*
import com.arogyasahaya.data.entity.VitalLogEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface VitalLogDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVitalLog(log: VitalLogEntity): Long

    @Update
    suspend fun updateVitalLog(log: VitalLogEntity)

    @Delete
    suspend fun deleteVitalLog(log: VitalLogEntity)

    @Query("SELECT * FROM vital_logs ORDER BY loggedAt DESC")
    fun getAllVitalLogs(): Flow<List<VitalLogEntity>>

    @Query("SELECT * FROM vital_logs WHERE loggedAt >= :from ORDER BY loggedAt ASC")
    fun getVitalLogsSince(from: Long): Flow<List<VitalLogEntity>>

    @Query("SELECT * FROM vital_logs ORDER BY loggedAt DESC LIMIT 7")
    fun getLast7DaysLogs(): Flow<List<VitalLogEntity>>

    @Query("SELECT * FROM vital_logs WHERE loggedAt >= :startOfDay AND loggedAt <= :endOfDay LIMIT 1")
    suspend fun getLogForDate(startOfDay: Long, endOfDay: Long): VitalLogEntity?
}
