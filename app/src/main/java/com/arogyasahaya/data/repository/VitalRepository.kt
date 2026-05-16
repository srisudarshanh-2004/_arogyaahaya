package com.arogyasahaya.data.repository

import com.arogyasahaya.data.dao.VitalLogDao
import com.arogyasahaya.data.entity.VitalLogEntity
import kotlinx.coroutines.flow.Flow
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VitalRepository @Inject constructor(
    private val vitalLogDao: VitalLogDao
) {

    fun getAllVitalLogs(): Flow<List<VitalLogEntity>> =
        vitalLogDao.getAllVitalLogs()

    fun getLast7DaysLogs(): Flow<List<VitalLogEntity>> =
        vitalLogDao.getLast7DaysLogs()

    fun getVitalLogsSince(from: Long): Flow<List<VitalLogEntity>> =
        vitalLogDao.getVitalLogsSince(from)

    suspend fun insertVitalLog(log: VitalLogEntity): Long =
        vitalLogDao.insertVitalLog(log)

    suspend fun updateVitalLog(log: VitalLogEntity) =
        vitalLogDao.updateVitalLog(log)

    suspend fun deleteVitalLog(log: VitalLogEntity) =
        vitalLogDao.deleteVitalLog(log)

    suspend fun getTodayLog(): VitalLogEntity? {
        val startOfDay = getStartOfDay()
        val endOfDay = startOfDay + TimeUnit.DAYS.toMillis(1)
        return vitalLogDao.getLogForDate(startOfDay, endOfDay)
    }

    private fun getStartOfDay(): Long {
        val cal = java.util.Calendar.getInstance()
        cal.set(java.util.Calendar.HOUR_OF_DAY, 0)
        cal.set(java.util.Calendar.MINUTE, 0)
        cal.set(java.util.Calendar.SECOND, 0)
        cal.set(java.util.Calendar.MILLISECOND, 0)
        return cal.timeInMillis
    }
}
