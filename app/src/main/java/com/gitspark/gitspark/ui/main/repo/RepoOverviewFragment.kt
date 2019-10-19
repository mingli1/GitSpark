package com.gitspark.gitspark.ui.main.repo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import br.tiagohm.markdownview.css.styles.Github
import com.gitspark.gitspark.R
import com.gitspark.gitspark.api.model.ApiSubscribed
import com.gitspark.gitspark.extension.isVisible
import com.gitspark.gitspark.extension.observe
import com.gitspark.gitspark.extension.setColor
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

class RepoOverviewFragment : BaseFragment<RepoOverviewViewModel>(RepoOverviewViewModel::class.java),
    ConfirmDialogCallback {

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
        setUpListeners()
    }

    override fun observeViewModel() {
        viewModel.viewState.observe(viewLifecycleOwner) { updateView(it) }
        viewModel.updatedRepoData.observe(viewLifecycleOwner) { sharedViewModel.repoData.value = it }
        viewModel.forkButtonAction.observe(viewLifecycleOwner) { showForkConfirmDialog(it) }
        viewModel.navigateToUserListAction.observe(viewLifecycleOwner) { navigateToUserListFragment(it) }
        viewModel.navigateToRepoListAction.observe(viewLifecycleOwner) { navigateToRepoListFragment(it) }
    }

    override fun onPositiveClicked() = viewModel.onForkConfirmClicked()

    override fun onNegativeClicked() {}

    fun notifyWatchingDataRetrieved(watchData: ApiSubscribed) = viewModel.setUserWatching(watchData)

    fun notifyStarringDataRetrieved(starring: Boolean) = viewModel.setUserStarring(starring)

    fun notifyNumWatchersDataRetrieved(numWatchers: Int) = viewModel.setNumWatching(numWatchers)

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

            if (languageColor != -1) {
                language_field.compoundDrawablesRelative[0].setColor(languageColor)
            }
            language_field.isVisible = repoLanguage.isNotEmpty()
            language_field.text = repoLanguage
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
                else getString(R.string.num_watchers_text, numWatchers)
            stars_field.text =
                if (numStars == 1) getString(R.string.num_stars_text_single)
                else getString(R.string.num_stars_text, numStars)
            forks_field.text =
                if (numForks == 1) getString(R.string.num_forks_text_single)
                else getString(R.string.num_forks_text, numForks)

            watch_button.drawable.setColor(context!!.getColor(
                if (userWatching) R.color.colorPrimaryDark else R.color.colorDrawableDefault))
            star_button.drawable.setColor(context!!.getColor(
                if (userStarring) R.color.colorPrimaryDark else R.color.colorDrawableDefault))

            readme_label.isVisible = readmeUrl.isNotEmpty()
            readme_view.isVisible = readmeUrl.isNotEmpty()
            if (readmeUrl.isNotEmpty()) readme_view.loadMarkdownFromUrl(readmeUrl)
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
}