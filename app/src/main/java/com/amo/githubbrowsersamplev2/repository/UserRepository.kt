package com.amo.githubbrowsersamplev2.repository

import androidx.lifecycle.LiveData
import com.amo.githubbrowsersamplev2.AppExecutors
import com.amo.githubbrowsersamplev2.api.ApiResponse
import com.amo.githubbrowsersamplev2.api.GithubService
import com.amo.githubbrowsersamplev2.db.UserDao
import com.amo.githubbrowsersamplev2.vo.Resource
import com.amo.githubbrowsersamplev2.vo.User
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class UserRepository @Inject constructor(
    private val appExecutors: AppExecutors,
    private val userDao: UserDao,
    private val githubService: GithubService
) {

    fun loadUser(login: String): LiveData<Resource<User>> {
        return object : NetworkBoundResource<User, User>(appExecutors) {
            override fun saveCallResult(item: User) {
                userDao.insert(item)
            }

            override fun shouldFetch(data: User?): Boolean {
                return data == null
            }

            override fun loadFromDb(): LiveData<User> {
                return userDao.findByLogin(login)
            }

            override fun createCall(): LiveData<ApiResponse<User>> {
                return githubService.getUser(login)
            }
        }.asLiveData()
    }

}