package com.amo.githubbrowsersamplev2.ui.test

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import com.amo.githubbrowsersamplev2.api.*
import com.amo.githubbrowsersamplev2.repository.RepoRepository
import com.amo.githubbrowsersamplev2.vo.Repo
import com.amo.githubbrowsersamplev2.vo.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class TestViewModel @Inject constructor(githubService: GithubService) :
    ViewModel() {

    val result = MediatorLiveData<Resource<List<Repo>>>()

    init {
        val apiResponse = githubService.searchRepos("movie")
        result.addSource(apiResponse) { response ->
            result.removeSource(apiResponse)
            when (response) {
                is ApiSuccessResponse -> {
                    result.value = Resource.success(response.body.items)
                }
                is ApiEmptyResponse -> {
                    result.value = Resource.success(null)
                }
                is ApiErrorResponse -> {
                    result.value = Resource.error(response.errorMessage, null)
                }
            }
        }
    }

}