//package com.hotmail.or_dvir.dxadapter.activities
//
//import android.os.Bundle
//import android.support.v7.view.ActionMode
//import android.support.v7.widget.DividerItemDecoration
//import android.support.v7.widget.LinearLayoutManager
//import android.support.v7.widget.RecyclerView
//import android.util.Log
//import android.view.Menu
//import android.view.MenuItem
//import com.hotmail.or_dvir.dxadapter.*
//import com.hotmail.or_dvir.dxadapter.adapters.MyAdapterHorizontal
//import com.hotmail.or_dvir.dxadapter.models.MyItemWithImage
//import kotlinx.android.synthetic.main.activity_horizontal_rv.*
//import org.jetbrains.anko.toast
//
//class ActivityHorizontalRv : BaseActivity()
//{
//  //todo not currently supported. this is for future use.
//
//    private lateinit var mAdapterHorizontal: MyAdapterHorizontal
//    private lateinit var mActionModeHelper: DxActionModeHelper<MyItemWithImage>
//
//    override fun onCreate(savedInstanceState: Bundle?)
//    {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_horizontal_rv)
//
//        val list = mutableListOf<MyItemWithImage>()
//
//        for(i in 1..100)
//            list.add(MyItemWithImage(R.drawable.ic_launcher))
//
//        mAdapterHorizontal = MyAdapterHorizontal(list,
//                                                 onItemSelectionChanged = { adapterPosition, item, isSelected ->
//                                                        mActionModeHelper.updateActionMode(this@ActivityHorizontalRv)
//                                                    })
//        mActionModeHelper =
//            DxActionModeHelper(mAdapterHorizontal,
//                               { "${mAdapterHorizontal.getNumSelectedItems()}" },
//                               object : ActionMode.Callback
//                               {
//                                   override fun onActionItemClicked(mode: ActionMode?,
//                                                                    menuItem: MenuItem?): Boolean
//                                   {
//                                       toast("${menuItem?.title}")
//                                       return true
//                                   }
//
//                                   override fun onCreateActionMode(mode: ActionMode?,
//                                                                   menu: Menu?): Boolean
//                                   {
//                                       menuInflater.inflate(R.menu.action_mode, menu)
//                                       return true
//                                   }
//
//                                   override fun onPrepareActionMode(mode: ActionMode?,
//                                                                    menu: Menu?): Boolean
//                                   {
//                                       return false
//                                   }
//
//                                   override fun onDestroyActionMode(mode: ActionMode?)
//                                   {
//                                   }
//                               })
//
//        val layMan = LinearLayoutManager(this@ActivityHorizontalRv, RecyclerView.HORIZONTAL, false)
//
//        val itemTouchHelper =
//            DxItemTouchHelper(DxItemTouchCallback(mAdapterHorizontal, layMan))
//                .apply { setDragHandleId(R.id.myItemImageHorizontal_dragHandle) }
//
//        rv_scrollListener_selectable_draggable.apply {
//            addItemDecoration(DividerItemDecoration(this@ActivityHorizontalRv, DividerItemDecoration.HORIZONTAL))
//            layoutManager = layMan
//            adapter = mAdapterHorizontal
//
//            itemTouchHelper.attachToRecyclerView(this)
//
//            firstItemVisibilityListener = DxItemVisibilityListener().apply {
//                onItemVisible = { Log.i("sample", "first item visible") }
//                onItemInvisible = { Log.i("sample", "first item not visible") }
//            }
//
//            lastItemVisibilityListener = DxItemVisibilityListener().apply {
//                onItemVisible = { Log.i("sample", "last item visible") }
//                onItemInvisible = { Log.i("sample", "last item not visible") }
//            }
//
//            onScrollListener = DxScrollListener(50).apply {
//                onScrollLeft = { Log.i("sample", "scroll left") }
//                onScrollRight = { Log.i("sample", "scroll right") }
//            }
//        }
//    }
//}
