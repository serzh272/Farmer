package ru.serzh272.farmer.ui.adapters

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.view.View
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import ru.serzh272.farmer.R
import ru.serzh272.farmer.extensions.attrValue
import ru.serzh272.farmer.data.local.entities.Field
import ru.serzh272.farmer.extensions.dpToPx

class FieldItemTouchHelperCallback(
    private val adapter: FieldsAdapter,
    private val swipeListener: (Field) -> Unit
) : ItemTouchHelper.Callback() {
    private val bgRect = RectF()
    private val bgPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val iconBounds = Rect()



    override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
        return if(viewHolder is ItemTouchViewHolder){
            makeFlag(ItemTouchHelper.ACTION_STATE_SWIPE, ItemTouchHelper.START)
        }else{
            makeFlag(ItemTouchHelper.ACTION_STATE_IDLE, ItemTouchHelper.START)
        }
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return false
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        swipeListener.invoke(adapter.items[viewHolder.bindingAdapterPosition])
    }

    override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
        if (actionState != ItemTouchHelper.ACTION_STATE_IDLE && viewHolder is ItemTouchViewHolder){
            viewHolder.onItemSelected()
        }
        super.onSelectedChanged(viewHolder, actionState)
    }

    override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
        if (viewHolder is ItemTouchViewHolder) viewHolder.onItemCleared()
        super.clearView(recyclerView, viewHolder)
    }

    override fun onChildDraw(
        canvas: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE){
            val itemView = viewHolder.itemView
            drawBackground(canvas, itemView)
            drawIcon(canvas, itemView, dX)
        }
        super.onChildDraw(canvas, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }

    private fun drawIcon(canvas: Canvas, itemView: View, dX: Float) {
        val icon = ResourcesCompat.getDrawable(itemView.resources, R.drawable.ic_delete_24, itemView.context.theme)
        val iconSize = itemView.resources.getDimensionPixelSize(R.dimen.icon_size_large_24)
        val space = itemView.resources.getDimensionPixelSize(R.dimen.spacing_normal_16)
        val margin = (itemView.bottom - itemView.top - iconSize)/2
        with(iconBounds){
            left = itemView.right + dX.toInt() + space
            top = itemView.top + margin
            right = itemView.right + dX.toInt() + iconSize + space
            bottom = itemView.bottom - margin
        }
        icon?.bounds = iconBounds
        icon?.draw(canvas)
    }

    private fun drawBackground(canvas: Canvas, itemView: View) {
        with(bgRect){
            left = itemView.left.toFloat()
            top = itemView.top.toFloat()
            right = itemView.right.toFloat()
            bottom = itemView.bottom.toFloat()
        }
        with(bgPaint){
            color = itemView.context.attrValue(R.attr.colorPrimaryVariant)
        }
        val r = itemView.context.dpToPx(8)
        canvas.drawRoundRect(bgRect, r, r,  bgPaint)
    }

}

interface ItemTouchViewHolder{
    fun onItemSelected()
    fun onItemCleared()
}