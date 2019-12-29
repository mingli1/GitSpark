package com.gitspark.gitspark.ui.adapter

import android.graphics.Color
import android.view.View
import com.gitspark.gitspark.R
import com.gitspark.gitspark.extension.isVisible
import com.gitspark.gitspark.helper.ColorHelper
import com.gitspark.gitspark.model.Label
import com.gitspark.gitspark.ui.dialog.LabelsAdapterCallback
import kotlinx.android.synthetic.main.label_list_view.view.*

class LabelsAdapter(
    private val currLabels: Array<String>,
    private val colorHelper: ColorHelper,
    private val callback: LabelsAdapterCallback
) : PaginationAdapter() {

    override fun getViewHolderId() = R.layout.label_list_view

    override fun bind(item: Pageable, view: View, position: Int) {
        if (item is Label) {
            with (view) {
                if (currLabels.contains(item.name)) check_mark.visibility = View.VISIBLE
                else check_mark.visibility = View.INVISIBLE

                label_name_field.text = item.name
                label_name_field.setBackgroundColor(Color.parseColor("#${item.color}"))
                label_name_field.setTextColor(Color.parseColor(colorHelper.getTextColor(item.color)))

                label_desc_field.isVisible = item.description.isNotEmpty()
                label_desc_field.text = item.description

                label_list_view.setOnClickListener {
                    if (check_mark.isVisible) {
                        callback.removeLabel(item)
                        check_mark.visibility = View.INVISIBLE
                    } else {
                        callback.addLabel(item)
                        check_mark.visibility = View.VISIBLE
                    }
                }
            }
        }
    }
}