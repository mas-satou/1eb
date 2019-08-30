package com.pionet.iot_challengeapp

import android.app.admin.DevicePolicyManager
import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import android.graphics.Color
import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.BaseColumns
import android.view.View
import android.view.WindowManager
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintStream
import java.net.HttpURLConnection
import java.net.URL

class MainActivity : AppCompatActivity() {

    var orderList:ArrayList<String> = ArrayList()
    //初期化を遅延する
    lateinit var mDBHelper: MyDBHelper
    lateinit var mDao: MyDBDao


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //初期化する
        mDBHelper = MyDBHelper(this)
        mDao = MyDBDao(mDBHelper)

        //テーブルを消す
        //val db = mDBHelper.writableDatabase
        //db.delete(MenuReaderContract.MenuEntry.TABLE_NAME,null,null)

        //画面固定化
        this.startLockTask()
        //スリープにしない
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        //デバイスオーナー解除ボタンは間違っておせないようにしておく
        DeviceOwnerOffButton.isEnabled = true

        //ステータスバー、ナビゲーションバーを非表示
        hideSystemUI()

        //メニュー表示
        readData(mDao)

        //Kioskモード終了ボタン
        KioskOffButton.setOnClickListener {

            this.stopLockTask()
            //ステータスバー、ナビゲーションバーを表示
            showSystemUI()

        }

        //デバイスオーナー解除ボタン
        DeviceOwnerOffButton.setOnClickListener{
            val dpm = getSystemService(DEVICE_POLICY_SERVICE) as DevicePolicyManager
            dpm.clearDeviceOwnerApp(packageName)

        }

        //とりあえず呼出ボタンにサーバーとの通信機能を与えとく
        CallButton.setOnClickListener {
            HttpResponse().execute("GET","http://192.168.0.200:3000/menuinfo")
        }

        //注文履歴ボタンにはデータベース確認機能を与えとく
//        CheckButton.setOnClickListener {
//            //データベース確認
//            //readData(mDao)
//        }

        OrderButton.setOnClickListener {
            orderList.clear()
            val adapter = ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,orderList)
            listView.adapter = adapter
        }

        //それぞれのボタンを押すとカゴに追加される
        ProductButton0.setOnClickListener {
            countOrderList(ProductButton0.text.toString())
        }


        ProductButton1.setOnClickListener {
            countOrderList(ProductButton1.text.toString())
        }

        ProductButton2.setOnClickListener {
            countOrderList(ProductButton2.text.toString())
        }

        ProductButton3.setOnClickListener {
            countOrderList(ProductButton3.text.toString())
        }
    }

    private fun countOrderList(name:String){
        if(orderList.count() >= 4){
            val toast = Toast.makeText(applicationContext, "4品までしか追加できません",Toast.LENGTH_SHORT)
            val view = toast.view
            view.setBackgroundColor(Color.RED)
            toast.show()
        }else {
            orderList.add(name)
            val adapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, orderList)
            listView.adapter = adapter
        }
    }


    /*
    フルスクリーンで表示する
     */
    private fun hideSystemUI() {
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN)
    }
    /*
    フルスクリーン表示を解除する
     */
    private fun showSystemUI() {
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
    }

    //商品情報テーブルへデータを追加する
    private fun addMenuInfoTable(dao: MyDBDao, result: String?) {

        val parentJsonArray = JSONArray(result)

        for(i in 0..(parentJsonArray.length()-1)) {
            val obj = parentJsonArray.getJSONObject(i)

            //商品IDが存在する場合
            if(obj.has(MenuReaderContract.MenuEntry.COLUMN_NAME_PRODUCTID)){
                val category = obj.getInt(MenuReaderContract.MenuEntry.COLUMN_NAME_CATEGORY)
                val sub_category = obj.getInt(MenuReaderContract.MenuEntry.COLUMN_NAME_SUBCATEGORY)
                val productID = obj.getInt(MenuReaderContract.MenuEntry.COLUMN_NAME_PRODUCTID)
                val productName = obj.getString(MenuReaderContract.MenuEntry.COLUMN_NAME_PRODUCTNAME)
                val price = obj.getInt(MenuReaderContract.MenuEntry.COLUMN_NAME_PRICE)

                //テーブルに追加する情報を作成
                val values = ContentValues().apply {
                    put(MenuReaderContract.MenuEntry.COLUMN_NAME_CATEGORY, category)
                    put(MenuReaderContract.MenuEntry.COLUMN_NAME_SUBCATEGORY, sub_category)
                    put(MenuReaderContract.MenuEntry.COLUMN_NAME_PRODUCTID, productID)
                    put(MenuReaderContract.MenuEntry.COLUMN_NAME_PRODUCTNAME, productName)
                    put(MenuReaderContract.MenuEntry.COLUMN_NAME_PRICE, price)
                }

                //既にテーブルにデータがあるか確認
                if(!checkProductID(mDao, productID)){
                    //ない場合はインサート
                    dao.insert(MenuReaderContract.MenuEntry.TABLE_NAME, values)
                }else{
                    //ある場合はアップデート
                    dao.update(MenuReaderContract.MenuEntry.TABLE_NAME, values, productID.toString())
                }
            }
        }
    }

    //商品IDが重複していないか確認する
    private fun checkProductID(dao: MyDBDao, id:Int) : Boolean{
        var flag = false

        with(dao.getMenuInfoData()){
            while (moveToNext()){
                val productID = getInt(getColumnIndexOrThrow(MenuReaderContract.MenuEntry.COLUMN_NAME_PRODUCTID))
                if(productID == id){
                    //idが存在する
                    flag = true
                }
            }
        }
        return flag
    }

    //データ確認用
    private fun readData(dao: MyDBDao){

        var data:ArrayList<String> = ArrayList()

        with(dao.getMenuInfoData()) {
            while (moveToNext()) {
                val category = getInt(getColumnIndexOrThrow(MenuReaderContract.MenuEntry.COLUMN_NAME_CATEGORY))
                val sub_category = getInt(getColumnIndexOrThrow(MenuReaderContract.MenuEntry.COLUMN_NAME_SUBCATEGORY))
                val productID = getInt(getColumnIndexOrThrow(MenuReaderContract.MenuEntry.COLUMN_NAME_PRODUCTID))
                val productName = getString(getColumnIndexOrThrow(MenuReaderContract.MenuEntry.COLUMN_NAME_PRODUCTNAME))
                val price = getInt(getColumnIndexOrThrow(MenuReaderContract.MenuEntry.COLUMN_NAME_PRICE))
                println("***********")
                println("カテゴリー【" + category + "】")
                println("サブカテゴリー【" + sub_category + "】")
                println("商品ID【"+ productID +"】")
                println("商品名【"+ productName +"】")
                println("価格【"+ price +"】")
                println("***********")

                data.add(productName)
            }
        }

        updateGridView(data)
    }

    //画面反映用
    private fun updateGridView(data:ArrayList<String>){

        if (data.isNotEmpty() && data.size > 0) {

            ProductButton0.text = data[0]
            ProductButton1.text = data[1]
            ProductButton2.text = data[2]

        }
    }


    override fun onDestroy() {
        super.onDestroy()
        mDBHelper.close()
    }

    //通信用
    inner class HttpResponse() : AsyncTask<String, Integer, String>()
    {
        //POSTの時に使う
        //val addJson = "name"

        override fun doInBackground(vararg params: String?): String {

            var jsonText:String = ""

            try
            {
                // url取得
                val url = URL(params[1])
                // 接続用コネクションを作成
                val con = url.openConnection() as HttpURLConnection
                //リクエストメソッドを設定
                con.requestMethod = params[0]

                ///////POSTだった場合
//                if(params[0] == "POST"){
//                    con.doOutput = true;
//                    val os = con.outputStream
//                    val ps = PrintStream(os)
//                    ps.print(addJson)
//                    ps.close()
//                }

                //接続する
                con.connect()
                //レポスンスコード取得
                val responseCode = con.responseCode
                //レスポンスメッセージ取得
                val responseMsg = con.responseMessage

                val stream = con.inputStream
                val reader = BufferedReader(InputStreamReader(stream))
                val buffer = StringBuffer()
                var line: String?
                while (true){
                    line = reader.readLine()
                    if(line == null){
                        break
                    }
                    buffer.append(line)
                }

                //println(jsonText)
                println("コード【" + responseCode + "】　メッセージ【" + responseMsg + "】")

                //bufferをjsonTextに格納
                jsonText = buffer.toString()

                //接続を切る
                con.disconnect()

            }catch (e : Exception)
            {
                println("エラー【" + e.toString() + "】")
            }

            //jsonTextを返す
            return jsonText
        }

        override fun onPostExecute(result: String?) {
            //println("jsonText:" + result)
            super.onPostExecute(result)
            //データベースに追加するデータを作成する
            if(!result.isNullOrEmpty()){
                addMenuInfoTable(mDao, result)
            }
        }
    }

}
