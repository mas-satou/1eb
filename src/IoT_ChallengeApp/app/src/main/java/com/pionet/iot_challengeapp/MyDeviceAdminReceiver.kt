package com.pionet.iot_challengeapp

import android.app.admin.DeviceAdminReceiver
import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent

class MyDeviceAdminReceiver : DeviceAdminReceiver() {
    // fun→関数の宣言
    override fun onEnabled(context: Context?, intent: Intent?) {
        super.onEnabled(context, intent)
        // ?→変数がnullならnullを返す。nullじゃなければ関数実行
        context?.run {
            // val→不変
            val deviceadmin = ComponentName(this,MyDeviceAdminReceiver::class.java)
            // as→キャスト。キャストに失敗したら例外が発生
            val dpm = this.getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
            // ユーザーの確認ステップなしでアプリの固定を有効にできる
            dpm.setLockTaskPackages(deviceadmin, arrayOf(this.packageName))
        }
    }
}