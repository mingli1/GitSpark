package com.gitspark.gitspark.ui.dialog

import android.content.Context
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
import com.gitspark.gitspark.ui.adapter.AssigneesAdapter
import com.gitspark.gitspark.ui.base.ViewModelFactory
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.dialog_assignees.*
import javax.inject.Inject

const val BUNDLE_ASSIGNEES_LIST = "BUNDLE_ASSIGNEES_LIST"
const val BUNDLE_AVATAR_URL_LIST = "BUNDLE_AVATAR_URL_LIST"
const val BUNDLE_USER_LIST = "BUNDLE_USER_LIST"

interface AssigneesAdapterCallback {
    fun addUser(username: String)
    fun removeUser(username: String)
}

class AssigneesDialog : BottomSheetDialogFragment() {

    @Inject lateinit var viewModelFactory: ViewModelFactory
    private val viewModel by lazy {
        ViewModelProviders.of(this, viewModelFactory)[AssigneesViewModel::class.java]
    }

    private lateinit var layoutManager: LinearLayoutManager
    private lateinit var assigneesAdapter: AssigneesAdapter

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_assignees, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val userList = arguments?.getStringArray(BUNDLE_USER_LIST)
        val assigneesList = arguments?.getStringArray(BUNDLE_ASSIGNEES_LIST)
        val avatarsList = arguments?.getStringArray(BUNDLE_AVATAR_URL_LIST)

        viewModel.initAssignees(assigneesList!!)

        layoutManager = LinearLayoutManager(context, VERTICAL, false)
        assignees_list.layoutManager = layoutManager
        assignees_list.setHasFixedSize(true)
        assigneesAdapter = AssigneesAdapter(viewModel.assignees.toTypedArray(), viewModel)
        assignees_list.adapter = assigneesAdapter

        viewModel.initialize(userList!!, avatarsList!!)

        observeViewModel()
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
    }

    companion object {
        fun newInstance(
            users: Array<String>,
            assignees: Array<String>,
            avatars: Array<String>
        ) = AssigneesDialog().apply {
            arguments = Bundle().apply {
                putStringArray(BUNDLE_USER_LIST, users)
                putStringArray(BUNDLE_ASSIGNEES_LIST, assignees)
                putStringArray(BUNDLE_AVATAR_URL_LIST, avatars)
            }
        }
    }
}