package com.hotmail.or_dvir.dxadapter

import android.view.View

typealias onItemClickedListener<ITEM> = ((view: View, position: Int, item: ITEM) -> Unit)
typealias onItemLongClickedListener<ITEM> = onItemClickedListener<ITEM>