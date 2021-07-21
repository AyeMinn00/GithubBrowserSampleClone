package com.amo.githubbrowsersamplev2.ui.search

import androidx.lifecycle.*
import androidx.lifecycle.Observer
import com.amo.githubbrowsersamplev2.repository.RepoRepository
import com.amo.githubbrowsersamplev2.util.AbsentLiveData
import com.amo.githubbrowsersamplev2.vo.Repo
import com.amo.githubbrowsersamplev2.vo.Resource
import com.amo.githubbrowsersamplev2.vo.Status
import dagger.hilt.android.lifecycle.HiltViewModel
import timber.log.Timber
import java.util.*
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(repository: RepoRepository) : ViewModel() {

    private val _query = MutableLiveData<String>()
    private val nextPageHandler = NextPageHandler(repository)

    val query: LiveData<String> = _query

    init {
        Timber.e("init")
    }

    val results: LiveData<Resource<List<Repo>>> = Transformations.switchMap(_query) { search ->
        if (search.isBlank()) {
            AbsentLiveData.create()
        } else {
            // using NetworkBoundResource to get result
            repository.search(search)
        }
    }

    val loadMoreStatus: LiveData<LoadMoreState>
        get() = nextPageHandler.loadMoreState

    fun setQuery(originalInput: String) {
        Timber.e("setQuery $originalInput")
        val input = originalInput.lowercase(Locale.getDefault()).trim()
        if (input == _query.value) {
            return
        }
        nextPageHandler.reset()
        _query.value = input
    }

    fun loadNextPage() {
        _query.value?.let {
            if (it.isNotBlank()) {
                nextPageHandler.queryNextPage(it)
            }
        }
    }

    fun refresh() {
        _query.value?.let {
            _query.value = it
        }
    }

}

























