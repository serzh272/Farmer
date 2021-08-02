package ru.serzh272.farmer.ui.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import ru.serzh272.farmer.R
import ru.serzh272.farmer.databinding.ItemSearchBinding
import ru.serzh272.farmer.models.SearchMapObject

class SearchResultsAdapter(private val listener: (SearchMapObject) -> Unit) :
    RecyclerView.Adapter<SearchResultsAdapter.FieldViewHolder>() {
    var items: MutableList<SearchMapObject> = mutableListOf()

    class FieldViewHolder(private val binding: ItemSearchBinding) :
        RecyclerView.ViewHolder(binding.root), ItemTouchViewHolder {
        fun bind(item: SearchMapObject, listener: (SearchMapObject) -> Unit) {
            with(binding) {
                tvFieldName.text =
                    "${item.name}"
                tvFieldDescription.text = "${item.description}"
            }

            itemView.setOnClickListener { listener(item) }
        }

        override fun onItemSelected() {
            itemView.setBackgroundColor(Color.LTGRAY)
        }

        override fun onItemCleared() {
            itemView.background =
                AppCompatResources.getDrawable(itemView.context, R.drawable.round_corner_bg)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FieldViewHolder {
        return FieldViewHolder(
            ItemSearchBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: FieldViewHolder, position: Int) {
        holder.bind(items[position], listener)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun deleteAllItems(){
        items.clear()

    }

    fun updateItems(data: List<SearchMapObject>) {
        val diffCallback = object : DiffUtil.Callback() {
            override fun getOldListSize(): Int = items.size

            override fun getNewListSize(): Int = data.size

            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
                items[oldItemPosition].location.latitude == data[newItemPosition].location.latitude &&
                        items[oldItemPosition].location.longitude == data[newItemPosition].location.longitude

            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
                items[oldItemPosition] == data[newItemPosition]

        }
        val diffResult = DiffUtil.calculateDiff(diffCallback, true)
        items = data.toMutableList()
        diffResult.dispatchUpdatesTo(this)
    }
}