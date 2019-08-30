package com.pionet.iot_challengeapp

import android.content.ContentValues
import android.database.Cursor
import android.provider.BaseColumns

public class MyDBDao(dbHelper: MyDBHelper) {
    val mDBHelper = dbHelper
    val SECTION = "${MenuReaderContract.MenuEntry.COLUMN_NAME_PRODUCTID} = ?"
    val INSERT_ERROR:Long = -1

    //データをインサートする
    fun insert(table:String, values: ContentValues){
        val db = mDBHelper.writableDatabase

        val result = db.insert(table, null, values)
        if(result == INSERT_ERROR){
            println("商品ID【" + values.getAsInteger(MenuReaderContract.MenuEntry.COLUMN_NAME_PRODUCTID) + "】を追加できませんでした")
        }else {
            println("商品ID【" + values.getAsInteger(MenuReaderContract.MenuEntry.COLUMN_NAME_PRODUCTID) + "】を追加しました")
        }
    }

    //データをアップデートする
    //配列を引数にするときはvarargをつける
    fun update(table: String, values: ContentValues, vararg productID:String){
        val db = mDBHelper.writableDatabase
        db.update(table, values, SECTION, productID)
        println("商品ID【" + values.getAsInteger(MenuReaderContract.MenuEntry.COLUMN_NAME_PRODUCTID) + "】をアップデートしました")
    }

    //商品情報テーブルのデータを取得する
    fun getMenuInfoData() : Cursor{
        val db = mDBHelper.readableDatabase
        //抜き出す対象のデータ
        val projection = arrayOf(
            BaseColumns._ID,
            MenuReaderContract.MenuEntry.COLUMN_NAME_CATEGORY,
            MenuReaderContract.MenuEntry.COLUMN_NAME_SUBCATEGORY,
            MenuReaderContract.MenuEntry.COLUMN_NAME_PRODUCTID,
            MenuReaderContract.MenuEntry.COLUMN_NAME_PRODUCTNAME,
            MenuReaderContract.MenuEntry.COLUMN_NAME_PRICE
        )
        //データを抜き出す
        val cursor = db.query(
            MenuReaderContract.MenuEntry.TABLE_NAME,
            projection,
            null,
            null,
            null,
            null,
            null
        )
        return cursor
    }


}