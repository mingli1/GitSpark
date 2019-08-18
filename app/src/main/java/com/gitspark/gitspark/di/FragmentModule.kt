package com.gitspark.gitspark.di

import com.gitspark.gitspark.ui.main.tab.*
import com.gitspark.gitspark.ui.main.tab.profile.OverviewFragment
import com.gitspark.gitspark.ui.main.tab.profile.ReposFragment
import com.gitspark.gitspark.ui.main.tab.profile.StarsFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
internal abstract class FragmentModule {

    @ContributesAndroidInjector
    internal abstract fun contributesFeedFragment(): FeedFragment

    @ContributesAndroidInjector
    internal abstract fun contributesSearchFragment(): SearchFragment

    @ContributesAndroidInjector
    internal abstract fun contributesPullRequestsFragment(): PullRequestsFragment

    @ContributesAndroidInjector
    internal abstract fun contributesIssuesFragment(): IssuesFragment

    @ContributesAndroidInjector
    internal abstract fun contributesOverviewFragment(): OverviewFragment

    @ContributesAndroidInjector
    internal abstract fun contributesReposFragment(): ReposFragment

    @ContributesAndroidInjector
    internal abstract fun contributesStarsFragment(): StarsFragment
}