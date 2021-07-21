package com.amo.githubbrowsersamplev2.ui.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.amo.githubbrowsersamplev2.repository.RepoRepository
import com.amo.githubbrowsersamplev2.vo.Resource
import com.amo.githubbrowsersamplev2.vo.Status
import timber.log.Timber

class LoadMoreState(val isRunning: Boolean, val errorMessage: String?) {
    private var handledError = false
    val errorMessageIfNotHandled: String?
        get() {
            if (handledError) {
                return null
            }
            handledError = true
            return errorMessage
        }

    override fun toString(): String {
        return "isRunning is $isRunning and errorMessage is $errorMessage"
    }
}

class NextPageHandler(private val repository: RepoRepository) : Observer<Resource<Boolean>?> {
    private var nextPageLiveData: LiveData<Resource<Boolean>?>? = null
    val loadMoreState = MutableLiveData<LoadMoreState>()
    private var query: String? = null
    private var _hasMore: Boolean = false
    val hasMore
        get() = _hasMore

    init {
        Timber.e("init")
        reset()
    }

    fun queryNextPage(query: String) {
        Timber.e("queryNextPage")
        if (this.query == query) {
            return
        }
        unregister()
        this.query = query
        nextPageLiveData = repository.searchNextPage(query)
        loadMoreState.value = LoadMoreState(true, null)
        nextPageLiveData?.observeForever(this)
    }

    private fun unregister() {
        Timber.e("unregister")
        nextPageLiveData?.removeObserver(this)
        nextPageLiveData = null
        if (_hasMore) {
            query = null
        }
    }

    fun reset() {
        Timber.e("reset")
        unregister()
        _hasMore = true
        loadMoreState.value = LoadMoreState(false, null)
    }

    override fun onChanged(result: Resource<Boolean>?) {
        Timber.e("onChanged with ${result?.status}")
        if (result == null) {
            reset()
        } else {
            when (result.status) {
                Status.SUCCESS -> {
                    _hasMore = result.data == true
                    unregister()
                    loadMoreState.setValue(LoadMoreState(false, null))
                }
                Status.ERROR -> {
                    _hasMore = true
                    unregister()
                    loadMoreState.setValue(
                        LoadMoreState(false, result.message)
                    )
                }
                Status.LOADING -> {

                }
            }
        }
    }
}