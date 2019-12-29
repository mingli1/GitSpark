package com.gitspark.gitspark.ui.dialog

import com.gitspark.gitspark.model.Label
import com.gitspark.gitspark.ui.base.BaseViewModel
import com.gitspark.gitspark.ui.livedata.SingleLiveEvent
import javax.inject.Inject

class LabelsViewModel @Inject constructor() : BaseViewModel(), LabelsAdapterCallback {

    val initializeDialog = SingleLiveEvent<ArrayList<Label>>()
    val setLabelsAction = SingleLiveEvent<List<Label>>()
    var labels = arrayListOf<Label>()
    private var init = false

    fun initLabels(snames: Array<String>, sdescs: Array<String>, scolors: Array<String>) {
        if (!init) {
            for (i in snames.indices) {
                labels.add(Label(name = snames[i], description = sdescs[i], color = scolors[i]))
            }
            init = true
        }
    }

    fun initialize(names: Array<String>, descs: Array<String>, colors: Array<String>) {
        val labelsList = arrayListOf<Label>()
        for (i in names.indices) {
            labelsList.add(Label(name = names[i], description = descs[i], color = colors[i]))
        }
        initializeDialog.value = labelsList
    }

    fun onSetLabelsClicked() {
        setLabelsAction.value = labels
    }

    fun onCancel() {
        init = false
        labels.clear()
    }

    override fun addLabel(label: Label) {
        if (!labels.contains(label)) labels.add(label)
    }

    override fun removeLabel(label: Label) {
        labels.removeAll { it.name == label.name }
    }
}