package com.nanamare.mac.grab.ext

import androidx.databinding.BindingAdapter
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.nanamare.mac.grab.base.ui.SimpleRecyclerView

@Suppress("UNCHECKED_CAST")
@BindingAdapter("replaceAll")
fun RecyclerView.replaceAll(list: List<Any>?) =
    (adapter as? SimpleRecyclerView.Adapter<Any, *>)?.replaceAll(list)


@BindingAdapter("adapter")
fun RecyclerView.bindRecyclerViewAdapter(adapter: SimpleRecyclerView.Adapter<*, out ViewDataBinding>) {
    this.adapter = adapter
}

@BindingAdapter("dividerDirection")
fun RecyclerView.setItemDecoration(newDirection: Int){
    addItemDecoration(DividerItemDecoration(this.context, newDirection))
}
