package com.hotmail.or_dvir.dxadapter

abstract class DxHeaderAdapter<ITEM: DxItem, VH: RecyclerViewHolder>(mItems: MutableList<ITEM>)
    : DxAdapter<ITEM, VH>(mItems), IDxStickyHeader
{
    override fun getHeaderPositionFromItemPosition(position: Int) =
        (position downTo 0).firstOrNull { isHeader(it) } ?: 0

    //todo should be provided by the user
//    override fun getHeaderLayout(): Int
//    {
//    }

    //todo should be provided by the user
//    override fun bindHeaderData(header: View, headerPosition: Int)
//    {
//    }

    //todo should return true of the item in the given position is a header
    override fun isHeader(itemPosition: Int): Boolean
    {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}