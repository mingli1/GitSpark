package com.gitspark.gitspark.ui.main.repo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import br.tiagohm.markdownview.css.styles.Github
import com.gitspark.gitspark.R
import com.gitspark.gitspark.extension.isVisible
import com.gitspark.gitspark.extension.observe
import com.gitspark.gitspark.ui.base.BaseFragment
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

private const val MAX_TOPICS_SHOWN = 4

class RepoOverviewFragment : BaseFragment<RepoOverviewViewModel>(RepoOverviewViewModel::class.java) {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_repo_overview, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.loadRepo((parentFragment as RepoDataCallback).getData())
        readme_view.addStyleSheet(Github())
    }

    override fun observeViewModel() {
        viewModel.viewState.observe(viewLifecycleOwner) { updateView(it) }
    }

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
                language_field.compoundDrawablesRelative[0].apply {
                    mutate()
                    setTint(languageColor)
                }
            }
            language_field.isVisible = repoLanguage.isNotEmpty()
            language_field.text = repoLanguage
            license_field.isVisible = licenseText.isNotEmpty()
            license_field.text = licenseText

            if (topics.isNotEmpty()) {
                topics_container.removeAllViews()
                topics.forEachIndexed { index, topic ->
                    if (index < MAX_TOPICS_SHOWN) {
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
            }
            topics_container.isVisible = topics.isNotEmpty()

            watching_field.text = getString(R.string.num_watchers_text, numWatchers)
            stars_field.text = getString(R.string.num_stars_text, numStars)
            forks_field.text = getString(R.string.num_forks_text, numForks)

            readme_label.isVisible = readmeUrl.isNotEmpty()
            readme_view.isVisible = readmeUrl.isNotEmpty()
            if (readmeUrl.isNotEmpty()) readme_view.loadMarkdownFromUrl(readmeUrl)
        }
    }
}