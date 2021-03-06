package com.gitspark.gitspark.api.model

const val VISIBILITY_ALL = "all"
const val VISIBILITY_PUBLIC = "public"
const val VISIBILITY_PRIVATE = "private"

const val SORT_CREATED = "created"
const val SORT_UPDATED = "updated"
const val SORT_PUSHED = "pushed"
const val SORT_FULL_NAME = "full_name"

private const val DEFAULT_AFFILIATION = "owner,collaborator,organization_member"

data class ApiAuthRepoRequest(
    val visibility: String = VISIBILITY_ALL,
    var affiliation: String = DEFAULT_AFFILIATION,
    val sort: String = SORT_PUSHED
) {

    fun setAffiliation(
        owner: Boolean = false,
        collaborator: Boolean = false,
        orgMember: Boolean = false
    ) {
        var aff = ""
        if (owner) aff += "owner,"
        if (collaborator) aff += "collaborator,"
        if (orgMember) aff += "organization_member,"

        aff = if (aff.isEmpty()) DEFAULT_AFFILIATION else aff.substring(0, aff.length - 1)

        affiliation = aff
    }
}