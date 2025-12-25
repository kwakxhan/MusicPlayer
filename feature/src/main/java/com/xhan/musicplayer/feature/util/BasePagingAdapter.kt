package com.xhan.musicplayer.feature.util

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding

class BasePagingAdapter<T : Any, VB : ViewBinding>(
    private val helper: ItemHelper<T, VB>
) : PagingDataAdapter<T, BasePagingAdapter.HelperViewHolder<T, VB>>(helper.getDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HelperViewHolder<T, VB> {
        val inflater = LayoutInflater.from(parent.context)
        val binding = helper.createBinding(inflater, parent)
        return HelperViewHolder(binding, helper)
    }

    override fun onBindViewHolder(holder: HelperViewHolder<T, VB>, position: Int) {
        getItem(position)?.let { item ->
            holder.bind(item, position)
        }
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

    companion object {
        inline fun <T : Any> createDiffCallback(
            crossinline itemComparator: (T, T) -> Boolean,
            crossinline contentComparator: (T, T) -> Boolean = { old, new -> old == new }
        ): DiffUtil.ItemCallback<T> {
            return object : DiffUtil.ItemCallback<T>() {
                override fun areItemsTheSame(old: T, new: T): Boolean =
                    itemComparator(old, new)

                override fun areContentsTheSame(old: T, new: T): Boolean =
                    contentComparator(old, new)
            }
        }

        inline fun <T : Any, ID> createDiffCallbackById(
            crossinline getId: (T) -> ID
        ): DiffUtil.ItemCallback<T> {
            return createDiffCallback(
                itemComparator = { old, new -> getId(old) == getId(new) }
            )
        }
    }
}