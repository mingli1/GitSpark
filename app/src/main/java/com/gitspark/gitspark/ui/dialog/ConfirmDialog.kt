package com.gitspark.gitspark.ui.dialog

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment

interface ConfirmDialogCallback {
    fun onPositiveClicked()
    fun onNegativeClicked()
}

private const val BUNDLE_TITLE = "BUNDLE_TITLE"
private const val BUNDLE_MESSAGE = "BUNDLE_MESSAGE"

class ConfirmDialog : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(context!!)
            .setTitle(arguments?.getString(BUNDLE_TITLE))
            .setMessage(arguments?.getString(BUNDLE_MESSAGE))
            .setCancelable(true)
            .setPositiveButton("Yes") { _, _ ->
                (parentFragment as ConfirmDialogCallback).onPositiveClicked()
                dismiss()
            }
            .setNegativeButton("No") { _, _ ->
                (parentFragment as ConfirmDialogCallback).onNegativeClicked()
                dismiss()
            }
            .create()
    }

    companion object {
        fun newInstance(title: String, message: String) =
            ConfirmDialog().apply {
                arguments = Bundle().apply {
                    putString(BUNDLE_TITLE, title)
                    putString(BUNDLE_MESSAGE, message)
                }
            }
    }
}