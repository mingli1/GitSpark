package com.gitspark.gitspark.model

import com.gitspark.gitspark.ui.adapter.Pageable
import com.gitspark.gitspark.ui.adapter.VIEW_TYPE_DATE_GROUP

data class DateGroup(val date: String) : Pageable {

    override fun getViewType() = VIEW_TYPE_DATE_GROUP

    override fun areItemsTheSame(other: Pageable) = this == (other as? DateGroup ?: false)
}