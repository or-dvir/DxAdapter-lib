package com.hotmail.or_dvir.dxadapter.models

import com.hotmail.or_dvir.dxadapter.R
import com.hotmail.or_dvir.dxadapter.interfaces.IItemDraggable
import com.hotmail.or_dvir.dxadapter.interfaces.IItemSelectable
import com.hotmail.or_dvir.dxadapter.interfaces.IItemSwipeable
import kotlin.random.Random

class MyItem(var mText: String)
    : IItemDraggable,
      IItemSwipeable,
      IItemSelectable
//note that all of these interfaces already extend IItemBase so there is no need
//to implement it directly
{
    //generating random number to show how to set swipe background
    //according to item state
    val random1to100 = Random.nextInt(100) + 1

    override var isSelected = false
    override fun getViewType() = R.id.itemType_MyItem
}