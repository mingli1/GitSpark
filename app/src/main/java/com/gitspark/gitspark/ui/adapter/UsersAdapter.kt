package com.gitspark.gitspark.ui.adapter

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.gitspark.gitspark.R
import com.gitspark.gitspark.extension.inflate
import com.gitspark.gitspark.extension.isVisible
import com.gitspark.gitspark.extension.loadImage
import com.gitspark.gitspark.model.Loading
import com.gitspark.gitspark.model.User
import kotlinx.android.synthetic.main.user_view.view.*

class UsersAdapter : RecyclerView.Adapter<UsersAdapter.ViewHolder>() {

    private val items = arrayListOf<Pageable>()

    override fun getItemCount() = items.size

    override fun getItemViewType(position: Int) = items[position].getViewType()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(parent.inflate(
            if (viewType == VIEW_TYPE_VIEW) R.layout.user_view else R.layout.loading_view))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        holder.bind(items[position])

    fun addInitialUsers(users: List<User>, isOnlyPage: Boolean) {
        if (users.isEmpty()) return
        items.clear()

        items.addAll(users)
        if (!isOnlyPage) items.add(Loading)
        notifyDataSetChanged()
    }

    fun addUsersOnLoadingComplete(users: List<User>, isLastPage: Boolean) {
        if (items.lastIndex < 0) return
        if (items.lastOrNull()!!.getViewType() != VIEW_TYPE_LOADING) return
        items.removeAt(items.lastIndex)

        items.addAll(users)
        if (!isLastPage) items.add(Loading)
        notifyDataSetChanged()
    }

    inner class ViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {

        fun bind(item: Pageable) {
            if (item is User) {
                with (view) {
                    avatar_image.loadImage(item.avatarUrl)
                    if (item.name.isNotEmpty()) {
                        name_field.text = item.name
                        username_field.text = item.login
                    } else {
                        name_field.text = item.login
                    }
                    bio_field.isVisible = item.bio.isNotEmpty()
                    bio_field.text = item.bio
                    company_field.isVisible = item.company.isNotEmpty()
                    company_field.text = item.company
                    location_field.isVisible = item.location.isNotEmpty()
                    location_field.text = item.location

                    divider.isVisible = item.bio.isNotEmpty() ||
                            item.company.isNotEmpty() ||
                            item.location.isNotEmpty()
                }
            }
        }
    }
}