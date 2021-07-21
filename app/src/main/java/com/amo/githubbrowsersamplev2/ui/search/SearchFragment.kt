package com.amo.githubbrowsersamplev2.ui.search

import android.content.Context
import android.os.Bundle
import android.os.IBinder
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.amo.githubbrowsersamplev2.R
import com.amo.githubbrowsersamplev2.databinding.SearchFragmentBinding
import com.amo.githubbrowsersamplev2.ui.common.RepoListAdapter
import com.amo.githubbrowsersamplev2.util.autoCleared
import com.amo.githubbrowsersamplev2.vo.Repo
import com.amo.githubbrowsersamplev2.vo.Status
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class SearchFragment : Fragment() {

    private val searchViewModel: SearchViewModel by viewModels()

    private lateinit var binding: SearchFragmentBinding

    var adapter by autoCleared<RepoListAdapter>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = SearchFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerView()
        initSearchInputListener()
        subscribeUi()
    }

    private fun initRecyclerView() {
        binding.repoList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val lastPosition = layoutManager.findLastVisibleItemPosition()
                if (lastPosition == adapter.itemCount - 1) {
                    searchViewModel.loadNextPage()
                }
            }
        })

        val rvAdapter = RepoListAdapter(true) {
            onClickRepo(it)
        }
        adapter = rvAdapter
        binding.repoList.adapter = adapter

    }

    private fun initSearchInputListener() {
        binding.input.setOnEditorActionListener { view, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                doSearch(view)
                true
            } else {
                false
            }
        }
        binding.input.setOnKeyListener { view, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                doSearch(view)
                true
            } else {
                false
            }
        }
    }

    private fun subscribeUi() {
        searchViewModel.query.observe(viewLifecycleOwner, {
            binding.noResultsText.text = resources.getString(R.string.empty_search_result, it)
        })

        searchViewModel.results.observe(viewLifecycleOwner, { result ->
            Timber.e("observe result with ${result?.status}")
            when (result?.status) {
                Status.SUCCESS -> {
                    showSearchResult(result.data)
                }
                Status.ERROR -> {
                    showError(result.message)
                }
                Status.LOADING -> {
                    showLoading()
                }
            }
        })

        searchViewModel.loadMoreStatus.observe(viewLifecycleOwner, { loadingMore ->
            Timber.e("observe loadingMore $loadingMore")
            if (loadingMore == null) {
                binding.loadMoreBar.visibility = View.GONE
            } else {
                binding.loadMoreBar.visibility =
                    if (loadingMore.isRunning) View.VISIBLE else View.GONE
                val error = loadingMore.errorMessageIfNotHandled
                if (error != null) {
                    Snackbar.make(binding.loadMoreBar, error, Snackbar.LENGTH_SHORT).show()
                }
            }
        })
    }

    private fun doSearch(v: View) {
        val query = binding.input.text.toString()
        dismissKeyboard(v.windowToken)
        searchViewModel.setQuery(query)
    }

    private fun dismissKeyboard(windowToken: IBinder) {
        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        imm?.hideSoftInputFromWindow(windowToken, 0)
    }

    private fun showSearchResult(result: List<Repo>?) {
        if (result != null) {
            if (result.isEmpty()) {
                binding.noResultsText.text = "no one"
                binding.noResultsText.visibility = View.VISIBLE
            } else {
                binding.noResultsText.visibility = View.INVISIBLE
            }
        }
        adapter.submitList(result)
        binding.loading.progressBar.visibility = View.INVISIBLE
        binding.loading.btnRetry.visibility = View.INVISIBLE
    }

    private fun showLoading() {
        binding.loading.progressBar.visibility = View.VISIBLE
        binding.loading.btnRetry.visibility = View.INVISIBLE
        binding.loading.tvErrorMsg.visibility = View.INVISIBLE
    }

    private fun showError(errorMsg: String?) {
        binding.loading.progressBar.visibility = View.INVISIBLE
        binding.loading.btnRetry.visibility = View.INVISIBLE
        binding.loading.tvErrorMsg.visibility = View.VISIBLE
        binding.loading.tvErrorMsg.text = errorMsg ?: "unknown error"
    }

    private fun onClickRepo(repo: Repo?) {
        repo?.let {
            findNavController().navigate(
                SearchFragmentDirections.showRepo(it.owner.login, it.name)
            )
        }
    }

}






















