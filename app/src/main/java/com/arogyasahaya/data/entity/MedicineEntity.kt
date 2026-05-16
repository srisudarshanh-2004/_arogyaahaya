package com.arogyasahaya.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "medicines")
data class MedicineEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val dosage: String,          // e.g., "500mg", "5ml"
    val isMorning: Boolean = false,
    val isAfternoon: Boolean = false,
    val isNight: Boolean = false,
    val morningTime: String = "08:00",
    val afternoonTime: String = "13:00",
    val nightTime: String = "21:00",
    val isActive: Boolean = true,
    val startDate: Long = System.currentTimeMillis(),
    val notes: String = ""
)
