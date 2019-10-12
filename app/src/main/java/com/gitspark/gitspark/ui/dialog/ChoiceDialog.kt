package com.gitspark.gitspark.ui.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment

interface ChoiceDialogCallback {
    fun onItemSelected(which: Int)
}

private const val BUNDLE_TITLE = "BUNDLE_TITLE"
private const val BUNDLE_ITEMS = "BUNDLE_ITEMS"

class ChoiceDialog : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(context!!)
            .setTitle(arguments?.getString(BUNDLE_TITLE))
            .setCancelable(false)
            .setItems(arguments?.getStringArray(BUNDLE_ITEMS)) { _, which ->
                (parentFragment as ChoiceDialogCallback).onItemSelected(which)
            }
            .create()
    }

    companion object {
        fun newInstance(title: String, items: Array<String>) =
            ChoiceDialog().apply {
                arguments = Bundle().apply {
                    putString(BUNDLE_TITLE, title)
                    putStringArray(BUNDLE_ITEMS, items)
                }
            }
    }
}