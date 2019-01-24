package com.hotmail.or_dvir.dxadapter

//open class SimpleViewHolder(itemView: View): RecyclerViewHolder(itemView)

/////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////

abstract class DxItem/*<VH: RecyclerViewHolder>*/(internal var mIsSelected: Boolean = false)
//    : IDxItem/*<VH>*/
{
    /**
     * to prevent bugs, it is recommended that you return an @idRes Int here
     */
    abstract fun getViewType(): Int
}
