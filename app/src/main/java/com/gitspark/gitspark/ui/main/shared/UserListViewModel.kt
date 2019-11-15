package com.gitspark.gitspark.ui.main.shared

import com.gitspark.gitspark.model.Page
import com.gitspark.gitspark.model.User
import com.gitspark.gitspark.repository.RepoRepository
import com.gitspark.gitspark.repository.RepoResult
import com.gitspark.gitspark.repository.UserRepository
import com.gitspark.gitspark.repository.UserResult
import com.gitspark.gitspark.ui.nav.UserProfileNavigator
import com.gitspark.gitspark.ui.livedata.SingleLiveEvent
import javax.inject.Inject

enum class UserListType {
    None,
    Watchers,
    Stargazers,
    Contributors,
    Followers,
    Following
}

class UserListViewModel @Inject constructor(
    private val repoRepository: RepoRepository,
    private val userRepository: UserRepository
) : ListViewModel<User>(), UserProfileNavigator {

    val navigateToProfileAction = SingleLiveEvent<String>()
    private var type = UserListType.None

    fun onStart(type: UserListType, args: String) {
        if (type == UserListType.None || args.isEmpty()) return
        this.type = type
        this.args = args
        start()
    }

    override fun onUserSelected(username: String) {
        navigateToProfileAction.value = username
    }

    override fun requestData() {
        when (type) {
            UserListType.None -> return
            UserListType.Watchers -> requestWatchers()
            UserListType.Stargazers -> requestStargazers()
            UserListType.Contributors -> requestContributors()
            UserListType.Followers -> requestFollowers()
            UserListType.Following -> requestFollowing()
        }
    }

    private fun requestWatchers() {
        val args = this.args.split("/")
        subscribe(repoRepository.getWatchers(args[0], args[1], page = page)) {
            onUserDataResult(it)
        }
    }

    private fun requestStargazers() {
        val args = this.args.split("/")
        subscribe(repoRepository.getStargazers(args[0], args[1], page = page)) {
            onUserDataResult(it)
        }
    }

    private fun requestContributors() {
        val args = this.args.split("/")
        subscribe(repoRepository.getContributors(args[0], args[1], page = page)) {
            onUserDataResult(it)
        }
    }

    private fun requestFollowers() {
        if (args.isEmpty()) {
            subscribe(userRepository.getAuthUserFollowers(page = page)) {
                onUserDataResult(it)
            }
        } else {
            subscribe(userRepository.getUserFollowers(args, page)) {
                onUserDataResult(it)
            }
        }
    }

    private fun requestFollowing() {
        if (args.isEmpty()) {
            subscribe(userRepository.getAuthUserFollowing(page = page)) {
                onUserDataResult(it)
            }
        } else {
            subscribe(userRepository.getUserFollowing(args, page)) {
                onUserDataResult(it)
            }
        }
    }

    private fun onUserDataResult(it: UserResult<Page<User>>) {
        when (it) {
            is UserResult.Success -> onDataSuccess(it.value.value, it.value.last)
            is UserResult.Failure -> onDataFailure(it.error)
        }
    }

    private fun onUserDataResult(it: RepoResult<Page<User>>) {
        when (it) {
            is RepoResult.Success -> onDataSuccess(it.value.value, it.value.last)
            is RepoResult.Failure -> onDataFailure(it.error)
        }
    }
}