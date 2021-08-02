package ru.serzh272.farmer.ui.custom

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ViewGroup
import androidx.core.graphics.ColorUtils
import com.google.android.material.floatingactionbutton.FloatingActionButton
import ru.serzh272.farmer.R
import ru.serzh272.farmer.extensions.attrValue
import ru.serzh272.farmer.extensions.dpToPx

class DrawFieldsView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ViewGroup(context, attrs, defStyleAttr) {
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = ColorUtils.setAlphaComponent(Color.BLUE, 90)
        style = Paint.Style.FILL_AND_STROKE
        strokeWidth = 2f
    }
    var points = mutableListOf<PointF>()
    private var fab: FloatingActionButton? = null
    private var btnPadding = context.dpToPx(24)
    private val path = Path()

    init {
        fab = FloatingActionButton(context).apply {
            setImageResource(R.drawable.ic_del_point_24)
            imageTintList = ColorStateList.valueOf(context.attrValue(R.attr.errorIconTint))
        }
        addView(fab)
        fab!!.setOnClickListener {
            if (points.isNotEmpty()){
                points.removeLast()
                invalidate()
            }
        }
    }

    override fun dispatchDraw(canvas: Canvas?) {
        super.dispatchDraw(canvas)
        path.reset()
        for (ind in points.indices) {
            if (ind == 0) {
                path.moveTo(points[ind].x, points[ind].y)
            } else {
                path.lineTo(points[ind].x, points[ind].y)
            }
            canvas?.drawCircle(points[ind].x, points[ind].y, context.dpToPx(4), paint)
        }
        canvas?.drawPath(path, paint)
    }


    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        measureChild(fab, widthMeasureSpec, heightMeasureSpec)
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {

        fab?.layout(
            (r-paddingEnd - fab!!.measuredWidth - btnPadding).toInt(),
            (t + paddingTop + btnPadding).toInt(),
            (r - paddingRight - btnPadding).toInt(),
            (t + paddingTop + fab!!.measuredHeight + btnPadding).toInt()
        )
    }


    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                points.add(PointF(event.x, event.y))
                invalidate()
                return true
            }
            MotionEvent.ACTION_MOVE -> {
                if (points.isNotEmpty()) {
                    points.removeLast()
                }
                points.add(PointF(event.x, event.y))
                invalidate()
            }
        }
        return super.onTouchEvent(event)
    }
}