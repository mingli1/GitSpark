package com.gitspark.gitspark.ui.dialog

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment

interface SelectDialogCallback {
    fun onSelected(item: String)
}

private const val BUNDLE_TITLE = "BUNDLE_TITLE"
private const val BUNDLE_ITEMS = "BUNDLE_ITEMS"

class SelectDialog : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(context!!)
            .setTitle(arguments?.getString(BUNDLE_TITLE))
            .setCancelable(true)
            .setSingleChoiceItems(arguments?.getStringArray(BUNDLE_ITEMS), 0) { _, _ -> }
            .setPositiveButton("Lock") { _, _ ->
                val lv = (dialog as AlertDialog).listView
                val selectedItem = lv.adapter.getItem(lv.checkedItemPosition)
                (parentFragment as SelectDialogCallback).onSelected(selectedItem as String)
                dismiss()
            }
            .setNegativeButton("Cancel") { _, _ -> dismiss() }
            .create()
    }

    companion object {
        fun newInstance(title: String, items: Array<String>) =
            SelectDialog().apply {
                arguments = Bundle().apply {
                    putString(BUNDLE_TITLE, title)
                    putStringArray(BUNDLE_ITEMS, items)
                }
            }
    }
}