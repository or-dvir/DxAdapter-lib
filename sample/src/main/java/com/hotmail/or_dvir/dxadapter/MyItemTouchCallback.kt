package com.hotmail.or_dvir.dxadapter

import android.content.Context
import android.graphics.Color
import com.hotmail.or_dvir.dxadapter.adapters.MyAdapter
import com.hotmail.or_dvir.dxadapter.models.MyItem

class MyItemTouchCallback(context: Context, adapter: MyAdapter/*, onSwipe: onItemSwipedListener<MyItem>*/)
    : DxItemTouchCallback<MyItem>(adapter)
{
    //an optional way of enabling swiping directly in this class as opposed to the way
    //it's done in ActivityMain
//    init
//    {
//        enableSwiping(ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT, onSwipe)
//    }

    //IMPORTANT: read documentation for DxSwipeIcon and DxSwipeText
    private val backgroundRightOptionA =
        DxSwipeBackground(30, Color.MAGENTA, DxSwipeText("right swipe A", 60f, Color.BLACK),
                          DxSwipeIcon(context, R.drawable.ic_arrow_up, 96))

    private val backgroundRightOptionB =
        DxSwipeBackground(30, Color.LTGRAY, DxSwipeText("right swipe B", 60f, Color.BLACK),
                          DxSwipeIcon(context, R.drawable.ic_arrow_down, 96))


    override fun getSwipeBackgroundRight(item: MyItem): DxSwipeBackground?
    {
        //get the background according to item state.
        //note that if that state changes during the swipe, the swipe background will flicker
        return if (item.random1to100 <= 50)
            backgroundRightOptionA
        else
            backgroundRightOptionB
    }
}