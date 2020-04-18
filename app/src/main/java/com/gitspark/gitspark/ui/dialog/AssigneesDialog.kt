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
import com.gitspark.gitspark.model.User
import com.gitspark.gitspark.ui.adapter.AssigneesAdapter
import com.gitspark.gitspark.ui.base.ViewModelFactory
import kotlinx.android.synthetic.main.dialog_assignees.*
import javax.inject.Inject

const val BUNDLE_ASSIGNEES_LIST = "BUNDLE_ASSIGNEES_LIST"
const val BUNDLE_ASSIGNEE_AVATAR_LIST = "BUNDLE_ASSIGNEE_URL_LIST"
const val BUNDLE_USER_AVATAR_LIST = "BUNDLE_AVATAR_URL_LIST"
const val BUNDLE_USER_LIST = "BUNDLE_USER_LIST"

interface AssigneesAdapterCallback {
    fun addUser(user: User)
    fun removeUser(user: User)
}

interface AssigneesDialogCallback {
    fun onAssigneesSet(assignees: List<User>)
}

class AssigneesDialog : FullBottomSheetDialog() {

    @Inject lateinit var viewModelFactory: ViewModelFactory
    private val viewModel by lazy {
        ViewModelProviders.of(this, viewModelFactory)[AssigneesViewModel::class.java]
    }

    private lateinit var assigneesAdapter: AssigneesAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_assignees, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val userList = arguments?.getStringArray(BUNDLE_USER_LIST)
        val assigneesList = arguments?.getStringArray(BUNDLE_ASSIGNEES_LIST)
        val userAvatarList = arguments?.getStringArray(BUNDLE_USER_AVATAR_LIST)
        val assigneeAvatarList = arguments?.getStringArray(BUNDLE_ASSIGNEE_AVATAR_LIST)

        viewModel.initAssignees(assigneesList!!, assigneeAvatarList!!)

        assignees_list.layoutManager = LinearLayoutManager(context, VERTICAL, false)
        assigneesAdapter = AssigneesAdapter(viewModel.assignees.map { it.login }.toTypedArray(), viewModel)
        assignees_list.adapter = assigneesAdapter

        viewModel.initialize(userList!!, userAvatarList!!)

        observeViewModel()

        set_assignees_button.setOnClickListener {
            viewModel.onSetAssigneesClicked()
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        assignees_list.adapter = null
    }

    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)
        viewModel.onCancel()
    }

    private fun observeViewModel() {
        viewModel.initializeDialog.observe(viewLifecycleOwner) { assigneesAdapter.setItems(it, true) }
        viewModel.setAssigneesAction.observe(viewLifecycleOwner) {
            (parentFragment as AssigneesDialogCallback).onAssigneesSet(it)
        }
    }

    companion object {
        fun newInstance(
            users: Array<String>,
            userAvatars: Array<String>,
            assignees: Array<String>,
            assigneeAvatars: Array<String>
        ) = AssigneesDialog().apply {
            arguments = Bundle().apply {
                putStringArray(BUNDLE_USER_LIST, users)
                putStringArray(BUNDLE_ASSIGNEES_LIST, assignees)
                putStringArray(BUNDLE_USER_AVATAR_LIST, userAvatars)
                putStringArray(BUNDLE_ASSIGNEE_AVATAR_LIST, assigneeAvatars)
            }
        }
    }
}