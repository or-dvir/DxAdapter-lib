package com.hotmail.or_dvir.dxadapter

import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.StateListDrawable
import android.support.annotation.ColorInt
import android.support.annotation.ColorRes
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.ViewGroup

class DxAdapter<VH : RecyclerView.ViewHolder, ITEM: DxItem<VH>>(private val mItems: List<ITEM>)
    : RecyclerView.Adapter<VH>()
{
    private var mOnClickListener: onItemClickListener<ITEM>? = null
    private var mOnLongClickListener: onItemLongClickListener<ITEM>? = null
    private var mOnSelectStateChangedListener: onItemSelectStateChangedListener<ITEM>? = null
    @ColorRes
    private var mSelectedColor: Int? = null

    override fun getItemCount(): Int = mItems.size

    override fun onBindViewHolder(holder: VH, position: Int) = mItems[position].bindViewHolder(holder)
//    override fun onBindViewHolder(holder: VH, position: Int)
//    {
//        mItems[position].let { item ->
//            holder.itemView.background =
//
//
//
//
//            item.bindViewHolder(holder)
//        }
//
//
//
//
//
//        change background color of view depending on selection state!!!
//    }

    //todo what about onBindViewHolder(VH holder, int position, List<Object> payloads)??!?!?!?!?!?!?!?!?
    //todo what about onFailedToRecycleView (VH holder)?!?!?!?!?!?!?!?!?!?!?!?!?!?!?!
    //todo any other important methods i should override??????

    //todo WHAT ABOUT CARDS?! REMEMBER THAT YOU NEED TO SELECT THE FOREGROUND!!! (SEE Televizia project!!!)
    //todo WHAT ABOUT CARDS?! REMEMBER THAT YOU NEED TO SELECT THE FOREGROUND!!! (SEE Televizia project!!!)
    //todo WHAT ABOUT CARDS?! REMEMBER THAT YOU NEED TO SELECT THE FOREGROUND!!! (SEE Televizia project!!!)
    //todo WHAT ABOUT CARDS?! REMEMBER THAT YOU NEED TO SELECT THE FOREGROUND!!! (SEE Televizia project!!!)
    fun setSelectedItemBackgroundColor(@ColorRes colorRes: Int)
            : DxAdapter<VH, ITEM>
    {
        mSelectedColor = colorRes
        return this
    }

    fun select(position: Int)
    {
        mItems[position].let {
            it.mSelected = true
            mOnSelectStateChangedListener?.apply {
                invoke(position, it, true)
            }
        }

        notifyItemChanged(position)
    }

    fun deselect(position: Int)
    {
        mItems[position].let {
            it.mSelected = false
            mOnSelectStateChangedListener?.apply {
                invoke(position, it, false)
            }
        }

        notifyItemChanged(position)
    }

    fun setOnSelectedStateChangedListener(listener: onItemSelectStateChangedListener<ITEM>)
            : DxAdapter<VH, ITEM>
    {
        mOnSelectStateChangedListener = listener
        return this
    }

    fun setOnClickListener(listener: onItemClickListener<ITEM>)
            : DxAdapter<VH, ITEM>
    {
        mOnClickListener = listener
        return this
    }

    fun setOnLongClickListener(listener: onItemLongClickListener<ITEM>)
            : DxAdapter<VH, ITEM>
    {
        mOnLongClickListener = listener
        return this
    }

    override fun onViewRecycled(holder: VH)
    {
        super.onViewRecycled(holder)
        mItems[holder.adapterPosition].unbindViewHolder(holder)
    }

    @ColorInt
    private fun getThemeAccentColorInt(context: Context): Int
    {
        val value = TypedValue()
        context.theme.resolveAttribute(android.R.attr.colorAccent, value, true)
        return value.data
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH
    {
        val first = mItems.first()
        val context = parent.context

        val v = LayoutInflater
                .from(context)
                .inflate(first.getLayoutRes(), parent, false)

        StateListDrawable().apply {
            //replacement method requires API 23 (lib min is 21)
            @Suppress("DEPRECATION")
            val selectedColorInt =
                if (mSelectedColor != null)
                    //for sure mSelectedColor is NOT null because of the "if"
                    context.resources.getColor(mSelectedColor!!)
                else
                    getThemeAccentColorInt(context)

            //selected
            addState(intArrayOf(android.R.attr.state_selected),
                     ColorDrawable(selectedColorInt))
//            //not selected
//            addState(intArrayOf(),
//                     ContextCompat.getDrawable(ctx, getSelectableBackground(ctx)))

            v.background = this
        }

        val holder = first.createViewHolder(v)

        //NOTE:
        //we CANNOT have "position" outside of the click listeners because
        //if we do, it will not make a local copy and when clicking the item
        //it would be -1
        mOnClickListener?.apply {
            v.setOnClickListener {
                val position = holder.adapterPosition
                invoke(it, position, mItems[position])
            }
        }

        mOnLongClickListener?.apply {
            v.setOnLongClickListener {
                val position = holder.adapterPosition
                invoke(it, position, mItems[position])
            }
        }

        return holder
    }
}