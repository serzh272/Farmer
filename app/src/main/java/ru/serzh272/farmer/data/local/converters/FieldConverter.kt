package ru.serzh272.farmer.data.local.converters

import androidx.room.TypeConverter
import ru.serzh272.farmer.PointD
import ru.serzh272.farmer.extensions.format
import java.text.SimpleDateFormat
import java.util.*

class FieldConverter {
    //58.2365|45.2563;58.2365|45.2563
    @TypeConverter
    fun fromString(value: String): List<PointD> {
        return value.split(";").fold(mutableListOf()) { acc, s ->
            val coo = s.split("|")
            acc.add(PointD(coo.first().toDouble(), coo.last().toDouble()))
            acc
        }
    }

    @TypeConverter
    fun fromArrayList(list: List<PointD>): String {
        return list.joinToString(";") {
            "${it.x}|${it.y}"
        }
    }

    @TypeConverter
    fun dateFromString(strDate: String): Date? {
        val format = SimpleDateFormat("yyyy-MM-dd", Locale("ru"))
        return format.parse(strDate)
    }

    @TypeConverter
    fun fromDate(date: Date): String {
        return date.format("yyyy-MM-dd")
    }
}
