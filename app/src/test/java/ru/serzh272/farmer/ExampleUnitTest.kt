package ru.serzh272.farmer

import org.junit.Test
import org.junit.Assert.*
import ru.serzh272.farmer.data.local.converters.FieldConverter
import ru.serzh272.farmer.extensions.getArea

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        var p = listOf(
            PointD(3.0, 4.0),
            PointD(5.0, 11.0),
            PointD(12.0, 8.0),
            PointD(9.0, 5.0),
            PointD(5.0, 6.0),
        )
        assertEquals(30.0, p.getArea(),0.01)
        p = listOf(
            PointD(55.97544201439153, 37.71984863281182),
            PointD(55.876808118310706, 37.66697692871001),
            PointD(55.8748378377763, 37.78258361816322))
        //39.945
        println(p.getArea())
    }

    @Test
    fun testConverters(){
        val c = FieldConverter()
        val v = "52.2365|120.2543;58.2365|45.2563;58.2325|45.2563"
        val rez = c.fromString(v)
        assertEquals(rez.first().x, 52.2365, 0.00001)
        val rez2 = c.fromArrayList(rez)
        assertEquals(v, rez2)
    }

    @Test
    fun test(){
        println(2.258f)
    }
}