package com.ditclear.paonet.view.article

import android.arch.paging.PagedList
import android.arch.paging.PagedListAdapter
import android.content.Context
import android.databinding.DataBindingUtil
import android.databinding.ObservableList
import android.databinding.ViewDataBinding
import android.support.v7.recyclerview.extensions.DiffCallback
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.ditclear.paonet.BR
import com.ditclear.paonet.R
import com.ditclear.paonet.vendor.recyclerview.BindingViewHolder
import com.ditclear.paonet.vendor.recyclerview.ItemClickPresenter


/**
 * 页面描述：PagedAdapter
 *
 * Created by ditclear on 2017/10/3.
 */
open class PagedAdapter<T>(context: Context, private val layoutRes: Int, private val mCollection: ObservableList<T>, diffCallback: DiffCallback<T>) : PagedListAdapter<T, BindingViewHolder<ViewDataBinding>>(diffCallback) {

    private var mLayoutInflater: LayoutInflater = LayoutInflater.from(context)
    var presenter: ItemClickPresenter<T>? = null
    var decorator: Decorator? = null
    private var viewPool: RecyclerView.RecycledViewPool? = null
    var needPool: Boolean = false

    init {
        if (needPool) {
            viewPool = RecyclerView.RecycledViewPool()
        }
        mCollection.addOnListChangedCallback(object : ObservableList.OnListChangedCallback<ObservableList<T>>() {
            override fun onChanged(contributorViewModels: ObservableList<T>) {
                notifyDataSetChanged()
            }

            override fun onItemRangeChanged(contributorViewModels: ObservableList<T>, i: Int, i1: Int) {
                notifyItemRangeChanged(i, i1)
            }

            override fun onItemRangeInserted(contributorViewModels: ObservableList<T>, i: Int, i1: Int) {
                notifyItemRangeInserted(i, i1)
            }

            override fun onItemRangeMoved(contributorViewModels: ObservableList<T>, i: Int, i1: Int,
                                          i2: Int) {
                notifyItemMoved(i, i1)
            }

            override fun onItemRangeRemoved(contributorViewModels: ObservableList<T>, i: Int, i1: Int) {
                notifyItemRangeRemoved(i, i1)
            }
        })
    }

    override fun getCurrentList(): PagedList<T>? = super.getCurrentList()

    override fun getItemCount() = mCollection.size

    override fun getItem(position: Int): T? = mCollection[position]

    interface Decorator {
        fun decorator(holder: BindingViewHolder<ViewDataBinding>?, position: Int, viewType: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): BindingViewHolder<ViewDataBinding> {
        val binding = DataBindingUtil.inflate<ViewDataBinding>(mLayoutInflater, layoutRes, parent, false)
        if (needPool != null && needPool) {
            //如果item有recyclerView 设置单一的 view pool
            // @link{http://www.jcodecraeer.com/a/anzhuokaifa/androidkaifa/2017/0914/8514.html}
            if (binding?.root?.findViewById<RecyclerView>(R.id.recycler_view) != null) {
                binding?.root?.findViewById<RecyclerView>(R.id.recycler_view)?.recycledViewPool = viewPool
            }
        }
        return BindingViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BindingViewHolder<ViewDataBinding>?, position: Int) {
        val item = getItem(position)
        holder?.binding?.setVariable(BR.item, item)
        holder?.binding?.setVariable(BR.presenter, presenter)
        holder?.binding?.executePendingBindings()
        decorator?.decorator(holder, position, getItemViewType(position))
    }

}