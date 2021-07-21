package com.amo.githubbrowsersamplev2.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.amo.githubbrowsersamplev2.api.*
import com.amo.githubbrowsersamplev2.db.GithubDb
import com.amo.githubbrowsersamplev2.vo.RepoSearchResult
import com.amo.githubbrowsersamplev2.vo.Resource
import timber.log.Timber
import java.io.IOException

class FetchNextSearchPageTask constructor(
    private val query: String,
    private val githubService: GithubService,
    private val db: GithubDb
) : Runnable {

    private val _liveData = MutableLiveData<Resource<Boolean>?>()
    val liveData: LiveData<Resource<Boolean>?> = _liveData

    override fun run() {
        Timber.e("run")
        val currentRepoSearchResult = db.repoDao().findSearchResult(query)
        if (currentRepoSearchResult == null) {
            _liveData.postValue(null)
            return
        }
        val nextPage = currentRepoSearchResult.next
        if (nextPage == null) {
            _liveData.postValue(Resource.success(false))
            return
        }
        val newValue = try {
            val response = githubService.searchRepos(query, nextPage).execute()
            when (val apiResponse = ApiResponse.create(response)) {
                is ApiSuccessResponse -> {
                    // we merge all repo ids into 1 list so that it is easier to fetch the result list
                    val ids = arrayListOf<Int>()
                    ids.addAll(currentRepoSearchResult.repoIds)

                    ids.addAll(apiResponse.body.items.map { it.id })
                    val merged = RepoSearchResult(
                        query, ids,
                        apiResponse.body.total, apiResponse.nextPage
                    )
                    // results livedata from SearchViewModel will get new updates
                    db.runInTransaction {
                        db.repoDao().insert(merged)
                        db.repoDao().insertRepos(apiResponse.body.items)
                    }
                    Resource.success(apiResponse.nextPage != null)

                }
                is ApiEmptyResponse -> {
                    Resource.success(false)
                }
                is ApiErrorResponse -> {
                    Resource.error(apiResponse.errorMessage, true)
                }
            }
        } catch (e: IOException) {
            Resource.error(e.message!!, true)
        }
        Timber.e("new Value is %s" , newValue)
        _liveData.postValue(newValue)
    }

    protected fun finalize() {
        Timber.e("finalize")
    }

}