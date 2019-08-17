package com.gitspark.gitspark.ui.main.tab.profile

import android.view.View
import android.view.ViewGroup
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
            }
        }
    }
}