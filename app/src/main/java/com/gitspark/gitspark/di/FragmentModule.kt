package com.gitspark.gitspark.di

import com.gitspark.gitspark.ui.main.tab.*
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
internal abstract class FragmentModule {

    @ContributesAndroidInjector
    internal abstract fun contributesFeedFragment(): FeedFragment

    @ContributesAndroidInjector
    internal abstract fun contributesProfileFragment(): ProfileFragment

    @ContributesAndroidInjector
    internal abstract fun contributesSearchFragment(): SearchFragment

    @ContributesAndroidInjector
    internal abstract fun contributesPullRequestsFragment(): PullRequestsFragment

    @ContributesAndroidInjector
    internal abstract fun contributesIssuesFragment(): IssuesFragment
}