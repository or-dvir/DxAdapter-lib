package com.hotmail.or_dvir.dxadapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet

class DxRecyclerView @JvmOverloads constructor(context: Context,
                                               attrs: AttributeSet? = null,
                                               defStyle: Int = 0)
    :RecyclerView(context, attrs, defStyle)
{
    private var mEdgeListener: IDxRecyclerEdgesListener? = null

    fun setRecyclerViewEdgeListener(listener: IDxRecyclerEdgesListener)
            : DxRecyclerView
    {
        mEdgeListener = listener
        return this
    }

    override fun onAttachedToWindow()
    {
        super.onAttachedToWindow()
        addOnScrollListener(object : OnScrollListener()
                            {
                                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int)
                                {
                                    if(layoutManager?.find)

                                    if(gridLayoutManager.findLastVisibleItemPosition() ==
                                        gridLayoutManager.itemCount-1){
                                        // We have reached the end of the recycler view.
                                    }


                                    super.onScrolled(recyclerView, dx, dy)
                                }
                            })
    }

    override fun onDetachedFromWindow()
    {
        clearOnScrollListeners()
        super.onDetachedFromWindow()
    }
}