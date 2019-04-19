package com.hotmail.or_dvir.dxadapter.interfaces

interface IDxItem
{
    /**
     * to prevent bugs, this value should be a constant.
     * it is recommended to use @IdRes
     */
    fun getViewType(): Int
}