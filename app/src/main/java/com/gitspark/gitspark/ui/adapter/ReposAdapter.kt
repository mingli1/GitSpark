package com.gitspark.gitspark.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.cardview.widget.CardView
import com.gitspark.gitspark.R
import com.gitspark.gitspark.extension.isVisible
import com.gitspark.gitspark.extension.setColor
import com.gitspark.gitspark.extension.withSuffix
import com.gitspark.gitspark.helper.LanguageColorHelper
import com.gitspark.gitspark.model.Repo
import kotlinx.android.synthetic.main.repo_view.view.*
import org.threeten.bp.Instant
import org.threeten.bp.LocalDateTime
import org.threeten.bp.format.DateTimeFormatter

private const val MAX_TOPICS_SHOWN = 3

class ReposAdapter(
    private val colorHelper: LanguageColorHelper,
    private val navigator: RepoDetailNavigator
) : PaginationAdapter() {

    override fun bind(item: Pageable, view: View) {
        if (item is Repo) {
            with (view) {
                full_name_field.text = item.fullName
                description_field.text = item.repoDescription

                if (item.repoPushedAt.isNotEmpty()) {
                    val updatedDate = Instant.parse(item.repoPushedAt)
                    val dateTime = LocalDateTime.ofInstant(
                        updatedDate,
                        org.threeten.bp.ZoneOffset.UTC
                    )
                    updated_field.isVisible = true
                    val formatted = DateTimeFormatter.ofPattern("MM-dd-yyyy hh:mm:ss").format(dateTime)
                    updated_field.text = context.getString(R.string.updated_repo, formatted)
                } else updated_field.isVisible = false

                stars_field.text = withSuffix(item.numStars)
                forks_field.text = withSuffix(item.numForks)
                private_label.isVisible = item.isPrivate
                forked_label.isVisible = item.isForked
                archived_label.isVisible = item.archived
                language_field.isVisible = item.repoLanguage.isNotEmpty()
                language_field.text = item.repoLanguage

                topics_container.removeAllViews()
                item.topics.forEachIndexed { index, topic ->
                    if (index < MAX_TOPICS_SHOWN) {
                        val topicView = LayoutInflater.from(context)
                            .inflate(
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

                colorHelper.getColor(item.repoLanguage)?.let {
                    language_field.compoundDrawablesRelative[0].setColor(it)
                }

                repo_card.setOnClickListener { navigator.onRepoSelected(item) }
            }
        }
    }

    override fun getViewHolderId() = R.layout.repo_view
}