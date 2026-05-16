package com.arogyasahaya.data.database

import androidx.room.TypeConverter
import com.arogyasahaya.data.entity.AshaEventType
import com.arogyasahaya.data.entity.DoseStatus

class Converters {
    @TypeConverter
    fun fromAshaEventType(value: AshaEventType): String = value.name

    @TypeConverter
    fun toAshaEventType(value: String): AshaEventType = AshaEventType.valueOf(value)

    @TypeConverter
    fun fromDoseStatus(value: DoseStatus): String = value.name

    @TypeConverter
    fun toDoseStatus(value: String): DoseStatus = DoseStatus.valueOf(value)
}
