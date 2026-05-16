package com.arogyasahaya.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.arogyasahaya.data.entity.MedicineEntity
import com.arogyasahaya.data.repository.MedicineRepository
import com.arogyasahaya.notifications.AlarmScheduler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MedicineViewModel @Inject constructor(
    private val medicineRepository: MedicineRepository,
    private val alarmScheduler: AlarmScheduler
) : ViewModel() {

    // All active medicines — Fragment observes this LiveData
    val medicines = medicineRepository.getAllActiveMedicines().asLiveData()

    // All dose history
    val allDoseLogs = medicineRepository.getAllDoseLogs().asLiveData()

    /**
     * Add a new medicine and schedule its alarms.
     */
    fun addMedicine(medicine: MedicineEntity) {
        viewModelScope.launch {
            val newId = medicineRepository.insertMedicine(medicine)
            // Schedule alarms for the newly added medicine
            alarmScheduler.scheduleMedicineAlarms(medicine.copy(id = newId.toInt()))
        }
    }

    /**
     * Update an existing medicine, reschedule alarms.
     */
    fun updateMedicine(medicine: MedicineEntity) {
        viewModelScope.launch {
            medicineRepository.updateMedicine(medicine)
            alarmScheduler.cancelMedicineAlarms(medicine)
            alarmScheduler.scheduleMedicineAlarms(medicine)
        }
    }

    /**
     * Delete a medicine and cancel its alarms.
     */
    fun deleteMedicine(medicine: MedicineEntity) {
        viewModelScope.launch {
            alarmScheduler.cancelMedicineAlarms(medicine)
            medicineRepository.deleteMedicine(medicine)
        }
    }

    /**
     * Load a single medicine by ID (used to pre-fill edit form).
     */
    fun getMedicineById(id: Int, callback: (MedicineEntity?) -> Unit) {
        viewModelScope.launch {
            callback(medicineRepository.getMedicineById(id))
        }
    }
}
