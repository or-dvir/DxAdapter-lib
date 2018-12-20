package com.hotmail.or_dvir.dxadapter

import android.support.v7.app.AppCompatActivity
import android.view.ActionMode

class DxActionModeHelper(var actionMode: ActionMode?,
                         val titleProvider: actionModeTitleProvider)
{
    fun start(act: AppCompatActivity)
    {
        act.startSupportActionMode()
    }

    fun finish()
    {
        actionMode?.finish()
        actionMode = null
    }

    fun setTitle() = { actionMode?.title = titleProvider.invoke() }
}