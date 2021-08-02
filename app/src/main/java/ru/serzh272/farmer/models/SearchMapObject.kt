package ru.serzh272.farmer.models

import com.yandex.mapkit.geometry.Point

data class SearchMapObject(
    val name:String?,
    val description:String?,
    val location: Point
)