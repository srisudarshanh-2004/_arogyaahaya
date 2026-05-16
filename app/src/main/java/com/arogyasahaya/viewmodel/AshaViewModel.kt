package com.arogyasahaya.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.arogyasahaya.data.repository.AshaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AshaViewModel @Inject constructor(
    private val ashaRepository: AshaRepository
) : ViewModel() {

    // All upcoming ASHA events (simulated data seeded in DB on first launch)
    val upcomingEvents = ashaRepository.getUpcomingEvents().asLiveData()

    // All events (for calendar display)
    val allEvents = ashaRepository.getAllEvents().asLiveData()
}
