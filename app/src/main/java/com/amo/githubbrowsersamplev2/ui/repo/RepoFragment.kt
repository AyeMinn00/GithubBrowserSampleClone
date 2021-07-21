package com.amo.githubbrowsersamplev2.ui.repo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.amo.githubbrowsersamplev2.databinding.FragmentRepoBinding
import com.amo.githubbrowsersamplev2.util.autoCleared
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RepoFragment : Fragment() {

    private lateinit var binding: FragmentRepoBinding
    private val viewModel: RepoViewModel by viewModels()

    private val params by navArgs<RepoFragmentArgs>()
    private var adapter by autoCleared<ContributorAdapter>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentRepoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.setId(params.owner , params.name)
        initContributorList()
        subscribeUi()
    }

    private fun subscribeUi() {
        viewModel.contributors.observe(viewLifecycleOwner, { listResource ->
            adapter.submitList(listResource.data)
        })
    }

    private fun initContributorList() {
        val adapter = ContributorAdapter {}
        this.adapter = adapter
        binding.contributorList.adapter = adapter
        binding.contributorList.layoutManager = LinearLayoutManager(requireContext())
        postponeEnterTransition()
        binding.contributorList.doOnPreDraw {
            startPostponedEnterTransition()
        }
    }

}