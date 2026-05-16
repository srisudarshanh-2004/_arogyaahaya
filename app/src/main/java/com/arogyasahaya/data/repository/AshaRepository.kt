package com.arogyasahaya.data.repository

import com.arogyasahaya.data.dao.AshaEventDao
import com.arogyasahaya.data.entity.AshaEventEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AshaRepository @Inject constructor(
    private val ashaEventDao: AshaEventDao
) {

    fun getAllEvents(): Flow<List<AshaEventEntity>> =
        ashaEventDao.getAllEvents()

    fun getUpcomingEvents(from: Long = System.currentTimeMillis()): Flow<List<AshaEventEntity>> =
        ashaEventDao.getUpcomingEvents(from)

    fun getEventsForMonth(startOfMonth: Long, endOfMonth: Long): Flow<List<AshaEventEntity>> =
        ashaEventDao.getEventsForMonth(startOfMonth, endOfMonth)

    suspend fun insertEvent(event: AshaEventEntity): Long =
        ashaEventDao.insertEvent(event)

    suspend fun deleteEvent(event: AshaEventEntity) =
        ashaEventDao.deleteEvent(event)
}
