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

class UserAdapter : RecyclerView.Adapter<UserAdapter.ViewHolder>() {

    private val items = arrayListOf<Pageable>()

    override fun getItemCount() = items.size

    override fun getItemViewType(position: Int) = items[position].getViewType()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(parent.inflate(
            if (viewType == VIEW_TYPE_VIEW) R.layout.user_view else R.layout.loading_view))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        holder.bind(items[position])

    fun addUsers(users: List<User>) {
        items.clear()
        notifyItemRangeRemoved(0, if (items.lastIndex < 0) 0 else items.lastIndex)

        items.addAll(users)
        items.add(Loading)
        notifyItemRangeInserted(0, items.size)
    }

    fun addUsersOnLoadingComplete(users: List<User>) {
        if (items.lastIndex < 0) return
        if (items.lastOrNull()!!.getViewType() != VIEW_TYPE_LOADING) return
        items.removeAt(items.lastIndex)
        notifyItemRemoved(items.lastIndex)

        items.addAll(users)
        items.add(Loading)
        notifyItemRangeChanged(items.lastIndex, users.size + 1)
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