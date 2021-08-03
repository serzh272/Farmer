package ru.serzh272.farmer.extensions

import androidx.core.graphics.ColorUtils
import com.yandex.mapkit.geometry.LinearRing
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.geometry.Polygon
import com.yandex.mapkit.map.*
import ru.serzh272.farmer.App

fun MapObjectCollection.addField(fieldPoints: List<Point>, fillColor:Int): PolygonMapObject {

    return this.addPolygon(Polygon(LinearRing(fieldPoints), listOf<LinearRing>())).apply {
        this.fillColor = ColorUtils.setAlphaComponent(fillColor, 100)
        this.strokeWidth = App.applicationContext().dpToPx(1)
        this.strokeColor = fillColor
    }
}

fun MapObjectCollection.removeAllPlacemarks(){
    this.traverse(object : MapObjectVisitor{
        override fun onPlacemarkVisited(p0: PlacemarkMapObject) {
            this@removeAllPlacemarks.remove(p0)
        }

        override fun onPolylineVisited(p0: PolylineMapObject) {

        }

        override fun onColoredPolylineVisited(p0: ColoredPolylineMapObject) {

        }

        override fun onPolygonVisited(p0: PolygonMapObject) {

        }

        override fun onCircleVisited(p0: CircleMapObject) {

        }

        override fun onCollectionVisitStart(p0: MapObjectCollection): Boolean {
            return true
        }

        override fun onCollectionVisitEnd(p0: MapObjectCollection) {

        }

        override fun onClusterizedCollectionVisitStart(p0: ClusterizedPlacemarkCollection): Boolean {
            return false
        }

        override fun onClusterizedCollectionVisitEnd(p0: ClusterizedPlacemarkCollection) {

        }

    })
}