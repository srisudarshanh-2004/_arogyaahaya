package com.arogyasahaya.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "asha_events")
data class AshaEventEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val description: String = "",
    val eventDate: Long,           // epoch millis
    val location: String = "",
    val eventType: AshaEventType = AshaEventType.HEALTH_CAMP,
    val isSimulated: Boolean = true
)

enum class AshaEventType {
    HEALTH_CAMP,
    ASHA_VISIT,
    VACCINATION,
    WELLNESS_CHECK
}
