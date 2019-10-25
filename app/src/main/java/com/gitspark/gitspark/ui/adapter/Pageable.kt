package com.gitspark.gitspark.ui.adapter

const val VIEW_TYPE_LOADING = 0
const val VIEW_TYPE_VIEW = 1
const val VIEW_TYPE_COMMIT_DATE = 2

interface Pageable {

    fun getViewType(): Int
}