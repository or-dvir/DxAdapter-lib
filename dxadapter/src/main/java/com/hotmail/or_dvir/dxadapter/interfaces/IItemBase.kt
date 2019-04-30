package com.hotmail.or_dvir.dxadapter.interfaces

interface IItemBase
{
    /**
     * returns an identifier for this adapter item. this identifier should be unique.
     *
     * to prevent bugs, this value should be a constant, and it is recommended to use @IdRes
     */
    fun getViewType(): Int
}