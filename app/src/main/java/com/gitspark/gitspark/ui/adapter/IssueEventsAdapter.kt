package com.gitspark.gitspark.ui.adapter

import android.text.SpannableStringBuilder
import android.view.View
import com.gitspark.gitspark.R

class IssueEventsAdapter : PaginationAdapter() {

    private val spannableCache = mutableMapOf<Long, SpannableStringBuilder>()

    override fun setItems(items: List<Pageable>, isLastPage: Boolean) {

    }

    override fun getViewHolderId() = R.layout.issue_comment_view

    override fun bind(item: Pageable, view: View) {

    }
}