package com.hotmail.or_dvir.dxadapter

import android.app.Application
import com.squareup.leakcanary.LeakCanary

//it IS used in the manifest
@Suppress("unused")
class MyApplication: Application()
{
    companion object
    {
        lateinit var INSTANCE: MyApplication
    }

    override fun onCreate()
    {
        super.onCreate()
        if (LeakCanary.isInAnalyzerProcess(this))
        {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return
        }
        LeakCanary.install(this)
        // Normal app init code...
    }
}