package com.hotmail.or_dvir.dxadapter

import android.graphics.Canvas
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

class DxStickyHeaderItemDecoration(recyclerView: RecyclerView,
                                   private val mHeaderListener: IDxStickyHeader)
    : RecyclerView.ItemDecoration()
{
    private var mStickyHeaderHeight: Int = 0

//    init
//    {
//        // On Sticky Header Click
//        recyclerView.addOnItemTouchListener(object : RecyclerView.OnItemTouchListener
//                                            {
//                                                override fun onInterceptTouchEvent(recyclerView: RecyclerView,
//                                                                                   motionEvent: MotionEvent): Boolean
//                                                {
//                                                    return motionEvent.y <= mStickyHeaderHeight
//                                                }
//
//                                                override fun onTouchEvent(recyclerView: RecyclerView,
//                                                                          motionEvent: MotionEvent)
//                                                {
//
//                                                }
//
//                                                override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean)
//                                                {
//
//                                                }
//                                            })
//    }

    override fun onDrawOver(c: Canvas, recyclerView: RecyclerView, state: RecyclerView.State)
    {
        super.onDrawOver(c, recyclerView, state)

        val topChild = recyclerView.getChildAt(0) ?: return

        val topChildPosition = recyclerView.getChildAdapterPosition(topChild)
        if (topChildPosition == RecyclerView.NO_POSITION)
            return

        val currentHeader = getHeaderViewForItemPosition(topChildPosition, recyclerView)
        fixLayoutSize(recyclerView, currentHeader)

        val childInContact = getChildInContact(recyclerView, currentHeader.bottom) ?: return

        if (mHeaderListener.isHeader(recyclerView.getChildAdapterPosition(childInContact)))
        {
            moveAndDrawHeader(c,
                              currentHeader,
                              0f,
                              childInContact.top.toFloat() - currentHeader.height)
//            moveHeader(c, currentHeader, childInContact)
            return
        }

        moveAndDrawHeader(c, currentHeader, 0f, 0f)
//        drawHeader(c, currentHeader)
    }

    private fun getHeaderViewForItemPosition(itemPosition: Int, recyclerView: RecyclerView): View
    {
        val headerPosition = mHeaderListener.getHeaderPositionFromItemPosition(itemPosition)

        //todo is this really needed or it's enough the way it is???????
        //todo can i make this generic???? like i did with DxAdapter????
        //todo meaning that the user should implement a viewholder for the header etc...
        val header = LayoutInflater.from(recyclerView.context)
            .inflate(mHeaderListener.getHeaderLayout(/*headerPosition*/),
                     recyclerView,
                     false)

        mHeaderListener.bindHeaderData(header, headerPosition)
        return header
    }

    private fun moveAndDrawHeader(c: Canvas,
                                  currentHeader: View,
                                  translationX: Float,
                                  translationY: Float)
    {
        c.save()
        c.translate(translationX, translationY)
        currentHeader.draw(c)
        c.restore()
    }

//    private fun drawHeader(c: Canvas, header: View)
//    {
//        c.save()
//        c.translate(0f, 0f)
//        header.draw(c)
//        c.restore()
//    }

//    private fun moveHeader(c: Canvas, currentHeader: View, nextHeader: View)
//    {
//        c.save()
//        c.translate(0f, nextHeader.top.toFloat() - currentHeader.height)
//        currentHeader.draw(c)
//        c.restore()
//    }

    private fun getChildInContact(recyclerView: RecyclerView, contactPoint: Int): View?
    {
        var child: View

        for (i in 0 until recyclerView.childCount)
        {
            child = recyclerView.getChildAt(i)

            if (child.bottom > contactPoint)
            {
                //This child overlaps the contactPoint
                if (child.top <= contactPoint)
                {
                    return child
                }
            }
        }

        return null
    }

    private fun fixLayoutSize(parent: ViewGroup, view: View)
    {
        //Specs for parent (RecyclerView)
        val widthSpec = View.MeasureSpec.makeMeasureSpec(parent.width, View.MeasureSpec.EXACTLY)
        val heightSpec = View.MeasureSpec.makeMeasureSpec(parent.height, View.MeasureSpec.UNSPECIFIED)

        //Specs for children (headers)
        val childWidthSpec = ViewGroup.getChildMeasureSpec(widthSpec,
                                                           parent.paddingLeft + parent.paddingRight,
                                                           view.getLayoutParams().width)
        val childHeightSpec = ViewGroup.getChildMeasureSpec(heightSpec,
                                                            parent.paddingTop + parent.paddingBottom,
                                                            view.getLayoutParams().height)
        view.measure(childWidthSpec, childHeightSpec)
        view.layout(0, 0, view.measuredWidth, /*mStickyHeaderHeight = */view.measuredHeight)
    }
}