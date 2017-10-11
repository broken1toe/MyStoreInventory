package com.example.android.mystoreinventory;

/**
 * Created by TG on 9/29/17.
 */

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.mystoreinventory.data.InvContract.InvEntry;

import static java.lang.Double.parseDouble;
import static java.lang.Integer.parseInt;

public class EditorActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    public static final String LOG_TAG = EditorActivity.class.getSimpleName();

    private static final int EXISTING_INV_LOADER = 0;
    private static final int RESULT_LOAD_IMAGE = 1;
    private Uri mCurrentInvUri;
    private Uri mImageUri;
    private EditText mNameEditText;
    private EditText mPriceEditText;
    private TextView mQuantityEditText;
    private EditText mPhoneEditText;
    private ImageView imageView;
    private Button mReduceStockButton;
    private Button mIncreaseStockButton;
    private Button mPhoneButton;
    private EditText mStockChangeEditText;

    private boolean mInvHasChanged = false;

    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mInvHasChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editor_activity);

        Intent intent = getIntent();
        mCurrentInvUri = intent.getData();

        if (mCurrentInvUri == null) {
            setTitle(getString(R.string.editor_activity_add_new_product));

            invalidateOptionsMenu();
        } else {
            setTitle(getString(R.string.editor_activity_edit_new_product));

            getLoaderManager().initLoader(EXISTING_INV_LOADER, null, this);
        }

        mNameEditText = (EditText) findViewById(R.id.edit_name_product);
        mPriceEditText = (EditText) findViewById(R.id.edit_price_product);
        mQuantityEditText = (TextView) findViewById(R.id.edit_quantity_product);
        mPhoneEditText = (EditText) findViewById(R.id.supplier_phone);
        imageView = (ImageView) findViewById(R.id.pic);
        mReduceStockButton = (Button) findViewById(R.id.detail_quantity_minus);
        mIncreaseStockButton = (Button) findViewById(R.id.detail_quantity_add);
        mPhoneButton = (Button) findViewById(R.id.supplier_order);
        mStockChangeEditText = (EditText) findViewById(R.id.edit_stock_change);


        mNameEditText.setOnTouchListener(mTouchListener);
        mPriceEditText.setOnTouchListener(mTouchListener);
        mQuantityEditText.setOnTouchListener(mTouchListener);
        mPhoneEditText.setOnTouchListener(mTouchListener);
        imageView.setOnTouchListener(mTouchListener);
        mStockChangeEditText.setOnTouchListener(mTouchListener);

        mReduceStockButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String currentProduct = mNameEditText.getText().toString();
                String toastMessage = null;
                int currentStock = parseInt(mQuantityEditText.getText().toString());
                int changeStock;
                int newStockholding;

                if (mStockChangeEditText.getText().toString().trim().length() == 0) {
                    newStockholding = currentStock - 1;
                } else {
                    changeStock = parseInt(mStockChangeEditText.getText().toString());
                    newStockholding = currentStock - changeStock;
                }

                if (newStockholding == 0) {
                    toastMessage = "You are out of stock of " + currentProduct + ".  Place an order";
                    mQuantityEditText.setText(String.valueOf(newStockholding));
                } else if (newStockholding > 0) {
                    toastMessage = "Stock of " + currentProduct + " has reduced from " + currentStock + " to " + newStockholding;
                    mQuantityEditText.setText(String.valueOf(newStockholding));
                } else {
                    toastMessage = "You can't reduce your stock of " + currentProduct + " by more than your current stockholding";
                }

                Toast.makeText(view.getContext(), toastMessage, Toast.LENGTH_LONG).show();
                mStockChangeEditText.setText((""));
            }
        });

        mIncreaseStockButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String currentProduct = mNameEditText.getText().toString();
                int currentStock = parseInt(mQuantityEditText.getText().toString());
                int changeStock;
                int newStockholding;

                if (mStockChangeEditText.getText().toString().trim().length() == 0) {
                    newStockholding = currentStock + 1;
                } else {
                    changeStock = parseInt(mStockChangeEditText.getText().toString());
                    newStockholding = currentStock + changeStock;
                }

                mQuantityEditText.setText(String.valueOf(newStockholding));
                String toastMessage = "Your stock of " + currentProduct + " has increased from " + currentStock + " to " + newStockholding;

                Toast.makeText(view.getContext(), toastMessage, Toast.LENGTH_LONG).show();
                mStockChangeEditText.setText((""));
            }
        });

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonImageClick();
            }
        });

        mPhoneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String orderQty = mPhoneEditText.getText().toString().trim();
                mPhoneEditText.setText("");
                if (orderQty.length() != 0) {
                    String productName = mNameEditText.getText().toString().trim();

                    String phoneNumber = "tel :" + mPhoneEditText;

                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse(phoneNumber));
                    if (intent.resolveActivity(getPackageManager()) != null) {
                        startActivity(intent);
                    }

                } else {
                    String toastMessage = "Phone Number required";
                    Toast.makeText(getApplicationContext(), toastMessage, Toast.LENGTH_LONG).show();
                }

            }
        });
    }

    private boolean saveInv() {
        String nameString = mNameEditText.getText().toString().trim();
        String priceString = mPriceEditText.getText().toString().trim();
        String quantityString = mQuantityEditText.getText().toString().trim();
        String phoneString = mPhoneEditText.getText().toString().trim();

        if (nameString.length() == 0) {
            Toast.makeText(this, "Please add a product name", Toast.LENGTH_LONG).show();
            return false;
        }
        if (priceString.length() == 0 || parseDouble(priceString) < 0) {
            Toast.makeText(this, "Please add a valid price number", Toast.LENGTH_LONG).show();
            return false;
        }

        if (quantityString.length() == 0 || parseInt(quantityString) < 0) {
            Toast.makeText(this, "Please add a valid quantity number", Toast.LENGTH_LONG).show();
            return false;
        }

        if (phoneString.length() == 0) {
            Toast.makeText(this, "Please add a supplier phone number", Toast.LENGTH_LONG).show();
            return false;
        }
        if (mImageUri == null) {
            Toast.makeText(this, "Please add a product picture", Toast.LENGTH_LONG).show();
            return false;
        }

        ContentValues values = new ContentValues();
        values.put(InvEntry.COLUMN_PRODUCT_NAME, nameString);
        values.put(InvEntry.COLUMN_PRODUCT_PRICE, priceString);
        values.put(InvEntry.COLUMN_PRODUCT_QUANTITY, quantityString);
        values.put(InvEntry.COLUMN_SUPPLIER_PHONE, phoneString);


        if (mImageUri == null) {
            mImageUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE +
                    "://" + getResources().getResourcePackageName(R.drawable.sad_smile)
                    + '/' + getResources().getResourceTypeName(R.drawable.sad_smile)
                    + '/' + getResources().getResourceEntryName(R.drawable.sad_smile));
        }
        values.put(InvEntry.COLUMN_PRODUCT_IMAGE_URI, mImageUri.toString());

        if (mCurrentInvUri == null) {
            Uri newUri = getContentResolver().insert(InvEntry.CONTENT_URI, values);

            if (newUri == null) {
                Toast.makeText(this, getString(R.string.editor_insert_product_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.editor_insert_product_successful),
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            int rowsAffected = getContentResolver().update(mCurrentInvUri, values, null, null);

            if (rowsAffected == 0) {
                Toast.makeText(this, getString(R.string.editor_update_product_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.editor_insert_product_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && data != null) {
            mImageUri = data.getData();
            imageView.setImageURI(mImageUri);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void buttonImageClick() {
        Intent intent;

        if (Build.VERSION.SDK_INT < 19) {
            intent = new Intent(Intent.ACTION_GET_CONTENT);
        } else {
            intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
        }
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), RESULT_LOAD_IMAGE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (mCurrentInvUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                saveInv();
                finish();
                return true;
            case R.id.action_delete:
                showDeleteConfirmationDialog();
                return true;
            case android.R.id.home:
                if (!mInvHasChanged) {
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;
                }

                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                NavUtils.navigateUpFromSameTask(EditorActivity.this);
                            }
                        };

                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (!mInvHasChanged) {
            super.onBackPressed();
            return;
        }

        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                };

        showUnsavedChangesDialog(discardButtonClickListener);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projection = {
                InvEntry._ID,
                InvEntry.COLUMN_PRODUCT_IMAGE_URI,
                InvEntry.COLUMN_PRODUCT_NAME,
                InvEntry.COLUMN_PRODUCT_PRICE,
                InvEntry.COLUMN_PRODUCT_QUANTITY,
                InvEntry.COLUMN_SUPPLIER_PHONE};

        return new CursorLoader(this,
                mCurrentInvUri,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        if (cursor.moveToFirst()) {
            int nameColumnIndex = cursor.getColumnIndex(InvEntry.COLUMN_PRODUCT_NAME);
            int priceColumnIndex = cursor.getColumnIndex(InvEntry.COLUMN_PRODUCT_PRICE);
            int quantityColumnIndex = cursor.getColumnIndex(InvEntry.COLUMN_PRODUCT_QUANTITY);
            int phoneColumnIndex = cursor.getColumnIndex(InvEntry.COLUMN_SUPPLIER_PHONE);
            int imageColumnIndex = cursor.getColumnIndex(InvEntry.COLUMN_PRODUCT_IMAGE_URI);


            String name = cursor.getString(nameColumnIndex);
            double price = cursor.getDouble(priceColumnIndex);
            int quantity = cursor.getInt(quantityColumnIndex);
            String phone = cursor.getString(phoneColumnIndex);
            String image = cursor.getString(imageColumnIndex);

            mNameEditText.setText(name);
            mPriceEditText.setText(Double.toString(price));
            mQuantityEditText.setText(String.valueOf(quantity));
            mPhoneEditText.setText(phone);
            mImageUri = Uri.parse(image);
            imageView.setImageURI(mImageUri);

        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mNameEditText.setText("");
        mPriceEditText.setText("");
        mQuantityEditText.setText(String.valueOf(String.valueOf(0)));
        mPhoneEditText.setText("");
        imageView.setImageResource(R.drawable.sad_smile);
    }

    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.action_delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deleteInv();
            }
        });
        builder.setNegativeButton(R.string.action_cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void deleteInv() {
        if (mCurrentInvUri != null) {
            int rowsDeleted = getContentResolver().delete(mCurrentInvUri, null, null);

            if (rowsDeleted == 0) {
                Toast.makeText(this, getString(R.string.editor_delete_product_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.editor_delete_product_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }

        finish();
    }
}