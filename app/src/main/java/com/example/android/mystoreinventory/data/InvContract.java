package com.example.android.mystoreinventory.data;

/**
 * Created by TG on 9/29/17.
 */

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;


public final class InvContract {

    public static final String CONTENT_AUTHORITY = "com.example.android.inventory";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_INV = "inventory";

    private InvContract() {
    }

    public static final class InvEntry implements BaseColumns {

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_INV);

        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_INV;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_INV;

        public final static String TABLE_NAME = "product";

        public final static String _ID = BaseColumns._ID;

        public static final String COLUMN_PRODUCT_IMAGE_URI = "image";

        public static final String COLUMN_PRODUCT_NAME = "name";

        public static final String COLUMN_PRODUCT_QUANTITY = "quantity";

        public static final String COLUMN_PRODUCT_PRICE = "price";

        public static final String COLUMN_SUPPLIER_PHONE = "phone";

    }

}