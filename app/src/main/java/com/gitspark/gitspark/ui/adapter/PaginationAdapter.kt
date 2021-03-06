package com.gitspark.gitspark.ui.adapter

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.gitspark.gitspark.R
import com.gitspark.gitspark.extension.inflate
import com.gitspark.gitspark.model.Loading

abstract class PaginationAdapter : RecyclerView.Adapter<PaginationAdapter.ViewHolder>() {

    protected val items = arrayListOf<Pageable>()

    override fun getItemCount() = items.size

    override fun getItemViewType(position: Int) = items[position].getViewType()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(parent.inflate(
            when (viewType) {
                VIEW_TYPE_VIEW -> getViewHolderId()
                VIEW_TYPE_LOADING -> R.layout.loading_view
                VIEW_TYPE_ISSUE_EVENT -> R.layout.issue_event_view
                else -> R.layout.date_group_view
            }
        ))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        holder.bind(items[position], position)

    abstract fun getViewHolderId(): Int

    abstract fun bind(item: Pageable, view: View, position: Int)

    open fun setItems(items: List<Pageable>, isLastPage: Boolean) {
        val result = DiffUtil.calculateDiff(DiffCallback(this.items, items))
        with (this.items) {
            clear()
            addAll(items)
            if (!isLastPage) add(Loading)
        }
        result.dispatchUpdatesTo(this)
    }

    inner class ViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        fun bind(item: Pageable, position: Int) = bind(item, view, position)
    }

    inner class DiffCallback(
        private val old: List<Pageable>,
        private val new: List<Pageable>
    ) : DiffUtil.Callback() {

        override fun getOldListSize() = old.size

        override fun getNewListSize() = new.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int) =
            old[oldItemPosition].areItemsTheSame(new[newItemPosition])

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int) = true
    }
}