package com.xhan.musicplayer.feature.util

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding

class BaseAdapter<T : Any, VB : ViewBinding>(
    private val helper: ItemHelper<T, VB>
) : ListAdapter<T, BaseAdapter.HelperViewHolder<T, VB>>(helper.getDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HelperViewHolder<T, VB> {
        val inflater = LayoutInflater.from(parent.context)
        val binding = helper.createBinding(inflater, parent)
        return HelperViewHolder(binding, helper)
    }

    override fun onBindViewHolder(holder: HelperViewHolder<T, VB>, position: Int) {
        holder.bind(getItem(position), position)
    }

    class HelperViewHolder<T : Any, VB : ViewBinding>(
        private val binding: VB,
        private val helper: ItemHelper<T, VB>
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: T, position: Int) {
            helper.bind(binding, item, position)
            binding.root.setOnClickListener {
                helper.onItemClick(item, binding, position)
            }
        }
    }
}