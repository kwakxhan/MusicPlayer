package com.xhan.musicplayer.feature.util

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.viewbinding.ViewBinding

interface ItemHelper<T : Any, VB : ViewBinding> {
    /** DiffUtil.ItemCallback 제공 */
    fun getDiffCallback(): DiffUtil.ItemCallback<T>

    /** ViewBinding 생성 */
    fun createBinding(inflater: LayoutInflater, parent: ViewGroup): VB

    /** ViewHolder와 아이템 바인딩 */
    fun bind(binding: VB, item: T, position: Int)

    /** 아이템 클릭 처리 (optional) */
    fun onItemClick(item: T, binding: VB, position: Int): Boolean = false
}

fun interface OnItemClick<T, VB : ViewBinding> {
    fun onClick(item: T, binding: VB, position: Int): Boolean
}

inline fun <T : Any, VB : ViewBinding> itemHelper(
    crossinline diffCallback: () -> DiffUtil.ItemCallback<T>,
    crossinline createBinding: (LayoutInflater, ViewGroup) -> VB,
    crossinline bind: (VB, T, Int) -> Unit,
    noinline onItemClick: ((T, VB, Int) -> Boolean)? = null
): ItemHelper<T, VB> = object : ItemHelper<T, VB> {
    override fun getDiffCallback() = diffCallback()
    override fun createBinding(inflater: LayoutInflater, parent: ViewGroup) =
        createBinding(inflater, parent)

    override fun bind(binding: VB, item: T, position: Int) = bind(binding, item, position)
    override fun onItemClick(item: T, binding: VB, position: Int) =
        onItemClick?.invoke(item, binding, position) ?: false
}