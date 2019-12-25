package com.gitspark.gitspark.ui.main.issues

interface CommentMenuCallback {

    fun onDeleteSelected(id: Long)

    fun onCopyLinkSelected(url: String)

    fun onEditCommentFocused()

    fun onEditCommentUnfocused()
}