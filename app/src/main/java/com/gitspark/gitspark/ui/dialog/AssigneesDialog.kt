package com.gitspark.gitspark.ui.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.gitspark.gitspark.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class AssigneesDialog : BottomSheetDialogFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_assignees, container, false)
    }
}