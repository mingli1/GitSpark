package com.gitspark.gitspark.ui.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.VERTICAL
import com.gitspark.gitspark.R
import com.gitspark.gitspark.model.User
import com.gitspark.gitspark.ui.adapter.AssigneesAdapter
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.dialog_assignees.*

const val BUNDLE_ASSIGNEES_LIST = "BUNDLE_ASSIGNEES_LIST"
const val BUNDLE_AVATAR_URL_LIST = "BUNDLE_AVATAR_URL_LIST"
const val BUNDLE_USER_LIST = "BUNDLE_USER_LIST"

class AssigneesDialog : BottomSheetDialogFragment() {

    private lateinit var layoutManager: LinearLayoutManager
    private lateinit var assigneesAdapter: AssigneesAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_assignees, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val userList = arguments?.getStringArray(BUNDLE_USER_LIST)
        val assigneesList = arguments?.getStringArray(BUNDLE_ASSIGNEES_LIST)
        val avatarsList = arguments?.getStringArray(BUNDLE_AVATAR_URL_LIST)

        val itemsList = arrayListOf<User>()
        for (i in userList!!.indices) {
            itemsList.add(User(login = userList[i], avatarUrl = avatarsList!![i]))
        }

        layoutManager = LinearLayoutManager(context, VERTICAL, false)
        assignees_list.layoutManager = layoutManager
        assignees_list.setHasFixedSize(true)
        assigneesAdapter = AssigneesAdapter(assigneesList!!)
        assignees_list.adapter = assigneesAdapter

        assigneesAdapter.setItems(itemsList, true)
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