package com.gitspark.gitspark.ui.adapter

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.gitspark.gitspark.R
import com.gitspark.gitspark.extension.inflate
import com.gitspark.gitspark.model.RepoContent
import com.gitspark.gitspark.model.TYPE_FILE
import kotlinx.android.synthetic.main.repo_content_view.view.*

class RepoContentAdapter : RecyclerView.Adapter<RepoContentAdapter.ViewHolder>() {

    private val items = arrayListOf<RepoContent>()

    override fun getItemCount() = items.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(parent.inflate(R.layout.repo_content_view))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        holder.bind(items[position])

    fun setContent(content: List<RepoContent>) {
        items.clear()
        items.addAll(content)
        notifyDataSetChanged()
    }

    inner class ViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {

        fun bind(item: RepoContent) {
            with (view) {
                repo_content_view.text = item.name
                repo_content_view.setCompoundDrawablesRelativeWithIntrinsicBounds(
                    if (item.type == TYPE_FILE) resources.getDrawable(R.drawable.ic_file, null)
                    else resources.getDrawable(R.drawable.ic_directory, null),
                    null,
                    null,
                    null
                )
            }
        }
    }
}