package com.amo.githubbrowsersamplev2.ui.common

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.amo.githubbrowsersamplev2.databinding.RepoItemBinding
import com.amo.githubbrowsersamplev2.vo.Repo

class RepoViewHolder(private val binding: RepoItemBinding, clickCallback: ((Repo?) -> Unit)) :
    RecyclerView.ViewHolder(binding.root) {

    private var model: Repo? = null

    init {
        binding.root.setOnClickListener {
            clickCallback(model)
        }
    }

    fun bind(model: Repo, showFullName: Boolean) {
        this.model = model
        binding.name.text = if (showFullName) model.fullName else model.name
        binding.desc.text = model.description
    }

    companion object {
        fun create(parent: ViewGroup, clickCallback: ((Repo?) -> Unit)): RepoViewHolder {
            val binding =
                RepoItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return RepoViewHolder(binding, clickCallback)
        }
    }

}