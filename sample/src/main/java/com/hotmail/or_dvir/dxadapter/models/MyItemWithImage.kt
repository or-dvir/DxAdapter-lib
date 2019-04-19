package com.hotmail.or_dvir.dxadapter.models

import android.support.annotation.DrawableRes
import com.hotmail.or_dvir.dxadapter.R
import com.hotmail.or_dvir.dxadapter.interfaces.IDxItem

class MyItemWithImage(@DrawableRes val imageRes: Int): IDxItem
{
    override fun getViewType() = R.id.itemType_MyItemWithImage
}