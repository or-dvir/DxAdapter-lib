package com.hotmail.or_dvir.dxadapter

import android.support.annotation.LayoutRes
import android.support.v7.widget.RecyclerView
import android.view.View

interface IDxItem<VH: RecyclerView.ViewHolder>
{
    @LayoutRes
    fun getLayoutRes(): Int
    fun createViewHolder(itemView: View): VH
    fun bindViewHolder(holder: VH)

    /**
     * here you should cancel any long running operations
     * or expensive resources related to this item and its' views.
     * e.g. loading images from the internet, performing long calculations, clearing image views
     */
    fun unbindViewHolder(holder: VH)
}