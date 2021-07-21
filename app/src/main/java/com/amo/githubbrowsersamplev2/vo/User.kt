package com.amo.githubbrowsersamplev2.vo

import androidx.room.Entity
import com.google.gson.annotations.SerializedName

@Entity(primaryKeys = ["login"])
data class User(
    val login: String,
    @SerializedName("avatar_url")
    val avatarUrl: String?,
    val name: String?,
    val company: String?,
    @SerializedName("repos_url")
    val reposUrl: String?,
    val blog: String?
)
