package com.gitspark.gitspark.ui.main.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.gitspark.gitspark.R
import com.gitspark.gitspark.extension.*
import com.gitspark.gitspark.model.Page
import com.gitspark.gitspark.model.SearchCriteria
import com.gitspark.gitspark.ui.adapter.ImageSpinnerAdapter
import com.gitspark.gitspark.ui.adapter.Pageable
import com.gitspark.gitspark.ui.base.BaseFragment
import com.gitspark.gitspark.ui.main.MainActivity
import com.squareup.moshi.JsonAdapter
import kotlinx.android.synthetic.main.fragment_search_filter.*
import kotlinx.android.synthetic.main.full_screen_progress_spinner.*
import javax.inject.Inject

private val SEARCH_TYPE_DRAWABLES = arrayOf(
    R.drawable.ic_repo,
    R.drawable.ic_person,
    R.drawable.ic_code,
    R.drawable.ic_commit,
    R.drawable.ic_issue,
    R.drawable.ic_pull_request
)

class SearchFilterFragment : BaseFragment<SearchFilterViewModel>(SearchFilterViewModel::class.java) {

    @Inject lateinit var scJsonAdapter: JsonAdapter<SearchCriteria>

    private var setInitial = false
    private val sharedViewModel by lazy {
        ViewModelProviders.of(activity!!, viewModelFactory)[SearchSharedViewModel::class.java]
    }
    private lateinit var sortAdapter: ArrayAdapter<String>

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

        sortAdapter = ArrayAdapter(context!!, android.R.layout.simple_spinner_item)
        sortAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        sort_spinner.adapter = sortAdapter

        setInitialState()
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

            forked_label.isVisible = currSearch == REPOS || currSearch == CODE
            forked_spinner.isVisible = currSearch == REPOS || currSearch == CODE
            state_label.isVisible = currSearch == ISSUES || currSearch == PULL_REQUESTS
            state_spinner.isVisible = currSearch == ISSUES || currSearch == PULL_REQUESTS

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
            if (!setInitial) {
                resetAllFields()
                setSortSpinnerItems(it)
            }
            setInitial = false
            viewModel.onSearchTypeSelected(it)
        }
        clear_button.setOnClickListener { viewModel.onClearFieldsButtonClicked() }
        search_bar_clear_button.setOnClickListener { viewModel.onMainQueryClearButtonClicked() }
        search_filter_button.setOnClickListener {
            viewModel.onSearch(
                sort = sort_spinner.selectedItem.toString(),
                order = order_spinner.selectedItem.toString(),
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
                fork = when (forked_spinner.selectedItem.toString()) {
                    "forked and not forked" -> "true"
                    "forked only" -> "only"
                    else -> "false"
                },
                state = when (state_spinner.selectedItem.toString()) {
                    "open and closed" -> ""
                    "open only" -> "open"
                    else -> "closed"
                },
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
        forked_spinner.setSelection(0)
        state_spinner.setSelection(0)
        num_comments_edit.clear()
        labels_edit.clear()
        sort_spinner.setSelection(0)
        order_spinner.setSelection(0)
    }

    private fun setInitialState() {
        arguments?.let { args ->
            search_spinner.onItemSelectedListener = null
            setInitial = true
            scJsonAdapter.fromJson(args.getString(BUNDLE_SEARCH_CRITERIA) ?: "")?.let {
                viewModel.existingSearchCriteria = it

                setSortSpinnerItems(it.type)
                search_spinner.setSelection(it.type)
                sort_spinner.setSelection(sortAdapter.getPosition(it.sort))
                order_spinner.setSelection(if (it.order == "desc") 0 else 1)
                main_search_edit.setText(it.mainQuery)
                created_on_edit.setText(it.createdOn)
                language_edit.setText(it.language)
                from_this_user_edit.setText(it.fromThisUser)
                full_name_edit.setText(it.fullName)
                location_edit.setText(it.location)
                followers_edit.setText(it.numFollowers)
                num_repos_edit.setText(it.numRepos)
                stars_edit.setText(it.numStars)
                forks_edit.setText(it.numForks)
                updated_on_edit.setText(it.updatedOn)
                file_extension_edit.setText(it.fileExtension)
                file_size_edit.setText(it.fileSize)
                repo_edit.setText(it.repoFullName)
                forked_spinner.setSelection(when (it.fork) {
                    "true" -> 0
                    "only" -> 1
                    else -> 2
                })
                state_spinner.setSelection(when (it.state) {
                    "" -> 0
                    "open" -> 1
                    else -> 2
                })
                num_comments_edit.setText(it.numComments)
                labels_edit.setText(it.labels)
            }
        }
    }

    private fun setSortSpinnerItems(type: Int) {
        sortAdapter.clear()
        when (type) {
            REPOS -> sortAdapter.addAll(resources.getStringArray(R.array.repo_sort).toList())
            USERS -> sortAdapter.addAll(resources.getStringArray(R.array.user_sort).toList())
            COMMITS -> sortAdapter.addAll(resources.getStringArray(R.array.commit_sort).toList())
            CODE -> sortAdapter.addAll(resources.getStringArray(R.array.code_sort).toList())
            else -> sortAdapter.addAll(resources.getStringArray(R.array.issue_sort).toList())
        }
    }

    private fun onSearchSuccess(results: Pair<SearchCriteria, Page<Pageable>>) {
        sharedViewModel.searchResults.value = results
        findNavController().navigateUp()
    }
}