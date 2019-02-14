package com.hotmail.or_dvir.dxadapter

import android.graphics.Canvas
import android.graphics.Rect
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

class DxStickyHeaderItemDecoration(private val mHeaderListener: IDxStickyHeader)
    : RecyclerView.ItemDecoration()
{
    //todo make the headers positions CONSTANTS!!!!
    //todo so that if the list is sorted, the headers remain constant!!!
    //TODO should headers show up on "filter"?
    //todo make the headers NOT interactable!!!!!

    //todo maybe let the user decide???? if they decide YES, check what happens in real time!!!
    //todo eg "header 1" is sticky and then the user drags items so that "header 2" should be sticky

    //todo DO NOT LET THE USER DRAG THEM OR SLIDE THEM OR CLICK THEM OR ANYTHING ELSE!!!!
    //todo when disabling drag, make sure though that the user CAN drag items from one "header group" to another and
    //todo that the items in the adapter actually switch!!!!!

    override fun onDrawOver(c: Canvas, recyclerView: RecyclerView, state: RecyclerView.State)
    {
        super.onDrawOver(c, recyclerView, state)

        val topChild = recyclerView.layoutManager?.getChildAt(0) ?: return

        val topChildPosition = recyclerView.getChildAdapterPosition(topChild)
        if (topChildPosition == RecyclerView.NO_POSITION)
            return

        val currentHeader = getHeaderViewForItemPosition(topChildPosition, recyclerView) ?: return
        fixLayoutSize(recyclerView, currentHeader)

        val childInContact = getChildInContact(recyclerView, currentHeader.bottom) ?: return

        if (mHeaderListener.isHeader(recyclerView.getChildAdapterPosition(childInContact)))
        {
            moveAndDrawHeader(c,
                              currentHeader,
                              0f,
                              childInContact.top.toFloat() - currentHeader.height)
            return
        }

        moveAndDrawHeader(c, currentHeader, 0f, 0f)
    }

    private fun getHeaderPositionFromItemPosition(position: Int) =
        (position downTo 0).firstOrNull { mHeaderListener.isHeader(it) }

    private fun getHeaderViewForItemPosition(itemPosition: Int, recyclerView: RecyclerView): View?
    {
        val headerPosition = getHeaderPositionFromItemPosition(itemPosition) ?: return null

        //todo how can i improve this??? do not inflate every time!!!!

        //todo is this really needed or it's enough the way it is???????
        //todo can i make this generic???? like i did with DxAdapter????
        //todo meaning that the user should implement a viewholder for the header etc...
        val header = LayoutInflater.from(recyclerView.context)
            .inflate(mHeaderListener.getHeaderLayoutRes(/*headerPosition*/),
                     recyclerView,
                     false)

        mHeaderListener.bindStickyHeader(header, headerPosition)
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

    private fun getChildInContact(recyclerView: RecyclerView, contactPoint: Int): View?
    {
        var child: View
        val viewRect = Rect()

        for (i in 0 until recyclerView.childCount)
        {
            child = recyclerView.getChildAt(i)
            recyclerView.getDecoratedBoundsWithMargins(child, viewRect)
            if(viewRect.contains(child.left, contactPoint))
                return child

//            if (child.bottom > contactPoint)
//                //This child overlaps the contactPoint
//                if (child.top <= contactPoint)
//                    return child
        }

        return null

        //original code!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
//        var child: View
//
//        for (i in 0 until recyclerView.childCount)
//        {
//            child = recyclerView.getChildAt(i)
//
//            if (child.bottom > contactPoint)
//                if (child.top <= contactPoint)
//                //This child overlaps the contactPoint
//                    return child
//        }
//
//        return null
    }

    private fun fixLayoutSize(parent: ViewGroup, view: View)
    {
        //Specs for parent (RecyclerView)
        val widthSpec = View.MeasureSpec.makeMeasureSpec(parent.width, View.MeasureSpec.EXACTLY)
        val heightSpec = View.MeasureSpec.makeMeasureSpec(parent.height, View.MeasureSpec.UNSPECIFIED)

        //Specs for children (headers)
        val childWidthSpec = ViewGroup.getChildMeasureSpec(widthSpec,
                                                           parent.paddingLeft + parent.paddingRight,
                                                           view.layoutParams.width)
        val childHeightSpec = ViewGroup.getChildMeasureSpec(heightSpec,
                                                            parent.paddingTop + parent.paddingBottom,
                                                            view.layoutParams.height)
        view.measure(childWidthSpec, childHeightSpec)
        view.layout(0, 0, view.measuredWidth, /*mStickyHeaderHeight = */view.measuredHeight)
    }
}