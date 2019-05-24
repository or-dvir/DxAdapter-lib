package com.hotmail.or_dvir.dxadapter.models

import com.hotmail.or_dvir.dxadapter.DxSwipeBackground
import com.hotmail.or_dvir.dxadapter.DxSwipeIcon
import com.hotmail.or_dvir.dxadapter.R
import com.hotmail.or_dvir.dxadapter.interfaces.IItemBase
import com.hotmail.or_dvir.dxadapter.interfaces.IItemDraggable
import com.hotmail.or_dvir.dxadapter.interfaces.IItemSelectable
import com.hotmail.or_dvir.dxadapter.interfaces.IItemSwipeable

class MyItem(var mText: String)
    : IItemDraggable,
      IItemSwipeable,
      IItemSelectable
//note that all of these interfaces already extend IItemBase so there is no need
//to implement it directly
{
    override var isSelected = false
    override fun getViewType() = R.id.itemType_MyItem



    val testing = DxSwipeBackground(30, f,"", DxSwipeIcon())


    override fun getSwipeBackgroundLeft(): DxSwipeBackground?
    {
        TODO("not implemented")
    }

    override fun getSwipeBackgroundRight(): DxSwipeBackground?
    {
        TODO("not implemented")
    }
}