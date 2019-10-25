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

    fun getEvents(username: String): Observable<EventResult<Page<Event>>> {
        return getEventService()
            .getEvents(username)
            .map {
                getSuccess(it.toModel<Event>().apply {
                    value = it.response.map { event -> event.toModel() }
                })
            }
            .onErrorReturn { getFailure("Failed to obtain events for $username") }
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