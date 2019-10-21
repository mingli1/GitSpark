package com.gitspark.gitspark.ui.adapter

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.gitspark.gitspark.R
import com.gitspark.gitspark.extension.inflate
import com.gitspark.gitspark.extension.setColor
import com.gitspark.gitspark.helper.LanguageColorHelper
import kotlinx.android.synthetic.main.language_view.view.*
import java.util.*

class LanguageAdapter(
    private val colorHelper: LanguageColorHelper
) : RecyclerView.Adapter<LanguageAdapter.ViewHolder>() {

    private var items = sortedMapOf<String, Int>()
    private var totalBytes = 0f

    override fun getItemCount() = items.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(parent.inflate(R.layout.language_view))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        for ((i, key) in items.keys.withIndex()) {
            if (i == position) {
                holder.bind(key)
                return
            }
        }
    }

    fun setContent(content: SortedMap<String, Int>) {
        if (items == content) return
        items = content
        totalBytes = 0f
        items.forEach { totalBytes += it.value }
        notifyDataSetChanged()
    }

    inner class ViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {

        fun bind(key: String) {
            with (view) {
                val bytes = (items[key] ?: 0).toFloat()
                val percentage = (bytes / totalBytes) * 100f
                val percentStr = String.format("%.2f%%", percentage)

                language_field.text = key
                breakdown_percent.text = percentStr

                colorHelper.getColor(key)?.let {
                    language_field.compoundDrawablesRelative[0].setColor(it)
                }
            }
        }
    }
}