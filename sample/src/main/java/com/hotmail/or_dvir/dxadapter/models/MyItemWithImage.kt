package com.hotmail.or_dvir.dxadapter.models

import android.support.annotation.DrawableRes
import com.hotmail.or_dvir.dxadapter.DxItem
import com.hotmail.or_dvir.dxadapter.R

class MyItemWithImage(@DrawableRes val imageRes: Int): DxItem/*<MyItem.DefaultViewHolder>*/()
{
    override fun isDraggable() = true

    override fun getViewType() = R.id.itemType_MyItemWithImage
}