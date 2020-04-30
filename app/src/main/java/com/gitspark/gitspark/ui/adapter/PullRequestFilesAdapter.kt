package com.gitspark.gitspark.ui.adapter

import android.annotation.SuppressLint
import android.view.View
import com.gitspark.gitspark.R
import com.gitspark.gitspark.extension.formatLarge
import com.gitspark.gitspark.model.PR_FILE_ADDED
import com.gitspark.gitspark.model.PR_FILE_MODIFIED
import com.gitspark.gitspark.model.PullRequestFile
import kotlinx.android.synthetic.main.item_pr_file.view.*

class PullRequestFilesAdapter : PaginationAdapter() {

    override fun getViewHolderId() = R.layout.item_pr_file

    @SuppressLint("DefaultLocale")
    override fun bind(item: Pageable, view: View, position: Int) {
        if (item is PullRequestFile) {
            with (view) {
                file_name.text = item.name
                status_field.text = item.status.toUpperCase()
                status_field.setTextColor(when (item.status) {
                    PR_FILE_ADDED -> context.getColor(R.color.colorGreen)
                    PR_FILE_MODIFIED -> context.getColor(R.color.colorYellowOrange)
                    else -> context.getColor(R.color.colorRed)
                })
                changes_field.text = context.getString(R.string.changes_text, item.changes.formatLarge())
                additions_field.text = context.getString(R.string.additions_text, item.additions.formatLarge())
                deletions_field.text = context.getString(R.string.deletions_text, item.deletions.formatLarge())
            }
        }
    }
}