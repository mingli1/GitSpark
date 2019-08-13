package com.gitspark.gitspark.ui.livedata

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import io.mockk.MockKAnnotations
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.verify
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class SingleLiveEventTest {

    @Rule @JvmField val liveDataRule = InstantTaskExecutorRule()

    private lateinit var singleLiveEvent: SingleLiveEvent<Int>
    @RelaxedMockK private lateinit var observer: Observer<Int>

    @Before
    fun setup() {
        MockKAnnotations.init(this)
        singleLiveEvent = SingleLiveEvent()
        singleLiveEvent.observeForever(observer)
    }

    @Test
    fun shouldObserveValueChanged() {
        singleLiveEvent.value = 1
        verify { observer.onChanged(1) }
    }
}