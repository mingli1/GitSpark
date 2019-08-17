package com.gitspark.gitspark.extension

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import com.gitspark.gitspark.R
import com.squareup.picasso.Picasso

inline var View.isVisible: Boolean
    get() = visibility == VISIBLE
    set(value) {
        visibility = if (value) VISIBLE else GONE
    }

fun EditText.onTextChanged(cb: (String) -> Unit) {
    addTextChangedListener(object : TextWatcher {
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            cb(s.toString())
        }
        override fun afterTextChanged(s: Editable?) {}
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
    })
}

fun EditText.getString(): String = text?.toString() ?: ""

fun ImageView.loadImage(url: String) =
    Picasso.get()
        .load(url)
        .placeholder(R.drawable.image_placeholder)
        .into(this)

fun ViewGroup.inflate(layoutId: Int, attachToRoot: Boolean = false): View =
    LayoutInflater.from(context).inflate(layoutId, this, attachToRoot)