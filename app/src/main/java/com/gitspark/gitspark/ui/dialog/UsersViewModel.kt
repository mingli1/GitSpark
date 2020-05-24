package com.gitspark.gitspark.ui.dialog

import com.gitspark.gitspark.model.User
import com.gitspark.gitspark.ui.base.BaseViewModel
import com.gitspark.gitspark.ui.livedata.SingleLiveEvent
import javax.inject.Inject

class UsersViewModel @Inject constructor() : BaseViewModel(), IssueUserAdapterCallback {

    val initializeDialog = SingleLiveEvent<ArrayList<User>>()
    val setAssigneesAction = SingleLiveEvent<List<User>>()
    var assignees = arrayListOf<User>()
    private var init = false

    fun initAssignees(assigneesList: Array<String>, assigneeAvatarList: Array<String>) {
        if (!init) {
            for (i in assigneesList.indices) {
                assignees.add(User(login = assigneesList[i], avatarUrl = assigneeAvatarList[i]))
            }
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

    fun onSetAssigneesClicked() {
        setAssigneesAction.value = assignees
    }

    fun onCancel() {
        init = false
        assignees.clear()
    }

    override fun addUser(user: User) {
        if (!assignees.contains(user)) assignees.add(user)
    }

    override fun removeUser(user: User) {
        assignees.removeAll { it.login == user.login }
    }
}