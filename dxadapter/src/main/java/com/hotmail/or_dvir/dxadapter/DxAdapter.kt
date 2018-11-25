package com.hotmail.or_dvir.dxadapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

class DxAdapter<VH : RecyclerView.ViewHolder, ITEM: IDxItem<VH>>(private val mItems: List<ITEM>)
    : RecyclerView.Adapter<VH>()
{
    private var mClickListener: onItemClickedListener<ITEM>? = null
//    private var mClickListener: IOnItemClickListener<VH, ITEM>? = null

    override fun getItemCount(): Int = mItems.size
    override fun onBindViewHolder(holder: VH, position: Int) = mItems[position].bindViewHolder(holder)

    //todo what about onBindViewHolder(VH holder, int position, List<Object> payloads)??!?!?!?!?!?!?!?!?
    //todo what about onFailedToRecycleView (VH holder)?!?!?!?!?!?!?!?!?!?!?!?!?!?!?!
    //todo any other important methods i should override??????

    fun setOnClickListener(listener: onItemClickedListener<ITEM>)
            : DxAdapter<VH, ITEM>
    {
        mClickListener = listener
        return this
    }

    override fun onViewRecycled(holder: VH)
    {
        super.onViewRecycled(holder)
        mItems[holder.adapterPosition].unbindViewHolder(holder)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH
    {
        val first = mItems.first()

        val v = LayoutInflater
                .from(parent.context)
                .inflate(first.getLayoutRes(), parent, false)

        val holder = first.createViewHolder(v)

        v.setOnClickListener {
            mClickListener?.apply {
                val position = holder.adapterPosition
                invoke(it, position, mItems[position])
            }
        }

        return holder
    }
}