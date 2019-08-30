package com.pionet.iot_challengeapp

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.provider.BaseColumns


class MyDBHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    // 商品情報テーブル作成用SQL
    val CREATE_MENUINFO_TABLE = "CREATE TABLE ${MenuReaderContract.MenuEntry.TABLE_NAME} (" +
            "${BaseColumns._ID} INTEGER PRIMARY KEY, " +
            "${MenuReaderContract.MenuEntry.COLUMN_NAME_CATEGORY} INTEGER NOT NULL, " +
            "${MenuReaderContract.MenuEntry.COLUMN_NAME_SUBCATEGORY} INTEGER NOT NULL, " +
            "${MenuReaderContract.MenuEntry.COLUMN_NAME_PRODUCTID} INTEGER NOT NULL, " +
            "${MenuReaderContract.MenuEntry.COLUMN_NAME_PRODUCTNAME} TEXT NOT NULL, " +
            "${MenuReaderContract.MenuEntry.COLUMN_NAME_PRICE} INTEGER NOT NULL);"

    override fun onCreate(db: SQLiteDatabase) {
        // 各テーブルを作成
        db.execSQL(CREATE_MENUINFO_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {

    }

    //staticとして使える
    companion object {
        const val DATABASE_VERSION = 1
        const val DATABASE_NAME = "My_DB"
    }
}