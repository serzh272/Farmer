package ru.serzh272.farmer

import android.os.Parcel
import android.os.Parcelable

class PointD(var x:Double = 0.0, var y: Double = 0.0):Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readDouble(),
        parcel.readDouble()
    )

    override fun describeContents(): Int = 0

    override fun writeToParcel(dest: Parcel?, flags: Int) {
        dest?.writeDouble(x)
        dest?.writeDouble(y)
    }

    companion object CREATOR : Parcelable.Creator<PointD> {
        override fun createFromParcel(parcel: Parcel): PointD {
            val rez = PointD()
            rez.readFromParcel(parcel)
            return rez
        }

        override fun newArray(size: Int): Array<PointD?> {
            return Array(size) {
                PointD()
            }
        }
    }

    fun readFromParcel(p:Parcel){
        x = p.readDouble()
        y = p.readDouble()
    }

    override fun toString(): String {
        return "{$x;$y}"
    }

}