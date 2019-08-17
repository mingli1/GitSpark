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
import com.gitspark.gitspark.model.Repo
import kotlinx.android.synthetic.main.repo_view.view.*
import org.threeten.bp.Instant
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneOffset
import org.threeten.bp.format.DateTimeFormatter

private const val MAX_TOPICS_SHOWN = 3

class ReposAdapter : RecyclerView.Adapter<ReposAdapter.ViewHolder>() {

    private var repos = listOf<Repo>()

    override fun getItemCount() = repos.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(parent.inflate(R.layout.repo_view))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(repos[position])
    }

    fun setData(repos: List<Repo>) {
        this.repos = repos
        notifyDataSetChanged()
    }

    inner class ViewHolder(private val repoView: View) : RecyclerView.ViewHolder(repoView) {

        @SuppressLint("InflateParams")
        fun bind(repo: Repo) {
            with (repoView) {
                full_name_field.text = repo.fullName
                description_field.text = repo.repoDescription

                if (repo.repoCreatedAt.isNotEmpty()) {
                    val createdDate = Instant.parse(repo.repoCreatedAt)
                    val dateTime = LocalDateTime.ofInstant(createdDate, ZoneOffset.UTC)
                    updated_field.isVisible = true
                    val formatted = DateTimeFormatter.ofPattern("MM-dd-yyyy hh:mm:ss").format(dateTime)
                    updated_field.text = context.getString(R.string.updated_repo, formatted)
                }
                else updated_field.isVisible = false

                stars_field.text = repo.numStars.toString()
                forks_field.text = repo.numForks.toString()
                language_field.text = repo.repoLanguage

                topics_container.removeAllViews()
                listOf("android", "kotlin", "java").forEach { topic ->
                    val topicView = LayoutInflater.from(context).inflate(R.layout.topics_view, topics_container, false)
                    ((topicView as CardView).getChildAt(0) as TextView).apply {
                        text = topic
                    }
                    topics_container.addView(topicView)
                }
            }
        }
    }
}