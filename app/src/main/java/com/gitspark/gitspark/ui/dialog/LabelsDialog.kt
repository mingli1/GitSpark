package com.gitspark.gitspark.ui.dialog

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.VERTICAL
import com.gitspark.gitspark.R
import com.gitspark.gitspark.extension.observe
import com.gitspark.gitspark.helper.ColorHelper
import com.gitspark.gitspark.model.Label
import com.gitspark.gitspark.ui.adapter.LabelsAdapter
import com.gitspark.gitspark.ui.base.ViewModelFactory
import kotlinx.android.synthetic.main.dialog_labels.*
import javax.inject.Inject

const val BUNDLE_LABEL_NAME_LIST = "BUNDLE_LABEL_NAME_LIST"
const val BUNDLE_LABEL_DESC_LIST = "BUNDLE_LABEL_DESC_LIST"
const val BUNDLE_LABEL_COLOR_LIST = "BUNDLE_LABEL_COLOR_LIST"
const val BUNDLE_LABEL_NAME_LIST_S = "BUNDLE_LABEL_NAME_LIST_S"
const val BUNDLE_LABEL_DESC_LIST_S = "BUNDLE_LABEL_DESC_LIST_S"
const val BUNDLE_LABEL_COLOR_LIST_S = "BUNDLE_LABEL_COLOR_LIST_S"

interface LabelsAdapterCallback {
    fun addLabel(label: Label)
    fun removeLabel(label: Label)
}

interface LabelsDialogCallback {
    fun onLabelsSet(labels: List<Label>)
}

class LabelsDialog : FullBottomSheetDialog() {

    @Inject lateinit var viewModelFactory: ViewModelFactory
    @Inject lateinit var colorHelper: ColorHelper
    private val viewModel by lazy {
        ViewModelProviders.of(this, viewModelFactory)[LabelsViewModel::class.java]
    }

    private lateinit var labelsAdapter: LabelsAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_labels, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val names = arguments?.getStringArray(BUNDLE_LABEL_NAME_LIST)
        val descs = arguments?.getStringArray(BUNDLE_LABEL_DESC_LIST)
        val colors = arguments?.getStringArray(BUNDLE_LABEL_COLOR_LIST)
        val snames = arguments?.getStringArray(BUNDLE_LABEL_NAME_LIST_S)
        val sdescs = arguments?.getStringArray(BUNDLE_LABEL_DESC_LIST_S)
        val scolors = arguments?.getStringArray(BUNDLE_LABEL_COLOR_LIST_S)

        viewModel.initLabels(snames!!, sdescs!!, scolors!!)

        labels_list.layoutManager = LinearLayoutManager(context, VERTICAL, false)
        labelsAdapter = LabelsAdapter(viewModel.labels.map { it.name }.toTypedArray(), colorHelper, viewModel)
        labels_list.adapter = labelsAdapter

        viewModel.initialize(names!!, descs!!, colors!!)

        observeViewModel()

        set_labels_button.setOnClickListener {
            viewModel.onSetLabelsClicked()
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        labels_list.adapter = null
    }

    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)
        viewModel.onCancel()
    }

    private fun observeViewModel() {
        viewModel.initializeDialog.observe(viewLifecycleOwner) { labelsAdapter.setItems(it, true) }
        viewModel.setLabelsAction.observe(viewLifecycleOwner) {
            (parentFragment as LabelsDialogCallback).onLabelsSet(it)
        }
    }

    companion object {
        fun newInstance(
            sNames: Array<String>,
            sDescs: Array<String>,
            sColors: Array<String>,
            names: Array<String>,
            descs: Array<String>,
            colors: Array<String>
        ) = LabelsDialog().apply {
            arguments = Bundle().apply {
                putStringArray(BUNDLE_LABEL_NAME_LIST_S, sNames)
                putStringArray(BUNDLE_LABEL_DESC_LIST_S, sDescs)
                putStringArray(BUNDLE_LABEL_COLOR_LIST_S, sColors)
                putStringArray(BUNDLE_LABEL_NAME_LIST, names)
                putStringArray(BUNDLE_LABEL_DESC_LIST, descs)
                putStringArray(BUNDLE_LABEL_COLOR_LIST, colors)
            }
        }
    }
}