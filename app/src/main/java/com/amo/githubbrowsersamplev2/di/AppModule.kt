package com.amo.githubbrowsersamplev2.di

import android.content.Context
import androidx.room.Room
import com.amo.githubbrowsersamplev2.api.GithubService
import com.amo.githubbrowsersamplev2.db.GithubDb
import com.amo.githubbrowsersamplev2.db.RepoDao
import com.amo.githubbrowsersamplev2.db.UserDao
import com.amo.githubbrowsersamplev2.util.LiveDataCallAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideGithubService(): GithubService {
        return Retrofit.Builder()
            .baseUrl("https://api.github.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(LiveDataCallAdapterFactory())
            .build()
            .create(GithubService::class.java)
    }

    @Singleton
    @Provides
    fun provideDb(@ApplicationContext context: Context): GithubDb {
        return Room.databaseBuilder(context, GithubDb::class.java, "github.db").build()
    }

    @Singleton
    @Provides
    fun provideUserDao(db: GithubDb): UserDao {
        return db.userDao()
    }

    @Singleton
    @Provides
    fun provideRepoDao(db: GithubDb): RepoDao {
        return db.repoDao()
    }

}