package com.hotmail.or_dvir.dxadapter.activities

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import or_dvir.hotmail.com.dxutils.setHomeUpEnabled

abstract class BaseActivity: AppCompatActivity()
{
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setHomeUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean
    {
        if(item.itemId == android.R.id.home)
            onBackPressed()

        return super.onOptionsItemSelected(item)
    }
}