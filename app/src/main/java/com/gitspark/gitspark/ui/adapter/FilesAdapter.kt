package com.gitspark.gitspark.ui.adapter

import android.view.View
import com.gitspark.gitspark.R
import com.gitspark.gitspark.model.File
import kotlinx.android.synthetic.main.file_view.view.*

class FilesAdapter : PaginationAdapter() {

    override fun getViewHolderId() = R.layout.file_view

    override fun bind(item: Pageable, view: View, position: Int) {
        if (item is File) {
            with (view) {
                file_name_field.text = item.name
                path_field.text = item.path
                repo_field.text = item.repo.fullName
            }
        }
    }
}