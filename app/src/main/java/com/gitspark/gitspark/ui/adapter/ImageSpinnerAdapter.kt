package com.gitspark.gitspark.ui.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.gitspark.gitspark.R

class ImageSpinnerAdapter(
    private val context: Context,
    private val iconIds: Array<Int>,
    private val strs: Array<String>
) : BaseAdapter() {

    override fun getCount() = strs.size

    override fun getItem(position: Int) = null

    override fun getItemId(position: Int) = 0L

    @SuppressLint("ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        return LayoutInflater.from(context).inflate(R.layout.custom_spinner_item, null).apply {
            findViewById<ImageView>(R.id.icon).setImageResource(iconIds[position])
            findViewById<TextView>(R.id.item_name).text = strs[position]
        }
    }
}