package com.gitspark.gitspark.helper

import android.content.Context
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class KeyboardHelper @Inject constructor(context: Context) {

    private val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

    fun showKeyboard(editText: EditText) {
        imm.showSoftInput(editText, 0)
    }

    fun hideKeyboard(editText: EditText) {
        imm.hideSoftInputFromWindow(editText.windowToken, 0)
    }
}