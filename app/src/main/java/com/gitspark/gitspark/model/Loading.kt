package com.gitspark.gitspark.model

import com.gitspark.gitspark.ui.adapter.Pageable
import com.gitspark.gitspark.ui.adapter.VIEW_TYPE_LOADING

object Loading : Pageable {
    override fun getViewType() = VIEW_TYPE_LOADING
}