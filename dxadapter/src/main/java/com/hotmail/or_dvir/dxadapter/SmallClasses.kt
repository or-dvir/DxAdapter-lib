package com.hotmail.or_dvir.dxadapter

//open class SimpleViewHolder(itemView: View): RecyclerViewHolder(itemView)

/////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////

<<<<<<< HEAD
abstract class DxItem<VH: RecyclerViewHolder>(internal var mIsSelected: Boolean = false)
    : IDxItem<VH>
=======
abstract class DxItem/*<VH: RecyclerViewHolder>*/(internal var mIsSelected: Boolean = false)
    : IDxItem/*<VH>*/
>>>>>>> parent of 4b34bfe... almost finished experimenting with abstract adapter
