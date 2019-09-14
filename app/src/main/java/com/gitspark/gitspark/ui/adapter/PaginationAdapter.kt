package com.gitspark.gitspark.ui.adapter

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.gitspark.gitspark.R
import com.gitspark.gitspark.extension.inflate
import com.gitspark.gitspark.model.Loading

abstract class PaginationAdapter : RecyclerView.Adapter<PaginationAdapter.ViewHolder>() {

    private val items = arrayListOf<Pageable>()

    override fun getItemCount() = items.size

    override fun getItemViewType(position: Int) = items[position].getViewType()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(parent.inflate(
            if (viewType == VIEW_TYPE_VIEW) getViewHolderId() else R.layout.loading_view
        ))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        holder.bind(items[position])

    abstract fun getViewHolderId(): Int

    abstract fun bind(item: Pageable, view: View)

    fun setItems(items: List<Pageable>, isLastPage: Boolean) {
        with (this.items) {
            clear()
            addAll(items)
            if (!isLastPage) add(Loading)
        }
        notifyDataSetChanged()
    }

    inner class ViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        fun bind(item: Pageable) = bind(item, view)
    }
}