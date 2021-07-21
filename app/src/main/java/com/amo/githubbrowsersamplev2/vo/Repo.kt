package com.amo.githubbrowsersamplev2.vo

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Index
import com.google.gson.annotations.SerializedName


@Entity(
    indices = [
        Index("id"),
        Index("owner_login")
    ],
    primaryKeys = ["name", "owner_login"]
)
data class Repo(
    val id: Int,
    val name: String,
    @SerializedName("full_name")
    val fullName: String,
    val description: String?,
    @Embedded(prefix = "owner_")
    val owner: Owner,
    @SerializedName("stargazers_count")
    val stars: Int
) {

    data class Owner(
        val login: String,
        val url: String?
    )

    companion object {
        const val UNKNOWN_ID = -1
    }

}