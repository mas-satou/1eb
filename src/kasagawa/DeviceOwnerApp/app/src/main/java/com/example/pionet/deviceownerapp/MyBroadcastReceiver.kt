package com.example.pionet.deviceownerapp

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent


/**
 * Created by pionet on 2018/09/14.
 */

const val REQUEST_CODE = 0

class MyBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action.equals(Intent.ACTION_BOOT_COMPLETED)) {
            val intent: Intent = Intent(context, MainActivity::class.java)
            context?.startActivity(intent)
        }
    }
}