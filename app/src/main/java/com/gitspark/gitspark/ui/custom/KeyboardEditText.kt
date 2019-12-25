package com.gitspark.gitspark.ui.custom

import android.content.Context
import android.util.AttributeSet
import android.view.KeyEvent
import android.widget.EditText

class KeyboardEditText(context: Context, attrs: AttributeSet) : EditText(context, attrs) {

    private var callback: ((KeyboardEditText, String) -> Unit)? = null

    override fun onKeyPreIme(keyCode: Int, event: KeyEvent): Boolean {
        if (event.keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_UP) {
            callback?.invoke(this, text.toString())
        }
        return super.dispatchKeyEvent(event)
    }

    fun onImeBack(callback: (KeyboardEditText, String) -> Unit) {
        this.callback = callback
    }
}