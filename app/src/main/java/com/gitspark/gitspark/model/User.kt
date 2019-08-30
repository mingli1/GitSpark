package com.gitspark.gitspark.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.gitspark.gitspark.ui.adapter.Pageable
import com.gitspark.gitspark.ui.adapter.VIEW_TYPE_VIEW

@Entity
open class User(
    var login: String = "",
    @PrimaryKey var userId: Int = 0,
    var avatarUrl: String = "",
    var type: String = "",
    var siteAdmin: Boolean = false,
    var name: String = "",
    var company: String = "",
    var location: String = "",
    var email: String = "",
    var bio: String = "",
    var blogUrl: String = "",
    var numPublicRepos: Int = 0,
    var numPublicGists: Int = 0,
    var followers: Int = 0,
    var following: Int = 0,
    var createdAt: String = "",
    var updatedAt: String = "",
    var hireable: Boolean = false
) : Pageable {

    override fun getViewType() = VIEW_TYPE_VIEW
}