package com.amo.githubbrowsersamplev2.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.amo.githubbrowsersamplev2.AppExecutors
import com.amo.githubbrowsersamplev2.api.ApiResponse
import com.amo.githubbrowsersamplev2.api.ApiSuccessResponse
import com.amo.githubbrowsersamplev2.api.GithubService
import com.amo.githubbrowsersamplev2.api.RepoSearchResponse
import com.amo.githubbrowsersamplev2.db.GithubDb
import com.amo.githubbrowsersamplev2.db.RepoDao
import com.amo.githubbrowsersamplev2.util.AbsentLiveData
import com.amo.githubbrowsersamplev2.util.RateLimitter
import com.amo.githubbrowsersamplev2.vo.Contributor
import com.amo.githubbrowsersamplev2.vo.Repo
import com.amo.githubbrowsersamplev2.vo.RepoSearchResult
import com.amo.githubbrowsersamplev2.vo.Resource
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RepoRepository @Inject constructor(
    private val appExecutors: AppExecutors,
    private val db: GithubDb,
    private val repoDao: RepoDao,
    private val githubService: GithubService
) {

    private val repoListRateLimit = RateLimitter<String>(10, TimeUnit.MINUTES)

    fun loadRepos(owner: String): LiveData<Resource<List<Repo>>> {
        return object : NetworkBoundResource<List<Repo>, List<Repo>>(appExecutors) {
            override fun saveCallResult(item: List<Repo>) {
                repoDao.insertRepos(item)
            }

            override fun shouldFetch(data: List<Repo>?): Boolean {
                return data == null || data.isEmpty() || repoListRateLimit.shouldFetch(owner)
            }

            override fun loadFromDb(): LiveData<List<Repo>> {
                return repoDao.loadRepositories(owner)
            }

            override fun createCall(): LiveData<ApiResponse<List<Repo>>> {
                return githubService.getRepos(owner)
            }

            override fun onFetchFailed() {
                repoListRateLimit.reset(owner)
            }
        }.asLiveData()
    }

    fun loadRepos(owner: String, name: String): LiveData<Resource<Repo>> {
        return object : NetworkBoundResource<Repo, Repo>(appExecutors) {
            override fun saveCallResult(item: Repo) {
                repoDao.insert(item)
            }

            override fun shouldFetch(data: Repo?): Boolean {
                return data == null
            }

            override fun loadFromDb(): LiveData<Repo> {
                return repoDao.load(
                    ownerLogin = owner,
                    name = name
                )
            }

            override fun createCall(): LiveData<ApiResponse<Repo>> {
                return githubService.getRepo(owner, name)
            }
        }.asLiveData()
    }

    fun loadContributors(owner: String, name: String): LiveData<Resource<List<Contributor>>> {
        return object : NetworkBoundResource<List<Contributor>, List<Contributor>>(appExecutors) {

            override fun saveCallResult(item: List<Contributor>) {
                item.forEach {
                    it.repoName = name
                    it.repoOwner = owner
                }
                db.runInTransaction {
                    repoDao.createRepoIfNotExists(
                        Repo(
                            id = Repo.UNKNOWN_ID,
                            name = name,
                            fullName = "$owner/$name",
                            description = "",
                            owner = Repo.Owner(owner, null),
                            stars = 0
                        )
                    )
                    repoDao.insertContributors(item)
                }
            }

            override fun shouldFetch(data: List<Contributor>?): Boolean {
                return data == null || data.isEmpty()
            }

            override fun loadFromDb(): LiveData<List<Contributor>> {
                return repoDao.loadContributors(owner, name)
            }

            override fun createCall(): LiveData<ApiResponse<List<Contributor>>> {
                return githubService.getContributors(owner, name)
            }

        }.asLiveData()
    }

    fun searchNextPage(query: String): LiveData<Resource<Boolean>?> {
        val fetchNextSearchPageTask = FetchNextSearchPageTask(
            query = query,
            githubService, db
        )
        appExecutors.networkIO().execute(fetchNextSearchPageTask)
        return fetchNextSearchPageTask.liveData
    }

    fun search(query: String): LiveData<Resource<List<Repo>>> {
        Timber.e("search with $query")
        return object : NetworkBoundResource<List<Repo>, RepoSearchResponse>(appExecutors) {

            override fun saveCallResult(item: RepoSearchResponse) {
                val repoIds = item.items.map { it.id }
                val repoSearchResult = RepoSearchResult(
                    query, repoIds, totalCount = item.total, item.nextPage
                )
                db.runInTransaction {
                    repoDao.insertRepos(item.items)
                    repoDao.insert(repoSearchResult)
                }
            }

            override fun shouldFetch(data: List<Repo>?): Boolean {
                return data == null
            }

            override fun loadFromDb(): LiveData<List<Repo>> {
                return Transformations.switchMap(repoDao.search(query)) { searchData ->
                    if (searchData == null) {
                        AbsentLiveData.create()
                    } else {
                        repoDao.loadOrdered(searchData.repoIds)
                    }
                }
            }

            override fun createCall(): LiveData<ApiResponse<RepoSearchResponse>> {
                return githubService.searchRepos(query)
            }

            override fun processResponse(response: ApiSuccessResponse<RepoSearchResponse>): RepoSearchResponse {
                val body = response.body
                body.nextPage = response.nextPage
                return body
            }

        }.asLiveData()
    }

}


































