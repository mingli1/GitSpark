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
private const val BUNDLE_CHECKED_ITEM = "BUNDLE_CHECKED_ITEM"
private const val BUNDLE_POSITIVE_TEXT = "BUNDLE_POSITIVE_TEXT"
private const val BUNDLE_NEGATIVE_TEXT = "BUNDLE_NEGATIVE_TEXT"

class SelectDialog : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val items = arguments?.getStringArray(BUNDLE_ITEMS)
        return AlertDialog.Builder(context!!)
            .setTitle(arguments?.getString(BUNDLE_TITLE))
            .setCancelable(true)
            .setSingleChoiceItems(items, items?.indexOf(arguments?.getString(BUNDLE_CHECKED_ITEM)) ?: 0) { _, _ -> }
            .setPositiveButton(arguments?.getString(BUNDLE_POSITIVE_TEXT)) { _, _ ->
                val lv = (dialog as AlertDialog).listView
                val selectedItem = lv.adapter.getItem(lv.checkedItemPosition)
                (parentFragment as SelectDialogCallback).onSelected(selectedItem as String)
                dismiss()
            }
            .setNegativeButton(arguments?.getString(BUNDLE_NEGATIVE_TEXT)) { _, _ -> dismiss() }
            .create()
    }

    companion object {
        fun newInstance(
            title: String,
            items: Array<String>,
            checkedItem: String = "",
            positiveText: String = "Lock",
            negativeText: String = "Cancel"
        ) = SelectDialog().apply {
                arguments = Bundle().apply {
                    putString(BUNDLE_TITLE, title)
                    putStringArray(BUNDLE_ITEMS, items)
                    putString(BUNDLE_CHECKED_ITEM, checkedItem)
                    putString(BUNDLE_POSITIVE_TEXT, positiveText)
                    putString(BUNDLE_NEGATIVE_TEXT, negativeText)
                }
            }
    }
}