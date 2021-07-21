package com.amo.githubbrowsersamplev2.ui.common

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.amo.githubbrowsersamplev2.vo.Repo

class RepoListAdapter(
    private val showFullName: Boolean,
    private val repoClickCallback: ((Repo?) -> Unit)
) : ListAdapter<Repo, RepoViewHolder>(diffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RepoViewHolder {
        return RepoViewHolder.create(parent, repoClickCallback)
    }

    override fun onBindViewHolder(holder: RepoViewHolder, position: Int) {
        holder.bind(getItem(position), showFullName)
    }

    companion object {
        val diffCallback = object : DiffUtil.ItemCallback<Repo>() {

            override fun areItemsTheSame(oldItem: Repo, newItem: Repo): Boolean {
                return oldItem.owner == newItem.owner
            }

            override fun areContentsTheSame(oldItem: Repo, newItem: Repo): Boolean {
                return oldItem == newItem
            }

        }
    }

}