package com.pionet.iot_challengeapp

import android.app.Service
import android.content.Intent
import android.content.IntentFilter
import android.os.IBinder

class MyService : Service(){

    val mBroadcastReceiver = MyBroadcastReceiver()
    val mIntentFilter = IntentFilter()

    override fun onCreate() {
        super.onCreate()

        mIntentFilter.addAction(Intent.ACTION_USER_PRESENT)
        registerReceiver(mBroadcastReceiver, mIntentFilter)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)

        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(mBroadcastReceiver)
    }
}