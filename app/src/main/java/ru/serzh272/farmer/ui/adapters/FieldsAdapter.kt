package ru.serzh272.farmer.ui.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import ru.serzh272.farmer.App
import ru.serzh272.farmer.R
import ru.serzh272.farmer.databinding.ItemFieldBinding
import ru.serzh272.farmer.data.local.entities.Field
import ru.serzh272.farmer.extensions.format

class FieldsAdapter(private val listener:(Field)->Unit) : RecyclerView.Adapter<FieldsAdapter.FieldViewHolder>() {
    var items: List<Field> = listOf()

    class FieldViewHolder(private val binding: ItemFieldBinding) :
        RecyclerView.ViewHolder(binding.root), ItemTouchViewHolder {
        fun bind(item: Field, listener:(Field)->Unit) {
            with(binding){
                //tvFieldName.text = "${App.applicationContext().getString(R.string.name_label_text)} ${item.name}"
                tvFieldName.text = App.applicationContext().getString(R.string.name_label_text, item.name)
                tvFieldCulture.text = App.applicationContext().getString(R.string.culture_label_text, item.culture)
                tvSowingDate.text = App.applicationContext().getString(R.string.sowing_date_label_text, item.sowingDate.format("dd.MM.yyyy"))
            }

            itemView.setOnClickListener { listener(item) }
        }

        override fun onItemSelected() {
            itemView.setBackgroundColor(Color.LTGRAY)
        }

        override fun onItemCleared() {
            itemView.background = AppCompatResources.getDrawable(itemView.context, R.drawable.round_corner_bg)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FieldViewHolder {
        return FieldViewHolder(ItemFieldBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: FieldViewHolder, position: Int) {
        holder.bind(items[position], listener)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun updateItems(data: List<Field>) {
        val diffCallback = object : DiffUtil.Callback() {
            override fun getOldListSize(): Int = items.size

            override fun getNewListSize(): Int = data.size

            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
                items[oldItemPosition].id == data[newItemPosition].id

            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
                items[oldItemPosition] == data[newItemPosition]

        }
        val diffResult = DiffUtil.calculateDiff(diffCallback, true)
        items = data
        diffResult.dispatchUpdatesTo(this)
    }
}