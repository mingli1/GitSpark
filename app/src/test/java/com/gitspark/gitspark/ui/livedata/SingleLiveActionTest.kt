package com.gitspark.gitspark.ui.livedata

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class SingleLiveActionTest {

    @Rule @JvmField val liveDataRule = InstantTaskExecutorRule()
    private lateinit var singleLiveAction: SingleLiveAction

    @Before
    fun setup() {
        singleLiveAction = SingleLiveAction()
    }

    @Test
    fun shouldCallAction() {
        assertThat(singleLiveAction.value).isNull()
        singleLiveAction.call()
        assertThat(singleLiveAction.value).isNotNull
    }
}