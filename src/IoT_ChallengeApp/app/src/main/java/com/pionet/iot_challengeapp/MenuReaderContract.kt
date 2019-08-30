package com.pionet.iot_challengeapp

import android.provider.BaseColumns

object MenuReaderContract {
    object MenuEntry : BaseColumns {
        const val TABLE_NAME = "menu_info"
        const val COLUMN_NAME_CATEGORY = "category"
        const val COLUMN_NAME_SUBCATEGORY = "sub_category"
        const val COLUMN_NAME_PRODUCTID = "product_id"
        const val COLUMN_NAME_PRODUCTNAME = "product_name"
        const val COLUMN_NAME_PRICE = "price"
    }
}