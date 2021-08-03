package ru.serzh272.farmer.extensions

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import androidx.core.content.ContextCompat
import com.yandex.runtime.image.ImageProvider

object Utils {
    fun fromVectorResource(context: Context, resId: Int): ImageProvider {
        val drawable = ContextCompat.getDrawable(context, resId)
        val bitmap = Bitmap.createBitmap(
            drawable?.intrinsicWidth ?: 0,
            (drawable?.intrinsicHeight ?: 0)*2,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        drawable?.setBounds(0, 0, canvas.width, canvas.height/2)
        drawable?.draw(canvas)
        return ImageProvider.fromBitmap(bitmap)
    }
}