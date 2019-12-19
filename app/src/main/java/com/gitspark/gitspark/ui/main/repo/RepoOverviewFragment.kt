package com.gitspark.gitspark.ui.main.repo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.VERTICAL
import br.tiagohm.markdownview.css.styles.Github
import com.gitspark.gitspark.R
import com.gitspark.gitspark.api.model.ApiSubscribed
import com.gitspark.gitspark.extension.isVisible
import com.gitspark.gitspark.extension.observe
import com.gitspark.gitspark.extension.setColor
import com.gitspark.gitspark.extension.withSuffix
import com.gitspark.gitspark.helper.LanguageColorHelper
import com.gitspark.gitspark.ui.adapter.LanguageAdapter
import com.gitspark.gitspark.ui.base.BaseFragment
import com.gitspark.gitspark.ui.dialog.ConfirmDialog
import com.gitspark.gitspark.ui.dialog.ConfirmDialogCallback
import com.gitspark.gitspark.ui.main.shared.*
import kotlinx.android.synthetic.main.fragment_repo_overview.*
import kotlinx.android.synthetic.main.fragment_repo_overview.archived_label
import kotlinx.android.synthetic.main.fragment_repo_overview.forked_label
import kotlinx.android.synthetic.main.fragment_repo_overview.forks_field
import kotlinx.android.synthetic.main.fragment_repo_overview.language_field
import kotlinx.android.synthetic.main.fragment_repo_overview.private_label
import kotlinx.android.synthetic.main.fragment_repo_overview.stars_field
import kotlinx.android.synthetic.main.fragment_repo_overview.topics_container
import kotlinx.android.synthetic.main.fragment_repo_overview.updated_field
import kotlinx.android.synthetic.main.full_screen_progress_spinner.*
import java.util.*
import javax.inject.Inject

class RepoOverviewFragment : BaseFragment<RepoOverviewViewModel>(RepoOverviewViewModel::class.java),
    ConfirmDialogCallback {

    @Inject lateinit var colorHelper: LanguageColorHelper
    private lateinit var languageAdapter: LanguageAdapter
    private lateinit var layoutManager: LinearLayoutManager
    private var rmUrl = ""

    private val sharedViewModel by lazy {
        ViewModelProviders.of(activity!!, viewModelFactory)[RepoDetailSharedViewModel::class.java]
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_repo_overview, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.loadRepo((parentFragment as RepoDataCallback).getData())
        readme_view.addStyleSheet(Github())

        layoutManager = LinearLayoutManager(context, VERTICAL, false)
        language_breakdown.setHasFixedSize(true)
        language_breakdown.layoutManager = layoutManager
        languageAdapter = LanguageAdapter(colorHelper)
        if (language_breakdown.adapter == null) language_breakdown.adapter = languageAdapter

        setUpListeners()
    }

    override fun observeViewModel() {
        viewModel.viewState.observe(viewLifecycleOwner) { updateView(it) }
        viewModel.updatedRepoData.observe(viewLifecycleOwner) { sharedViewModel.repoData.value = it }
        viewModel.forkButtonAction.observe(viewLifecycleOwner) { showForkConfirmDialog(it) }
        viewModel.navigateToUserListAction.observe(viewLifecycleOwner) { navigateToUserListFragment(it) }
        viewModel.navigateToRepoListAction.observe(viewLifecycleOwner) { navigateToRepoListFragment(it) }
        viewModel.navigateToEventListAction.observe(viewLifecycleOwner) { navigateToEventListFragment(it) }
        viewModel.navigateToIssuesAction.observe(viewLifecycleOwner) { navigateToIssuesListFragment(it, true) }
        viewModel.navigateToPrAction.observe(viewLifecycleOwner) { navigateToIssuesListFragment(it, false) }
    }

    override fun onPositiveClicked() = viewModel.onForkConfirmClicked()

    override fun onNegativeClicked() {}

    override fun onDestroyView() {
        super.onDestroyView()
        language_breakdown.adapter = null
        rmUrl = ""
    }

    fun notifyWatchingDataRetrieved(watchData: ApiSubscribed) = viewModel.setUserWatching(watchData)

    fun notifyStarringDataRetrieved(starring: Boolean) = viewModel.setUserStarring(starring)

    fun notifyNumWatchersDataRetrieved(numWatchers: Int) = viewModel.setNumWatching(numWatchers)

    fun notifyLanguagesDataRetrieved(lang: SortedMap<String, Int>) = viewModel.setLanguages(lang)

    private fun updateView(viewState: RepoOverviewViewState) {
        with (viewState) {
            loading_indicator.isVisible = loading

            repo_name_field.text = repoName
            repo_description_field.text = repoDescription
            repo_description_field.isVisible = repoDescription.isNotEmpty()
            updated_field.isVisible = updatedText.isNotEmpty()
            updated_field.text = getString(R.string.updated_repo, updatedText)

            private_label.isVisible = isPrivate
            forked_label.isVisible = isForked
            archived_label.isVisible = isArchived

            language_field.isVisible = repoLanguage.isNotEmpty()
            license_field.isVisible = licenseText.isNotEmpty()
            license_field.text = licenseText

            if (topics.isNotEmpty()) {
                topics_container.removeAllViews()
                topics.forEach { topic ->
                    val topicView = LayoutInflater.from(context).inflate(
                        R.layout.topics_view,
                        topics_container,
                        false
                    )
                    ((topicView as CardView).getChildAt(0) as TextView).apply {
                        text = topic
                    }
                    topics_container.addView(topicView)
                }
            }
            topics_container.isVisible = topics.isNotEmpty()

            watching_field.text =
                if (numWatchers == 1) getString(R.string.num_watchers_text_single)
                else getString(R.string.num_watchers_text, withSuffix(numWatchers))
            stars_field.text =
                if (numStars == 1) getString(R.string.num_stars_text_single)
                else getString(R.string.num_stars_text, withSuffix(numStars))
            forks_field.text =
                if (numForks == 1) getString(R.string.num_forks_text_single)
                else getString(R.string.num_forks_text, withSuffix(numForks))

            watch_button.drawable.setColor(context!!.getColor(
                if (userWatching) R.color.colorPrimaryDark else R.color.colorDrawableDefault))
            star_button.drawable.setColor(context!!.getColor(
                if (userStarring) R.color.colorPrimaryDark else R.color.colorDrawableDefault))

            readme_label.isVisible = readmeUrl.isNotEmpty()
            readme_view.isVisible = readmeUrl.isNotEmpty()
            if (readmeUrl.isNotEmpty() && readmeUrl != rmUrl) {
                rmUrl = readmeUrl
                readme_view.loadMarkdownFromUrl(readmeUrl)
            }

            languageAdapter.setContent(languages)
            language_button.setImageResource(if (langDetailsShown)
                R.drawable.ic_expand_less else R.drawable.ic_expand_more)
            language_breakdown.isVisible = langDetailsShown

            if (langDetailsShown) {
                language_field.text = getString(R.string.lang_breakdown)
                language_field.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, 0)
            } else {
                language_field.text = repoLanguage
                language_field.setCompoundDrawablesRelativeWithIntrinsicBounds(
                    resources.getDrawable(R.drawable.ic_circle, null),
                    null,
                    null,
                    null
                )
                if (languageColor != -1) {
                    language_field.compoundDrawablesRelative[0].setColor(languageColor)
                }
            }
        }
    }

    private fun setUpListeners() {
        watch_button.setOnClickListener { viewModel.onWatchButtonClicked() }
        star_button.setOnClickListener { viewModel.onStarButtonClicked() }
        fork_button.setOnClickListener { viewModel.onForkButtonClicked() }

        watching_field.setOnClickListener {
            viewModel.onUserListClicked(getString(R.string.watchers_title), UserListType.Watchers)
        }
        stars_field.setOnClickListener {
            viewModel.onUserListClicked(getString(R.string.stargazers_title), UserListType.Stargazers)
        }
        forks_field.setOnClickListener {
            viewModel.onRepoListClicked(getString(R.string.forks_title), RepoListType.Forks)
        }
        contributors_field.setOnClickListener {
            viewModel.onUserListClicked(getString(R.string.contributors_button_text), UserListType.Contributors)
        }
        activity_field.setOnClickListener {
            viewModel.onActivityButtonClicked(getString(R.string.repo_events_title))
        }
        language_button.setOnClickListener { viewModel.onLanguageButtonClicked() }
        issues_field.setOnClickListener { viewModel.onIssuesClicked() }
        pull_requests_field.setOnClickListener { viewModel.onPullRequestsClicked() }
    }

    private fun showForkConfirmDialog(name: String) {
        ConfirmDialog.newInstance(
            getString(R.string.fork_confirm_title, name),
            getString(R.string.fork_confirm_message)
        ).show(childFragmentManager, null)
    }

    private fun navigateToUserListFragment(triple: Triple<String, UserListType, String>) {
        val data = Bundle().apply {
            putString(BUNDLE_TITLE, triple.first)
            putSerializable(BUNDLE_USER_LIST_TYPE, triple.second)
            putString(BUNDLE_ARGUMENTS, triple.third)
        }
        findNavController().navigate(
            R.id.action_repo_detail_fragment_to_user_list_fragment,
            data
        )
    }

    private fun navigateToRepoListFragment(triple: Triple<String, RepoListType, String>) {
        val data = Bundle().apply {
            putString(BUNDLE_TITLE, triple.first)
            putSerializable(BUNDLE_REPO_LIST_TYPE, triple.second)
            putString(BUNDLE_ARGUMENTS, triple.third)
        }
        findNavController().navigate(
            R.id.action_repo_detail_fragment_to_repo_list_fragment,
            data
        )
    }

    private fun navigateToEventListFragment(triple: Triple<String, EventListType, String>) {
        val data = Bundle().apply {
            putString(BUNDLE_TITLE, triple.first)
            putSerializable(BUNDLE_EVENT_LIST_TYPE, triple.second)
            putString(BUNDLE_ARGUMENTS, triple.third)
        }
        findNavController().navigate(
            R.id.action_repo_detail_fragment_to_event_list_fragment,
            data
        )
    }

    private fun navigateToIssuesListFragment(pair: Pair<String, String>, issue: Boolean) {
        val data = Bundle().apply {
            putString(BUNDLE_ISSUE_LIST_TYPE, "list")
            putString(BUNDLE_TITLE, pair.first)
            putString(BUNDLE_ISSUE_TYPE, if (issue) "$REPO_ISSUE_Q${pair.second}" else "$REPO_PR_Q${pair.second}")
        }
        findNavController().navigate(
            R.id.action_repo_detail_fragment_to_issues_list_fragment,
            data
        )
    }
}