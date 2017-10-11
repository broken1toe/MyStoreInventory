package com.example.android.mystoreinventory.data;

/**
 * Created by TG on 9/29/17.
 */

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.android.mystoreinventory.data.InvContract.InvEntry;

public class InvDbHelper extends SQLiteOpenHelper {

    public static final String LOG_TAG = InvDbHelper.class.getSimpleName();

    private static final String DATABASE_NAME = "inventory.db";

    private static final int DATABASE_VERSION = 1;

    public InvDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String SQL_CREATE_PRODUCT_TABLE = "CREATE TABLE " + InvEntry.TABLE_NAME + "("
                + InvEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + InvEntry.COLUMN_PRODUCT_NAME + " TEXT NOT NULL, "
                + InvEntry.COLUMN_PRODUCT_IMAGE_URI + " TEXT, "
                + InvEntry.COLUMN_PRODUCT_QUANTITY + " INTEGER NOT NULL DEFAULT 0, "
                + InvEntry.COLUMN_PRODUCT_PRICE + " FLOAT NOT NULL, "
                + InvEntry.COLUMN_SUPPLIER_PHONE + " TEXT)";

        db.execSQL(SQL_CREATE_PRODUCT_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}