package com.pionet.iot_challengeapp

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class MyBroadcastReceiver : BroadcastReceiver(){
    val TAG = MyBroadcastReceiver::class.java.simpleName

    override fun onReceive(context: Context?, intent: Intent?) {
        println("インテント【" + intent + "】")

        // Javaでいうswitch文
        when (intent?.action) {
            Intent.ACTION_LOCKED_BOOT_COMPLETED -> {

                //サービス開始
                val startService = Intent(context?.applicationContext, MyService::class.java)
                context?.startService(startService)
            }

            Intent.ACTION_USER_PRESENT -> {
                println("ロック解除しました")

                //アクティビティ起動する
                val startActivity = Intent(context?.applicationContext, MainActivity::class.java)
                context?.startActivity(startActivity)
            }
        }
    }
}