package com.hotmail.or_dvir.dxadapter

//NOTE:
//cannot use interface instead of this class because we need to save the state
//of the mIsSelected variable
abstract class DxItem/*<VH: RecyclerViewHolder>*/(internal var mIsSelected: Boolean = false)
//    : IDxItem/*<VH>*/
{
    //todo think about a way to maybe convert everything (including "mIsSelected"!) to interfaces
    //todo or aliases so that the user does NOT have to inherit from this class

    /**
     * to prevent bugs, it is recommended that you return an @idRes Int here
     */
    abstract fun getViewType(): Int

    //todo when documenting, explain that these can be overridden to change behavior
    open fun isDraggable() = true
    open fun isSelectable() = true
}
