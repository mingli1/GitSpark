package com.gitspark.gitspark.model

import com.gitspark.gitspark.ui.adapter.Pageable
import com.gitspark.gitspark.ui.adapter.VIEW_TYPE_DATE_GROUP

class DateGroup(val date: String) : Pageable {
    override fun getViewType() = VIEW_TYPE_DATE_GROUP
}