package com.arogyasahaya.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "vital_logs")
data class VitalLogEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val bpSystolic: Int = 0,        // mmHg
    val bpDiastolic: Int = 0,       // mmHg
    val heartRate: Int = 0,         // bpm
    val bloodGlucose: Float = 0f,   // mg/dL
    val loggedAt: Long = System.currentTimeMillis(),
    val notes: String = ""
)
