package com.hotmail.or_dvir.dxadapter

import android.support.v7.widget.RecyclerView

abstract class DxItem<VH: RecyclerView.ViewHolder>(internal var mIsSelected: Boolean = false)
    : IDxItem<VH>
