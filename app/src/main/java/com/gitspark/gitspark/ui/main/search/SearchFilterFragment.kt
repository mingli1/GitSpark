package com.gitspark.gitspark.ui.main.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.gitspark.gitspark.R
import com.gitspark.gitspark.extension.*
import com.gitspark.gitspark.model.Page
import com.gitspark.gitspark.ui.adapter.ImageSpinnerAdapter
import com.gitspark.gitspark.ui.adapter.Pageable
import com.gitspark.gitspark.ui.base.BaseFragment
import com.gitspark.gitspark.ui.main.MainActivity
import kotlinx.android.synthetic.main.fragment_search_filter.*
import kotlinx.android.synthetic.main.full_screen_progress_spinner.*

private val SEARCH_TYPE_DRAWABLES = arrayOf(
    R.drawable.ic_repo,
    R.drawable.ic_person,
    R.drawable.ic_code,
    R.drawable.ic_commit,
    R.drawable.ic_issue,
    R.drawable.ic_pull_request
)

class SearchFilterFragment : BaseFragment<SearchFilterViewModel>(SearchFilterViewModel::class.java) {

    private val sharedViewModel by lazy {
        ViewModelProviders.of(activity!!, viewModelFactory)[SearchSharedViewModel::class.java]
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_search_filter, container, false)
        val toolbar = view.findViewById<Toolbar>(R.id.search_toolbar)

        with (activity as MainActivity) {
            setSupportActionBar(toolbar)
            supportActionBar?.run {
                setDisplayHomeAsUpEnabled(true)
                setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp)
            }
        }

        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val spinnerAdapter= ImageSpinnerAdapter(
            context!!,
            SEARCH_TYPE_DRAWABLES,
            resources.getStringArray(R.array.search_types)
        )
        search_spinner.adapter = spinnerAdapter

        setUpListeners()
    }

    override fun observeViewModel() {
        viewModel.viewState.observe(viewLifecycleOwner) { updateView(it) }
        viewModel.clearAction.observe(viewLifecycleOwner) { clearFields() }
        viewModel.clearMainQueryAction.observe(viewLifecycleOwner) { main_search_edit.clear() }
        viewModel.searchAction.observe(viewLifecycleOwner) { onSearchSuccess(it) }
    }

    private fun updateView(viewState: SearchFilterViewState) {
        with (viewState) {
            loading_indicator.isVisible = loading

            created_on_label.isVisible = currSearch != CODE
            created_on_edit.isVisible = currSearch != CODE

            from_this_user_label.isVisible = currSearch != USERS
            from_this_user_edit.isVisible = currSearch != USERS

            user_full_name_label.isVisible = currSearch == USERS
            full_name_edit.isVisible = currSearch == USERS
            location_label.isVisible = currSearch == USERS
            location_edit.isVisible = currSearch == USERS
            num_followers_label.isVisible = currSearch == USERS
            followers_edit.isVisible = currSearch == USERS
            num_repos_label.isVisible = currSearch == USERS
            num_repos_edit.isVisible = currSearch == USERS

            num_stars_label.isVisible = currSearch == REPOS
            stars_edit.isVisible = currSearch == REPOS
            num_forks_label.isVisible = currSearch == REPOS
            forks_edit.isVisible = currSearch == REPOS

            updated_on_label.isVisible = currSearch != USERS && currSearch != COMMITS && currSearch != CODE
            updated_on_edit.isVisible = currSearch != USERS && currSearch != COMMITS && currSearch != CODE

            file_extension_label.isVisible = currSearch == CODE
            file_extension_edit.isVisible = currSearch == CODE
            file_size_label.isVisible = currSearch == CODE
            file_size_edit.isVisible = currSearch == CODE

            repo_label.isVisible = currSearch == COMMITS
            repo_edit.isVisible = currSearch == COMMITS

            forked_checkbox.isVisible = currSearch == REPOS || currSearch == CODE
            open_checkbox.isVisible = currSearch == ISSUES || currSearch == PULL_REQUESTS

            num_comments_label.isVisible = currSearch == ISSUES || currSearch == PULL_REQUESTS
            num_comments_edit.isVisible = currSearch == ISSUES || currSearch == PULL_REQUESTS
            labels_label.isVisible = currSearch == ISSUES || currSearch == PULL_REQUESTS
            labels_edit.isVisible = currSearch == ISSUES || currSearch == PULL_REQUESTS

            main_search_edit.hint = getString(when (currSearch) {
                REPOS -> R.string.repo_search_hint
                USERS -> R.string.user_search_hint
                CODE -> R.string.code_search_hint
                COMMITS -> R.string.commit_search_hint
                ISSUES -> R.string.issues_search_hint
                else -> R.string.pr_search_hint
            })
        }
    }

    private fun setUpListeners() {
        search_spinner.onItemSelected {
            resetAllFields()
            viewModel.onSearchTypeSelected(it)
        }
        clear_button.setOnClickListener { viewModel.onClearFieldsButtonClicked() }
        search_bar_clear_button.setOnClickListener { viewModel.onMainQueryClearButtonClicked() }
        search_filter_button.setOnClickListener {
            viewModel.onSearch(
                mainQuery = main_search_edit.getStringTrimmed(),
                createdOn = created_on_edit.getStringTrimmed(),
                language = language_edit.getStringTrimmed(),
                fromThisUser = from_this_user_edit.getStringTrimmed(),
                fullName = full_name_edit.getStringTrimmed(),
                location = location_edit.getStringTrimmed(),
                numFollowers = followers_edit.getStringTrimmed(),
                numRepos = num_repos_edit.getStringTrimmed(),
                numStars = stars_edit.getStringTrimmed(),
                numForks = forks_edit.getStringTrimmed(),
                updatedOn = updated_on_edit.getStringTrimmed(),
                fileExtension = file_extension_edit.getStringTrimmed(),
                fileSize = file_size_edit.getStringTrimmed(),
                repoFullName = full_name_edit.getStringTrimmed(),
                includeForked = forked_checkbox.isChecked,
                isOpen = open_checkbox.isChecked,
                numComments = num_comments_edit.getStringTrimmed(),
                labels = labels_edit.getStringTrimmed()
            )
        }
    }

    private fun clearFields() {
        created_on_edit.clear()
        language_edit.clear()
        resetAllFields()
    }

    private fun resetAllFields() {
        from_this_user_edit.clear()
        full_name_edit.clear()
        location_edit.clear()
        followers_edit.clear()
        num_repos_edit.clear()
        stars_edit.clear()
        forks_edit.clear()
        updated_on_edit.clear()
        file_extension_edit.clear()
        file_size_edit.clear()
        repo_edit.clear()
        forked_checkbox.isChecked = true
        open_checkbox.isChecked = true
        num_comments_edit.clear()
        labels_edit.clear()
    }

    private fun onSearchSuccess(results: Page<Pageable>) {
        sharedViewModel.searchResults.value = results
        findNavController().navigateUp()
    }
}