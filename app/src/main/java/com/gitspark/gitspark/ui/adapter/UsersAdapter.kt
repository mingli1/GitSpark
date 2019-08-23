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
import com.gitspark.gitspark.ui.main.tab.profile.FollowState
import kotlinx.android.synthetic.main.user_view.view.*

class UsersAdapter : RecyclerView.Adapter<UsersAdapter.ViewHolder>() {

    private val items = arrayListOf<Pageable>()
    private var followState = FollowState.Followers

    override fun getItemCount() = items.size

    override fun getItemViewType(position: Int) = items[position].getViewType()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(parent.inflate(
            if (viewType == VIEW_TYPE_VIEW) R.layout.user_view else R.layout.loading_view))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        holder.bind(items[position])

    fun addInitialUsers(users: List<User>, isOnlyPage: Boolean, followState: FollowState) {
        if (users.isEmpty()) return
        this.followState = followState
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
                    name_field.text = item.login
                    unfollow_button.isVisible = followState == FollowState.Following
                }
            }
        }
    }
}