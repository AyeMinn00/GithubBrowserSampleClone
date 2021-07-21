package com.amo.githubbrowsersamplev2.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.amo.githubbrowsersamplev2.vo.Contributor
import com.amo.githubbrowsersamplev2.vo.Repo
import com.amo.githubbrowsersamplev2.vo.RepoSearchResult
import com.amo.githubbrowsersamplev2.vo.User

/**
 * Main database description.
 */
@Database(
    entities = [
        User::class,
        Repo::class,
        Contributor::class,
        RepoSearchResult::class
    ],
    version = 1,
    exportSchema = false
)
abstract class GithubDb : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun repoDao(): RepoDao
}