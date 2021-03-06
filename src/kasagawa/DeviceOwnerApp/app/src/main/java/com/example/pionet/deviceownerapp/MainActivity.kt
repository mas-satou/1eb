package com.example.pionet.deviceownerapp

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //画面固定化
        this.startLockTask()

        kioskOnButton.setOnClickListener {
            this.startLockTask()
        }

        kioskOffButton.setOnClickListener {
            this.stopLockTask()
        }
    }
}
