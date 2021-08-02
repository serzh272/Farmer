package ru.serzh272.farmer.data.local.entities

import androidx.annotation.ColorInt
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.serzh272.farmer.PointD
import ru.serzh272.farmer.extensions.getArea
import java.util.*
import kotlin.math.max
import kotlin.math.min

@Entity(tableName = "fields")
data class Field(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val name: String, //наименование поля
    val culture: String, //наименование посевной культуры
    @ColumnInfo(name="sowing_date")
    val sowingDate: Date, //дата посева
    @ColorInt
    val color:Int, //цвет, отображаемый на карте
    val points: List<PointD>, //координаты полигона, описывающего поле
    val zoom: Float //zoom камеры
){
    /**
     * Returns area of this field
     */
    fun getArea():Double{
        return points.getArea()
    }

    fun getCenter():PointD{
        var minX = points[0].x
        var maxX = minX
        var minY = points[0].y
        var maxY = minY
        points.forEach { p ->
            minX = min(p.x, minX)
            minY = min(p.y, minY)
            maxX = max(p.x, maxX)
            maxY = max(p.y, maxY)
        }
        return PointD((minX + maxX)/2, (minY + maxY)/2)
    }
}
