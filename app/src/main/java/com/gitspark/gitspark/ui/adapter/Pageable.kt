package com.gitspark.gitspark.ui.adapter

const val VIEW_TYPE_LOADING = 0
const val VIEW_TYPE_VIEW = 1
const val VIEW_TYPE_DATE_GROUP = 2
const val VIEW_TYPE_ISSUE_EVENT = 3

interface Pageable {

    fun getViewType(): Int

    fun areItemsTheSame(other: Pageable): Boolean
}