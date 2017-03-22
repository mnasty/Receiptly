package com.nasty.receiptly.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Defines table and column names for the weather database.
 */
public class DbConstants {

    static String logTag = "!!!!";

    public static final String CONTENT_AUTHORITY = "com.nasty.receiptly.provider";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    // Possible paths (appended to base content URI for possible URI's)
    public static final String PATH_RECEIPTS = "receipts";

    // Inner class that defines the table contents
    public static final class ReceiptsEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_RECEIPTS).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_RECEIPTS;

        public static final String TABLE_NAME = "receipts";

        // Date, stored in a human readable format as a string (formatted at runtime)
        public static final String COLUMN_DATE = "date";

        // Merchant name as entered
        public static final String COLUMN_MERCHANT_NAME = "merchant_name";

        // Total money spent as entered
        public static final String COLUMN_MONEY_SPENT = "money_spent";

        // Total tax paid as entered
        public static final String COLUMN_TAX_PAID = "tax_paid";

        // Image thumbnail stored in db
        public static final String COLUMN_RECEIPT_IMG_PATH = "receipt_img";
    }
}
