package com.gitspark.gitspark.helper

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ClipboardHelper @Inject constructor(private val context: Context) {

    private val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

    fun copy(text: String) {
        val clip = ClipData.newPlainText("copied text", text)
        clipboard.primaryClip = clip
    }
}