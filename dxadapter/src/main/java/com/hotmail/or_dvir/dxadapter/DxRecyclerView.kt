package com.hotmail.or_dvir.dxadapter

import android.content.Context
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.util.Log
import kotlin.math.abs

class DxRecyclerView @JvmOverloads constructor(context: Context,
                                               attrs: AttributeSet? = null,
                                               defStyle: Int = 0)
    :RecyclerView(context, attrs, defStyle)
{
    //todo test these listeners if the adapter changes during runtime!!!!

    /**
     * * [OnAdapterItemVisibilityChanged.onVisible] will trigger immediately as the [DxRecyclerView] loads
     * (assuming the adapter contains at least 1 item).
     * * if your entire list fits inside the screen, [OnAdapterItemVisibilityChanged.onInvisible] will NEVER trigger.
     */
    var firstItemVisibilityListener: OnAdapterItemVisibilityChanged? = null
    /**
     * * [OnAdapterItemVisibilityChanged.onInvisible] will trigger immediately as the [DxRecyclerView] loads
     * (assuming it does NOT fit inside the screen).
     * * if your entire list fits inside the screen, [OnAdapterItemVisibilityChanged.onVisible]
     * will trigger immediately as the [DxRecyclerView] loads,
     * and [OnAdapterItemVisibilityChanged.onInvisible] will NEVER trigger.
     */
    var lastItemVisibilityListener: OnAdapterItemVisibilityChanged? = null

    /**
     * NOTE: this will trigger MANY times while scrolling
     *
     * [Pair.first] = sensitivity of the listener
     *
     * [Pair.second] = the listener itself
     */
    var onScrollingDownListener: scrollUpDownPair? = null
    /**
     * NOTE: this will trigger MANY times while scrolling
     *
     * [Pair.first] = sensitivity of the listener
     *
     * [Pair.second] = the listener itself
     */
    var onScrollingUpListener: scrollUpDownPair? = null

    private var notifiedFirstVisible = false
    private var notifiedFirstInvisible = false
    private var notifiedLastVisible = false
    private var notifiedLastInvisible = false


    consider this scenario: user keeps holding his finger and moving it up and down,
    this this causes mTotalDy to increase when dragging in BOTH direction
    meaning the the fab will be showing/hiding when the user drags up and down...
    private var mTotalDy = 0

    override fun onAttachedToWindow()
    {
        super.onAttachedToWindow()

        //no need to waste runtime on ScrollListener if
        //no callbacks have been set
        if (firstItemVisibilityListener != null ||
            lastItemVisibilityListener  != null)
        {
            addOnScrollListener(object : OnScrollListener()
            {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int)
                {
                    super.onScrollStateChanged(recyclerView, newState)
                    //stops scrolling - reset the variable
                    if(newState == SCROLL_STATE_IDLE)
                        mTotalDy = 0
                }

                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int)
                {
                    super.onScrolled(recyclerView, dx, dy)

                    mTotalDy += abs(dy)
                    Log.i("aaaaa", "$mTotalDy")
                    when
                    {
                        //user is dragging up -> list is scrolling down
                        dy > 0 -> onScrollingDownListener?.let { invokeScrollUpDownListener(it) }
                        //user is dragging down -> list is scrolling up
                        dy < 0 -> onScrollingUpListener?.let { invokeScrollUpDownListener(it) }
                    }

                    //todo can i save layoutManager as global member so i don't have to cast each time???
                    //todo don't forget about staggered grid!!!!!!!
                    layoutManager?.apply {
                        val layMan = this as LinearLayoutManager

                        //todo when documenting, note the order of the callbacks!!!

                        firstItemVisibilityListener?.let {
                            val firstPos = layMan.findFirstVisibleItemPosition()

                            //if no items, we can immediately return
                            if (firstPos == NO_POSITION)
                                return@apply

                            when
                            {
                                firstPos == 0 ->
                                {
                                    if (!notifiedFirstVisible)
                                    {
                                        it.onVisible()
                                        notifiedFirstVisible = true
                                        notifiedFirstInvisible = false
                                    }
                                }

                                //if we get here, firstPos is NOT 0
                                !notifiedFirstInvisible ->
                                {
                                    it.onInvisible()
                                    notifiedFirstVisible = false
                                    notifiedFirstInvisible = true
                                }
                            }
                        }

                        lastItemVisibilityListener?.let {
                            val lastPos = layMan.findLastVisibleItemPosition()
                            val numItems = adapter?.itemCount

                            //if no items or no adapter is attached, we can immediately return
                            if (lastPos == NO_POSITION || numItems == null)
                                return@apply

                            when
                            {
                                lastPos == (numItems -1) ->
                                {
                                    if (!notifiedLastVisible)
                                    {
                                        it.onVisible()
                                        notifiedLastVisible = true
                                        notifiedLastInvisible = false
                                    }
                                }

                                //if we get here, lastPos is NOT (numItems -1)
                                !notifiedLastInvisible ->
                                {
                                    it.onInvisible()
                                    notifiedLastVisible = false
                                    notifiedLastInvisible = true
                                }
                            }
                        }
                    }
//                                    linearLayoutManager has functions for this!
//                                    GRID LAYOUT MANAGER IS A SUBCLASS OF LINEAR LAYOUT MANAGER!!!!!!!
//
//                                    for staggered grid, it has its own functions (needs separate class)
                }
            })
        }
    }

    private fun invokeScrollUpDownListener(pair: scrollUpDownPair)
    {
        if(mTotalDy >= pair.first)
        {
            pair.second.invoke()
            //triggered the listener - reset the variable
            mTotalDy = 0
        }
    }

    override fun onDetachedFromWindow()
    {
        clearOnScrollListeners()
        super.onDetachedFromWindow()
    }
}