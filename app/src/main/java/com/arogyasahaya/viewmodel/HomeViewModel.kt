package com.arogyasahaya.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.arogyasahaya.data.repository.MedicineRepository
import com.arogyasahaya.data.repository.UserProfileRepository
import com.arogyasahaya.data.repository.VitalRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val medicineRepository: MedicineRepository,
    private val profileRepository: UserProfileRepository,
    private val vitalRepository: VitalRepository
) : ViewModel() {

    // User profile — observed by HomeFragment
    val userProfile = profileRepository.getUserProfile().asLiveData()

    // All active medicines
    val medicines = medicineRepository.getAllActiveMedicines().asLiveData()

    // Today's vital log (to show a summary on home screen)
    val todayVitals = vitalRepository.getVitalLogsSince(
        System.currentTimeMillis() - TimeUnit.DAYS.toMillis(1)
    ).asLiveData()

    private val _adherenceRate = MutableLiveData<Float>()
    val adherenceRate: LiveData<Float> = _adherenceRate

    init {
        loadAdherenceRate()
    }

    // 7-day adherence rate (0-100%)
    fun loadAdherenceRate() {
        viewModelScope.launch {
            val sevenDaysAgo = System.currentTimeMillis() - TimeUnit.DAYS.toMillis(7)
            val rate = medicineRepository.getAdherenceRate(sevenDaysAgo)
            _adherenceRate.postValue(rate)
        }
    }
}
