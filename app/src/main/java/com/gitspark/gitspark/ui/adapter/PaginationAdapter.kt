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
        if (items.isEmpty()) return
        with (this.items) {
            clear()
            addAll(items)
            if (!isLastPage) add(Loading)
        }
        notifyDataSetChanged()
    }

    fun addInitialItems(items: List<Pageable>, isOnlyPage: Boolean) {
        if (items.isEmpty()) return
        this.items.clear()

        this.items.addAll(items)
        if (!isOnlyPage) this.items.add(Loading)
        notifyDataSetChanged()
    }

    fun addItemsOnLoadingComplete(items: List<Pageable>, isLastPage: Boolean) {
        if (this.items.lastIndex < 0) return
        if (this.items.lastOrNull()?.getViewType() != VIEW_TYPE_LOADING) return
        this.items.removeAt(this.items.lastIndex)

        this.items.addAll(items)
        if (!isLastPage) this.items.add(Loading)
        notifyDataSetChanged()
    }

    inner class ViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        fun bind(item: Pageable) = bind(item, view)
    }
}