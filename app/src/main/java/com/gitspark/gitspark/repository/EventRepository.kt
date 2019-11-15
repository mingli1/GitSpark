package com.gitspark.gitspark.repository

import com.gitspark.gitspark.api.service.EventService
import com.gitspark.gitspark.helper.PreferencesHelper
import com.gitspark.gitspark.helper.RetrofitHelper
import com.gitspark.gitspark.model.Event
import com.gitspark.gitspark.model.Page
import io.reactivex.Observable
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EventRepository @Inject constructor(
    private val prefsHelper: PreferencesHelper,
    private val retrofitHelper: RetrofitHelper
) {

    fun getPublicEvents(page: Int): Observable<EventResult<Page<Event>>> {
        return getEventService()
            .getPublicEvents(page)
            .map {
                getSuccess(it.toModel<Event>().apply {
                    value = it.response.map { event -> event.toModel() }
                })
            }
            .onErrorReturn { getFailure("Failed to obtain public events") }
    }

    fun getEvents(username: String, page: Int): Observable<EventResult<Page<Event>>> {
        return getEventService()
            .getEvents(username, page)
            .map {
                getSuccess(it.toModel<Event>().apply {
                    value = it.response.map { event -> event.toModel() }
                })
            }
            .onErrorReturn { getFailure("Failed to obtain events for $username") }
    }

    fun getReceivedEvents(username: String, page: Int): Observable<EventResult<Page<Event>>> {
        return getEventService()
            .getReceivedEvents(username, page)
            .map {
                getSuccess(it.toModel<Event>().apply {
                    value = it.response.map { event -> event.toModel() }
                })
            }
            .onErrorReturn { getFailure("Failed to obtain received events for $username") }
    }

    fun getRepoEvents(username: String, repoName: String, page: Int): Observable<EventResult<Page<Event>>> {
        return getEventService()
            .getRepoEvents(username, repoName, page)
            .map {
                getSuccess(it.toModel<Event>().apply {
                    value = it.response.map { event -> event.toModel() }
                })
            }
            .onErrorReturn { getFailure("Failed to obtain repo events for $username/$repoName") }
    }

    private fun getEventService() =
        retrofitHelper.getRetrofit(token = prefsHelper.getCachedToken())
            .create(EventService::class.java)

    private fun <T> getSuccess(value: T): EventResult<T> = EventResult.Success(value)

    private fun <T> getFailure(error: String): EventResult<T> = EventResult.Failure(error)
}

sealed class EventResult<T> {
    data class Success<T>(val value: T) : EventResult<T>()
    data class Failure<T>(val error: String) : EventResult<T>()
}