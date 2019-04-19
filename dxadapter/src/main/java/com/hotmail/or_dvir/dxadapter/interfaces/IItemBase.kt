package com.hotmail.or_dvir.dxadapter.interfaces

interface IItemBase
{
    /**
     * to prevent bugs, this value should be a constant.
     * it is recommended to use @IdRes
     */
    fun getViewType(): Int
}