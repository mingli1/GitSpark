package com.gitspark.gitspark.ui.main.tab.profile

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.gitspark.gitspark.R
import com.gitspark.gitspark.extension.inflate
import com.gitspark.gitspark.extension.isVisible
import com.gitspark.gitspark.extension.withSuffix
import com.gitspark.gitspark.helper.LanguageColorHelper
import com.gitspark.gitspark.model.Repo
import kotlinx.android.synthetic.main.repo_view.view.*
import org.threeten.bp.Instant
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneOffset
import org.threeten.bp.format.DateTimeFormatter

private const val MAX_TOPICS_SHOWN = 3

class ReposAdapter(
    private val colorHelper: LanguageColorHelper
) : RecyclerView.Adapter<ReposAdapter.ViewHolder>() {

    private var repos = listOf<Repo>()

    override fun getItemCount() = repos.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(parent.inflate(R.layout.repo_view))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        holder.bind(repos[position])

    fun setData(repos: List<Repo>) {
        if (this.repos != repos)
        this.repos = repos
        notifyDataSetChanged()
    }

    inner class ViewHolder(private val repoView: View) : RecyclerView.ViewHolder(repoView) {

        @SuppressLint("InflateParams")
        fun bind(repo: Repo) {
            with (repoView) {
                full_name_field.text = repo.fullName
                description_field.text = repo.repoDescription

                if (repo.repoPushedAt.isNotEmpty()) {
                    val updatedDate = Instant.parse(repo.repoPushedAt)
                    val dateTime = LocalDateTime.ofInstant(updatedDate, ZoneOffset.UTC)
                    updated_field.isVisible = true
                    val formatted = DateTimeFormatter.ofPattern("MM-dd-yyyy hh:mm:ss").format(dateTime)
                    updated_field.text = context.getString(R.string.updated_repo, formatted)
                }
                else updated_field.isVisible = false

                stars_field.text = withSuffix(repo.numStars)
                forks_field.text = withSuffix(repo.numForks)
                private_label.isVisible = repo.isPrivate
                forked_label.isVisible = repo.isForked
                language_field.text = repo.repoLanguage

                topics_container.removeAllViews()
                repo.topics.forEachIndexed { index, topic ->
                    if (index < MAX_TOPICS_SHOWN) {
                        val topicView = LayoutInflater.from(context)
                            .inflate(R.layout.topics_view, topics_container, false)
                        ((topicView as CardView).getChildAt(0) as TextView).apply {
                            text = topic
                        }
                        topics_container.addView(topicView)
                    }
                }

                colorHelper.getColor(repo.repoLanguage)?.let {
                    language_field.compoundDrawablesRelative[0].apply {
                        mutate()
                        setTint(it)
                    }
                }

                if (repo.starred) {
                    stars_field.compoundDrawablesRelative[0].apply {
                        mutate()
                        setTint(context.getColor(R.color.colorYellow))
                    }
                }
            }
        }
    }
}