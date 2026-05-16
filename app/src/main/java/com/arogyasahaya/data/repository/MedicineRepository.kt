package com.arogyasahaya.data.repository

import com.arogyasahaya.data.dao.MedicineDao
import com.arogyasahaya.data.dao.MedicineDoseLogDao
import com.arogyasahaya.data.entity.MedicineDoseLogEntity
import com.arogyasahaya.data.entity.MedicineEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MedicineRepository @Inject constructor(
    private val medicineDao: MedicineDao,
    private val doseLogDao: MedicineDoseLogDao
) {

    // ── Medicine CRUD ──────────────────────────────────────────────────────────

    fun getAllActiveMedicines(): Flow<List<MedicineEntity>> =
        medicineDao.getAllActiveMedicines()

    fun getAllMedicines(): Flow<List<MedicineEntity>> =
        medicineDao.getAllMedicines()

    suspend fun getMedicineById(id: Int): MedicineEntity? =
        medicineDao.getMedicineById(id)

    suspend fun insertMedicine(medicine: MedicineEntity): Long =
        medicineDao.insertMedicine(medicine)

    suspend fun updateMedicine(medicine: MedicineEntity) =
        medicineDao.updateMedicine(medicine)

    suspend fun deleteMedicine(medicine: MedicineEntity) =
        medicineDao.deleteMedicine(medicine)

    suspend fun deleteMedicineById(id: Int) =
        medicineDao.deleteMedicineById(id)

    // ── Dose Logs ──────────────────────────────────────────────────────────────

    suspend fun insertDoseLog(log: MedicineDoseLogEntity): Long =
        doseLogDao.insertLog(log)

    fun getAllDoseLogs(): Flow<List<MedicineDoseLogEntity>> =
        doseLogDao.getAllLogs()

    fun getLogsForDay(startOfDay: Long, endOfDay: Long): Flow<List<MedicineDoseLogEntity>> =
        doseLogDao.getLogsForDay(startOfDay, endOfDay)

    fun getRecentLogs(from: Long): Flow<List<MedicineDoseLogEntity>> =
        doseLogDao.getRecentLogs(from)

    suspend fun getAdherenceRate(from: Long): Float {
        val total = doseLogDao.countTotalDoses(from)
        if (total == 0) return 0f
        val taken = doseLogDao.countTakenDoses(from)
        return taken.toFloat() / total.toFloat() * 100f
    }
}
