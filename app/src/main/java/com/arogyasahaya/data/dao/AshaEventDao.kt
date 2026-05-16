package com.arogyasahaya.data.dao

import androidx.room.*
import com.arogyasahaya.data.entity.AshaEventEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AshaEventDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvent(event: AshaEventEntity): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertEvents(events: List<AshaEventEntity>)

    @Delete
    suspend fun deleteEvent(event: AshaEventEntity)

    @Query("SELECT * FROM asha_events ORDER BY eventDate ASC")
    fun getAllEvents(): Flow<List<AshaEventEntity>>

    @Query("SELECT * FROM asha_events WHERE eventDate >= :from ORDER BY eventDate ASC")
    fun getUpcomingEvents(from: Long): Flow<List<AshaEventEntity>>

    @Query("SELECT * FROM asha_events WHERE eventDate >= :startOfMonth AND eventDate <= :endOfMonth")
    fun getEventsForMonth(startOfMonth: Long, endOfMonth: Long): Flow<List<AshaEventEntity>>

    @Query("SELECT COUNT(*) FROM asha_events")
    suspend fun getEventCount(): Int
}
