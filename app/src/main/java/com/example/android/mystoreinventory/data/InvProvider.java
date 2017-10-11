package com.example.android.mystoreinventory.data;

/**
 * Created by TG on 9/29/17.
 */

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import com.example.android.mystoreinventory.data.InvContract.InvEntry;

public class InvProvider extends ContentProvider {

    public static final String LOG_TAG = InvProvider.class.getSimpleName();

    private static final int INV = 100;

    private static final int INV_ID = 101;



    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sUriMatcher.addURI(InvContract.CONTENT_AUTHORITY, InvContract.PATH_INV, INV);

        sUriMatcher.addURI(InvContract.CONTENT_AUTHORITY, InvContract.PATH_INV + "/#", INV_ID);
    }

    private InvDbHelper mDbHelper;

    @Override
    public boolean onCreate() {
        mDbHelper = new InvDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        Cursor cursor;

        int match = sUriMatcher.match(uri);
        switch (match) {
            case INV:
                cursor = database.query(InvEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case INV_ID:
                selection = InvEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                cursor = database.query(InvEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case INV:
                return insertInv(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    private Uri insertInv(Uri uri, ContentValues values) {
        String name = values.getAsString(InvEntry.COLUMN_PRODUCT_NAME);
        if (name == null) {
            throw new IllegalArgumentException("Insertion is not supported for name " + uri);
        }

        String imageUri = values.getAsString(InvEntry.COLUMN_PRODUCT_IMAGE_URI);
        if (imageUri == null) {
            throw new IllegalArgumentException("Insertion is not supported for image " + uri);
        }

        Integer quantity = values.getAsInteger(InvEntry.COLUMN_PRODUCT_QUANTITY);
        if (quantity == null || quantity < 0) {
            throw new IllegalArgumentException("Insertion is not supported for quantity " + uri);
        }

        Integer price = values.getAsInteger(InvEntry.COLUMN_PRODUCT_PRICE);
        if (price == null || price < 0) {
            throw new IllegalArgumentException("Insertion is not supported for price" + uri);
        }

        String phone = values.getAsString(InvEntry.COLUMN_SUPPLIER_PHONE);
        if (phone == null) {
            throw new IllegalArgumentException("Insertion is not supported for phone" + uri);
        }

        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        long id = db.insert(InvEntry.TABLE_NAME, null, values);
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection,
                      String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case INV:
                return updateInv(uri, contentValues, selection, selectionArgs);
            case INV_ID:
                selection = InvEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateInv(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    private int updateInv(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        if (values.containsKey(InvEntry.COLUMN_PRODUCT_NAME)) {
            String name = values.getAsString(InvEntry.COLUMN_PRODUCT_NAME);
            if (name == null) {
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
            }
        }

        if (values.containsKey(InvEntry.COLUMN_PRODUCT_IMAGE_URI)) {
            String imageUri = values.getAsString(InvEntry.COLUMN_PRODUCT_IMAGE_URI);
            if (imageUri == null) {
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
            }
        }

        if (values.containsKey(InvEntry.COLUMN_PRODUCT_QUANTITY)) {
            Integer quantity = values.getAsInteger(InvEntry.COLUMN_PRODUCT_QUANTITY);
            if (quantity == null || quantity < 0) {
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
            }
        }

        if (values.containsKey(InvEntry.COLUMN_PRODUCT_PRICE)) {
            Integer price = values.getAsInteger(InvEntry.COLUMN_PRODUCT_PRICE);
            if (price == null || price < 0) {
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
            }
        }

        if (values.containsKey(InvEntry.COLUMN_SUPPLIER_PHONE)) {
            String phone = values.getAsString(InvEntry.COLUMN_SUPPLIER_PHONE);
            if (phone == null) {
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
            }
        }

        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        int rowsUpdated = db.update(InvEntry.TABLE_NAME, values, selection, selectionArgs);
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }


    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        int rowsDeleted;

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case INV:
                rowsDeleted = database.delete(InvEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case INV_ID:
                selection = InvEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = database.delete(InvEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }

        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsDeleted;
    }

    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case INV:
                return InvEntry.CONTENT_LIST_TYPE;
            case INV_ID:
                return InvEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }
}