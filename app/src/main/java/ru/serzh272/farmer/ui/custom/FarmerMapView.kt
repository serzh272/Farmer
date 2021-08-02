package ru.serzh272.farmer.ui.custom

import android.content.Context
import android.util.AttributeSet
import androidx.core.view.isVisible
import com.yandex.mapkit.mapview.MapView
import ru.serzh272.farmer.PointD
import ru.serzh272.farmer.extensions.listPointFToListMapPoint
import ru.serzh272.farmer.extensions.toListOfPointD

class FarmerMapView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : MapView(context, attrs, defStyleAttr) {
    private val drawFieldsView = DrawFieldsView(context)
    var isEditMode: Boolean = false
        set(value) {
            drawFieldsView.isVisible = value
            drawFieldsView.points = mutableListOf()
            field = value
        }

    init {
        drawFieldsView.isVisible = isEditMode
        addView(drawFieldsView)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        super.onLayout(changed, l, t, r, b)
        drawFieldsView.layout(l, t, r, b)
    }

    fun applyField(result: (List<PointD>) -> Unit) {
        val fieldPoints = listPointFToListMapPoint(drawFieldsView.points).filterNotNull()
        if (fieldPoints.size > 2) {
            result(fieldPoints.toListOfPointD())
        }
    }

}