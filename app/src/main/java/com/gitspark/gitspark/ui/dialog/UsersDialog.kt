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
import com.gitspark.gitspark.ui.adapter.IssueUsersAdapter
import com.gitspark.gitspark.ui.base.ViewModelFactory
import kotlinx.android.synthetic.main.dialog_assignees.*
import javax.inject.Inject

private const val BUNDLE_EXISTING_USER_LIST = "BUNDLE_ASSIGNEES_LIST"
private const val BUNDLE_EXISTING_USER_AVATAR_LIST = "BUNDLE_ASSIGNEE_URL_LIST"
private const val BUNDLE_USER_AVATAR_LIST = "BUNDLE_AVATAR_URL_LIST"
private const val BUNDLE_USER_LIST = "BUNDLE_USER_LIST"
private const val BUNDLE_IS_REVIEWER = "BUNDLE_IS_REVIEWER"


interface IssueUserAdapterCallback {
    fun addUser(user: User)
    fun removeUser(user: User)
}

interface UsersDialogCallback {
    fun onUsersSet(users: List<User>, isReviewer: Boolean)
}

class UsersDialog : FullBottomSheetDialog() {

    @Inject lateinit var viewModelFactory: ViewModelFactory
    private val viewModel by lazy {
        ViewModelProviders.of(this, viewModelFactory)[UsersViewModel::class.java]
    }

    private lateinit var issueUsersAdapter: IssueUsersAdapter
    private var isReviewer = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_assignees, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val userList = arguments?.getStringArray(BUNDLE_USER_LIST)
        val assigneesList = arguments?.getStringArray(BUNDLE_EXISTING_USER_LIST)
        val userAvatarList = arguments?.getStringArray(BUNDLE_USER_AVATAR_LIST)
        val assigneeAvatarList = arguments?.getStringArray(BUNDLE_EXISTING_USER_AVATAR_LIST)
        isReviewer = arguments?.getBoolean(BUNDLE_IS_REVIEWER) ?: false

        viewModel.initAssignees(assigneesList!!, assigneeAvatarList!!)

        assignees_list.layoutManager = LinearLayoutManager(context, VERTICAL, false)
        issueUsersAdapter = IssueUsersAdapter(viewModel.assignees.map { it.login }.toTypedArray(), viewModel)
        assignees_list.adapter = issueUsersAdapter

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
        viewModel.initializeDialog.observe(viewLifecycleOwner) { issueUsersAdapter.setItems(it, true) }
        viewModel.setAssigneesAction.observe(viewLifecycleOwner) {
            (parentFragment as UsersDialogCallback).onUsersSet(it, isReviewer)
        }
    }

    companion object {
        fun newInstance(
            users: Array<String>,
            userAvatars: Array<String>,
            existingUsers: Array<String>,
            existingUserAvatars: Array<String>,
            isReviewer: Boolean = false
        ) = UsersDialog().apply {
            arguments = Bundle().apply {
                putStringArray(BUNDLE_USER_LIST, users)
                putStringArray(BUNDLE_EXISTING_USER_LIST, existingUsers)
                putStringArray(BUNDLE_USER_AVATAR_LIST, userAvatars)
                putStringArray(BUNDLE_EXISTING_USER_AVATAR_LIST, existingUserAvatars)
                putBoolean(BUNDLE_IS_REVIEWER, isReviewer)
            }
        }
    }
}