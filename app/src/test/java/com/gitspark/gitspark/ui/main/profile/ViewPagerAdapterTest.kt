package com.gitspark.gitspark.ui.main.profile

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.gitspark.gitspark.ui.adapter.ViewPagerAdapter
import io.mockk.MockKAnnotations
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test

class ViewPagerAdapterTest {

    private lateinit var viewPagerAdapter: ViewPagerAdapter
    @MockK private lateinit var fragmentManager: FragmentManager
    @RelaxedMockK private lateinit var fragment: Fragment

    @Before
    fun setup() {
        MockKAnnotations.init(this)
        viewPagerAdapter = ViewPagerAdapter(fragmentManager)
    }

    @Test
    fun shouldGetFragmentData() {
        viewPagerAdapter.addFragment(fragment, "FRAGMENT")

        assertThat(viewPagerAdapter.count).isEqualTo(1)
        assertThat(viewPagerAdapter.getItem(0)).isEqualTo(fragment)
        assertThat(viewPagerAdapter.getPageTitle(0)).isEqualTo("FRAGMENT")
    }
}