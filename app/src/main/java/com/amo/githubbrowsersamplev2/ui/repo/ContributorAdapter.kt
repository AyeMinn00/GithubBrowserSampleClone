package com.amo.githubbrowsersamplev2.ui.repo

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.amo.githubbrowsersamplev2.databinding.ContributorItemBinding
import com.amo.githubbrowsersamplev2.vo.Contributor

class ContributorAdapter(private val onClickCallback: (Contributor?) -> Unit) :
    ListAdapter<Contributor, ContributorViewHolder>(diffUtil) {

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<Contributor>() {
            override fun areItemsTheSame(oldItem: Contributor, newItem: Contributor): Boolean {
                return oldItem.login == newItem.login
            }

            override fun areContentsTheSame(oldItem: Contributor, newItem: Contributor): Boolean {
                return oldItem.avatarUrl == newItem.avatarUrl && oldItem.contributions == newItem.contributions
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContributorViewHolder {
        return ContributorViewHolder.create(parent, onClickCallback)
    }

    override fun onBindViewHolder(holder: ContributorViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

}

class ContributorViewHolder(
    private val binding: ContributorItemBinding,
    val onClickCallback: (Contributor?) -> Unit
) :
    RecyclerView.ViewHolder(binding.root) {

    private var model: Contributor? = null

    init {
        itemView.setOnClickListener {
            onClickCallback(model)
        }
    }

    fun bind(model: Contributor) {
        this.model = model
        binding.tvContributorName.text = model.repoOwner
    }

    companion object {
        fun create(
            viewGroup: ViewGroup,
            onClickCallback: (Contributor?) -> Unit
        ): ContributorViewHolder {
            val binding = ContributorItemBinding.inflate(
                LayoutInflater.from(viewGroup.context),
                viewGroup,
                false
            )
            return ContributorViewHolder(binding, onClickCallback)
        }
    }

}