package com.example.android.mystoreinventory;

/**
 * Created by TG on 9/29/17.
 */

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v4.widget.CursorAdapter;

import com.example.android.mystoreinventory.data.InvContract;


public class InvCursorAdapter extends CursorAdapter {

    public InvCursorAdapter(Context context, Cursor c) {
        super(context, c, 0 /* flags */ );
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);

    }

    @Override
    public void bindView (View view, Context context, Cursor cursor){

        TextView nameTextView = (TextView) view.findViewById(R.id.item_name);
        TextView priceTextView = (TextView) view.findViewById(R.id.item_price);
        TextView quantityTextView = (TextView) view.findViewById(R.id.item_quantity);
        Button buttonSale = (Button) view.findViewById(R.id.item_sale_bt);
        ImageView imageImageView = (ImageView) view.findViewById(R.id.item_image);

        int idColumnIndex = cursor.getColumnIndex(InvContract.InvEntry._ID);
        int nameColumnIndex = cursor.getColumnIndex(InvContract.InvEntry.COLUMN_PRODUCT_NAME);
        int priceColumnIndex = cursor.getColumnIndex(InvContract.InvEntry.COLUMN_PRODUCT_PRICE);
        int quantityColumnIndex = cursor.getColumnIndex(InvContract.InvEntry.COLUMN_PRODUCT_QUANTITY);
        int imageColumnIndex = cursor.getColumnIndex(InvContract.InvEntry.COLUMN_PRODUCT_IMAGE_URI);

        String productName = cursor.getString(nameColumnIndex);
        String productPrice = cursor.getString(priceColumnIndex);
        final int productStock = cursor.getInt(quantityColumnIndex);
        String productImage = cursor.getString(imageColumnIndex);

        nameTextView.setText(productName);
        priceTextView.setText(productPrice);
        quantityTextView.setText(String.valueOf(productStock));
        imageImageView.setImageURI(Uri.parse(productImage));
        final int productId = cursor.getInt(idColumnIndex);

        // Set a clickListener on sale button
        buttonSale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Uri currentProductUri = ContentUris.withAppendedId(InvContract.InvEntry.CONTENT_URI, productId);
                reduceProductQuantity(view, productStock, currentProductUri);
            }
        });
    }


    private void reduceProductQuantity(View view, int productStock, Uri uri) {

        if (productStock > 0) {
            productStock--;

            ContentValues values = new ContentValues();
            values.put(InvContract.InvEntry.COLUMN_PRODUCT_QUANTITY, productStock);
            mContext.getContentResolver().update(uri, values, null, null);
        } else {
            Toast.makeText(view.getContext(), "This product has no stock", Toast.LENGTH_SHORT).show();
        }
    }
}
