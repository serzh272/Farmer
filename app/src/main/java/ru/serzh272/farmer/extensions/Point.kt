package ru.serzh272.farmer.extensions

import android.graphics.PointF
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.mapview.MapView
import ru.serzh272.farmer.PointD

fun Point.offset(latitudeOffset:Double = 0.0, longitudeOffset:Double = 0.0):Point{
    return Point(this.latitude + latitudeOffset, this.longitude + longitudeOffset)
}

fun Point.toPointD():PointD{
    return PointD(this.latitude, this.longitude)
}

fun PointD.toMapPoint():Point{
    return Point(this.x, this.y)
}

fun Point.toScreenPoint(mpV: MapView):PointF?{
    val scrP = mpV.mapWindow.worldToScreen(this)
    return if (scrP != null) {
        PointF(scrP.x, scrP.y)
    }else{
        null
    }
}