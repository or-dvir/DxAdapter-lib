package com.hotmail.or_dvir.dxadapter

import android.view.View

open class SimpleViewHolder(itemView: View): RecyclerViewHolder(itemView)

/////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////

abstract class DxItem<VH: RecyclerViewHolder>(internal var mIsSelected: Boolean = false)
    : IDxItem<VH>