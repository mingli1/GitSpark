package com.gitspark.gitspark.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.gitspark.gitspark.ui.adapter.Pageable
import com.gitspark.gitspark.ui.adapter.VIEW_TYPE_VIEW

const val PREFERENCES_LOGIN = "PREFERENCES_LOGIN"

@Entity
open class User(
    var login: String = "",
    @PrimaryKey var userId: Long = 0,
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
    var hireable: Boolean = false,
    var contributions: Int = 0
) : Pageable {

    override fun getViewType() = VIEW_TYPE_VIEW

    override fun areItemsTheSame(other: Pageable): Boolean {
        val user = other as? User ?: return false
        return login == user.login &&
                userId == user.userId &&
                avatarUrl == user.avatarUrl &&
                type == user.type &&
                siteAdmin == user.siteAdmin &&
                name == user.name &&
                company == user.company &&
                location == user.location &&
                email == user.email &&
                bio == user.bio &&
                blogUrl == user.blogUrl &&
                numPublicRepos == user.numPublicRepos &&
                numPublicGists == user.numPublicGists &&
                followers == user.followers &&
                following == user.following &&
                createdAt == user.createdAt &&
                updatedAt == user.updatedAt &&
                hireable == user.hireable &&
                contributions == user.contributions
    }
}