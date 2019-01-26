package com.hotmail.or_dvir.dxadapter

//todo do i really need this class?!?!?!! of all i need is the function "getViewType()"
//todo then there is no reason to limit the user to extend from this!!!!!!!!!!!!!!!!!!!!!
abstract class DxItem/*<VH: RecyclerViewHolder>*/(internal var mIsSelected: Boolean = false)
//    : IDxItem/*<VH>*/
{
    /**
     * to prevent bugs, it is recommended that you return an @idRes Int here
     */
    abstract fun getViewType(): Int
}
