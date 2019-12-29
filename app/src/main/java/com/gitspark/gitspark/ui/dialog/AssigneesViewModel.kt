package com.gitspark.gitspark.ui.dialog

import com.gitspark.gitspark.model.User
import com.gitspark.gitspark.ui.base.BaseViewModel
import com.gitspark.gitspark.ui.livedata.SingleLiveEvent
import javax.inject.Inject

class AssigneesViewModel @Inject constructor() : BaseViewModel(), AssigneesAdapterCallback {

    val initializeDialog = SingleLiveEvent<ArrayList<User>>()
    var assignees = arrayListOf<String>()
    private var init = false

    fun initAssignees(assigneesList: Array<String>) {
        if (!init) {
            assignees.addAll(assigneesList)
            init = true
        }
    }

    fun initialize(userList: Array<String>, avatarsList: Array<String>) {
        val itemsList = arrayListOf<User>()
        for (i in userList.indices) {
            itemsList.add(User(login = userList[i], avatarUrl = avatarsList[i]))
        }
        initializeDialog.value = itemsList
    }

    fun onCancel() {
        init = false
        assignees.clear()
    }

    override fun addUser(username: String) {
        if (!assignees.contains(username)) assignees.add(username)
    }

    override fun removeUser(username: String) {
        assignees.remove(username)
    }
}