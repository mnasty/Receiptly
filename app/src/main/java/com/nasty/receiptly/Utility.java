package com.nasty.receiptly;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nasty.receiptly.data.DbConstants;

import java.io.File;
import java.util.Calendar;
import java.util.Vector;

import static com.nasty.receiptly.MainActivity.imageFile;

/**
 * Created by Mick on 2/13/17.
 */

// This class is designated for static, multi use helper methods
public class Utility {

    static String logTag = "!!!!";

    public static void deleteImage(Context c)
    {
        boolean success = true;
        Log.d(logTag, "File Exists: " + Boolean.toString(imageFile.exists()));

        if (imageFile.exists()) {
            File file = new File(imageFile.toString());
            success = file.delete();
        }
        if (!success) {
            AlertDialog.Builder builder = new AlertDialog.Builder(c);
            builder.setTitle("Error!");
            builder.setMessage("The receipt photo you took was unable to be deleted. This could be due to the denial of storage permissions to this app. " +
                   "Check to ensure this app has proper access to your phone storage. Please note you will need to delete the picture you just took yourself if you don't want it.");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            // Build the dialog into a alert dialog object
            final AlertDialog receiptDataDialog = builder.show();
            receiptDataDialog.setCanceledOnTouchOutside(false);

            Log.d(logTag, "Image Deletion Error @ Cancel Button On Alert Dialog");
        }
        else
        {
            Log.d(logTag, "Deleted Image Successfully Upon Cancellation");
        }
    }

    @SuppressWarnings("all")
    public static String getFormattedDateTime()
    {
        String date;
        java.text.DateFormat df = new java.text.SimpleDateFormat("MM-dd-yy, h:mm a");
        return date = df.format(Calendar.getInstance().getTime());
    }

    protected static void getReceiptData(final Context c)
    {
        final Context thisContext = c;

        // Create alert dialog builder
        AlertDialog.Builder builder = new AlertDialog.Builder(thisContext);
        builder.setTitle("Enter Receipt Data");

        // Create a layout to contain the multiple children
        LinearLayout alertDLayout = new LinearLayout(thisContext);
        alertDLayout.setPadding(16, 16, 16, 16);
        alertDLayout.setOrientation(LinearLayout.VERTICAL);

        // Set up the input, create the individual edit text view object
        final EditText merchantNameInput = new EditText(thisContext);
        merchantNameInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);
        merchantNameInput.setHint(R.string.view_merchant_name);
        merchantNameInput.setMaxLines(1);
        // Assign it to the layout
        alertDLayout.addView(merchantNameInput);

        final TextView dollarsSpentTextView = new TextView(thisContext);
        dollarsSpentTextView.setText(R.string.view_dollars_spent);
        dollarsSpentTextView.setPadding(0, 24, 0, 0);
        alertDLayout.addView(dollarsSpentTextView);

        final EditText dollarsSpentInput = new EditText(thisContext);
        dollarsSpentInput.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        dollarsSpentInput.setHint(R.string.view_dollars_spent);
        dollarsSpentInput.setText(R.string.view_defvalue_spent);
        alertDLayout.addView(dollarsSpentInput);

        final TextView totalTaxTextView = new TextView(thisContext);
        totalTaxTextView.setText(R.string.view_total_tax);
        alertDLayout.addView(totalTaxTextView);

        final EditText totalTaxInput = new EditText(thisContext);
        totalTaxInput.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        totalTaxInput.setHint("Total Tax Paid on this Transaction");
        totalTaxInput.setText(R.string.view_defvalue_spent);
        alertDLayout.addView(totalTaxInput);

        // Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    // Create content values
                    ContentValues receiptValues = new ContentValues();
                    Vector<ContentValues> valuesVector = new Vector<ContentValues>(4);

                    // Grab human readable date time string
                    String date = getFormattedDateTime();

                    // Put the returned values into the cv's
                    receiptValues.put(DbConstants.ReceiptsEntry.COLUMN_DATE, date);
                    receiptValues.put(DbConstants.ReceiptsEntry.COLUMN_MERCHANT_NAME, merchantNameInput.getText().toString());
                    receiptValues.put(DbConstants.ReceiptsEntry.COLUMN_MONEY_SPENT, dollarsSpentInput.getText().toString());
                    receiptValues.put(DbConstants.ReceiptsEntry.COLUMN_TAX_PAID, totalTaxInput.getText().toString());
                    receiptValues.put(DbConstants.ReceiptsEntry.COLUMN_RECEIPT_IMG_PATH, imageFile.getAbsolutePath());

                    // Throw into a vector to make it easier to convert to an array
                    valuesVector.add(receiptValues);

                    if (valuesVector.size() > 0) {
                        // Convert to an array of cv's
                        ContentValues[] cvArray = new ContentValues[valuesVector.size()];
                        valuesVector.toArray(cvArray);
                        // Run a bulkInsert to the db using the content provider framework
                        thisContext.getContentResolver().bulkInsert(DbConstants.ReceiptsEntry.CONTENT_URI, cvArray);
                    }
                }
                catch (Exception e) {
                    Log.d(logTag, e.toString());
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteImage(thisContext);
                dialog.cancel();
            }
        });

        // Set the dialog builder to inflate the programmatic layout
        builder.setView(alertDLayout);
        // Build the dialog into a alert dialog object
        final AlertDialog receiptDataDialog = builder.show();
        receiptDataDialog.setCanceledOnTouchOutside(false);
        // Get the ok button
        final Button okButton = receiptDataDialog.getButton(AlertDialog.BUTTON_POSITIVE);
        // Disable the button until proper data is entered
        okButton.setEnabled(false);
        // Set a TextChanged listener to handle the checks for proper data
        merchantNameInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //input validation every time the text changes at all
                //(not null fields)
                if (merchantNameInput.getText().toString().isEmpty() || dollarsSpentInput.getText().toString().isEmpty() || totalTaxInput.getText().toString().isEmpty()) {

                    okButton.setEnabled(false);
                }
                else
                {
                    okButton.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }
}
