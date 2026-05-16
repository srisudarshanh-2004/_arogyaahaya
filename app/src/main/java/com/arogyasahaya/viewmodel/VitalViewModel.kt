package com.arogyasahaya.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.arogyasahaya.data.entity.VitalLogEntity
import com.arogyasahaya.data.repository.VitalRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class VitalViewModel @Inject constructor(
    private val vitalRepository: VitalRepository
) : ViewModel() {

    // Last 7 days of vitals for the chart
    val last7DaysLogs = vitalRepository.getLast7DaysLogs().asLiveData()

    // All vitals for the history list
    val allVitalLogs = vitalRepository.getAllVitalLogs().asLiveData()

    /**
     * Save today's vitals to Room DB.
     */
    fun saveVitalLog(
        bpSystolic: Int,
        bpDiastolic: Int,
        heartRate: Int,
        bloodGlucose: Float,
        notes: String = ""
    ) {
        viewModelScope.launch {
            vitalRepository.insertVitalLog(
                VitalLogEntity(
                    bpSystolic   = bpSystolic,
                    bpDiastolic  = bpDiastolic,
                    heartRate    = heartRate,
                    bloodGlucose = bloodGlucose,
                    notes        = notes
                )
            )
        }
    }

    /**
     * Check if user already logged vitals today (to avoid duplicates).
     */
    fun checkTodayLog(callback: (VitalLogEntity?) -> Unit) {
        viewModelScope.launch {
            callback(vitalRepository.getTodayLog())
        }
    }

    /**
     * Delete a vital log entry.
     */
    fun deleteVitalLog(log: VitalLogEntity) {
        viewModelScope.launch {
            vitalRepository.deleteVitalLog(log)
        }
    }
}
