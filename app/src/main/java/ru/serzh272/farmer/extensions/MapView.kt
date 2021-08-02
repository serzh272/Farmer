package ru.serzh272.farmer.extensions

import android.graphics.PointF
import com.yandex.mapkit.ScreenPoint
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.mapview.MapView

fun MapView.listPointFToListMapPoint(points: List<PointF>): List<Point?>{
    return points.map {
        this.pointFToMapPoint(it)
    }
}

fun MapView.pointFToMapPoint(point: PointF): Point?{

    return this.mapWindow.screenToWorld(ScreenPoint(point.x, point.y))
}
