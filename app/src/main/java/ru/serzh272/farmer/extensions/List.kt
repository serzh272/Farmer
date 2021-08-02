package ru.serzh272.farmer.extensions

import com.yandex.mapkit.geometry.Point
import ru.serzh272.farmer.PointD
import kotlin.math.abs

fun List<PointD>.getArea():Double{
    var acc = 0.0
    if (this.size > 2){
        for (ind in this.indices){
            acc += this[ind].x * this[(ind+1)%this.size].y
            acc -= this[ind].y * this[(ind+1)%this.size].x
        }
    }
    return abs(acc) / 2
}

fun List<Point>.toListOfPointD():List<PointD>{
    return this.map {
        it.toPointD()
    }
}