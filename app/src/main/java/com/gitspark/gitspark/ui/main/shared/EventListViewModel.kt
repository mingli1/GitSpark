package com.gitspark.gitspark.ui.main.shared

import com.gitspark.gitspark.model.*
import com.gitspark.gitspark.repository.EventRepository
import com.gitspark.gitspark.repository.EventResult
import com.gitspark.gitspark.ui.livedata.SingleLiveEvent
import com.gitspark.gitspark.ui.nav.FeedNavigator
import javax.inject.Inject

enum class EventListType {
    None,
    PublicEvents,
    RepoEvents
}

class EventListViewModel @Inject constructor(
    private val eventRepository: EventRepository
) : ListViewModel<Event>(), FeedNavigator {

    val navigateToProfileAction = SingleLiveEvent<String>()
    val navigateToRepo = SingleLiveEvent<String>()
    val navigateToIssue = SingleLiveEvent<Pair<Issue, Boolean>>()
    private var type = EventListType.None

    fun onStart(type: EventListType, args: String) {
        if (type == EventListType.None) return
        this.type = type
        this.args = args
        start()
    }

    override fun onUserSelected(username: String) {
        navigateToProfileAction.value = username
    }

    override fun onRepoSelected(fullName: String) {
        navigateToRepo.value = fullName
    }

    override fun onIssueSelected(issue: Issue) {
        navigateToIssue.value = Pair(issue, true)
    }

    override fun onPullRequestSelected(pullRequest: PullRequest, repoFullName: String) {
        navigateToIssue.value = Pair(pullRequest.toSimpleIssue(repoFullName), false)
    }

    override fun requestData() {
        when (type) {
            EventListType.None -> return
            EventListType.PublicEvents -> requestPublicEvents()
            EventListType.RepoEvents -> requestRepoEvents()
        }
    }

    private fun requestPublicEvents() {
        subscribe(eventRepository.getPublicEvents(page = page)) { onEventResult(it) }
    }

    private fun requestRepoEvents() {
        val args = this.args.split("/")
        subscribe(eventRepository.getRepoEvents(args[0], args[1], page = page)) { onEventResult(it) }
    }

    private fun onEventResult(it: EventResult<Page<Event>>) {
        when (it) {
            is EventResult.Success -> onDataSuccess(it.value.value, it.value.last)
            is EventResult.Failure -> onDataFailure(it.error)
        }
    }
}