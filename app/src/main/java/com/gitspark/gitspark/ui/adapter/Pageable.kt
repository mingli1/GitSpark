package com.gitspark.gitspark.ui.adapter

const val VIEW_TYPE_LOADING = 0
const val VIEW_TYPE_VIEW = 1
const val VIEW_TYPE_DATE_GROUP = 2

interface Pageable {

    fun getViewType(): Int
}