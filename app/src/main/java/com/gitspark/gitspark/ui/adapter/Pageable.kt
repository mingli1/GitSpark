package com.gitspark.gitspark.ui.adapter

const val VIEW_TYPE_LOADING = 0
const val VIEW_TYPE_VIEW = 1

interface Pageable {

    fun getViewType(): Int
}