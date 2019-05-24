package com.hotmail.or_dvir.dxadapter.interfaces

import com.hotmail.or_dvir.dxadapter.DxSwipeBackground

/**
 * implement this interface in your ITEM if you wish for it to be swipeable
 */
interface IItemSwipeable: IItemBase
{
    note that these functiosn will be called a lot so its better to have them return
    pre-existing values instead of making new ones
    fun getSwipeBackgroundLeft(): DxSwipeBackground?
    fun getSwipeBackgroundRight(): DxSwipeBackground?
}