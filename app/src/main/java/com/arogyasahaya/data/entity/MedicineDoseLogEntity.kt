package com.arogyasahaya.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "medicine_dose_logs",
    foreignKeys = [ForeignKey(
        entity = MedicineEntity::class,
        parentColumns = ["id"],
        childColumns = ["medicineId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("medicineId")]
)
data class MedicineDoseLogEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val medicineId: Int,
    val medicineName: String,
    val slot: String,            // "MORNING", "AFTERNOON", "NIGHT"
    val scheduledTime: Long,
    val loggedAt: Long = System.currentTimeMillis(),
    val status: DoseStatus       // TAKEN, SKIPPED, MISSED
)

enum class DoseStatus { TAKEN, SKIPPED, MISSED }
