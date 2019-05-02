package com.hotmail.or_dvir.dxadapter.models

import com.hotmail.or_dvir.dxadapter.R
import com.hotmail.or_dvir.dxadapter.interfaces.IItemBase
import com.hotmail.or_dvir.dxadapter.interfaces.IItemDraggable
import com.hotmail.or_dvir.dxadapter.interfaces.IItemSelectable
import com.hotmail.or_dvir.dxadapter.interfaces.IItemSwipeable

class MyItem(var mText: String)
    : IItemDraggable,
      IItemSwipeable,
      IItemSelectable
{
    override var isSelected = false
    override fun getViewType() = R.id.itemType_MyItem
}