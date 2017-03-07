package com.nasty.receiptly.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Manages a local database for weather data.
 */
public class DbSchema extends SQLiteOpenHelper {

    // Incremented Db Version
    private static final int DATABASE_VERSION = 1;

    public static final String DATABASE_NAME = "receipts.db";

    Context sqlHelperContext;

    public DbSchema(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        sqlHelperContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        // Create table for receipts
        final String SQL_CREATE_RECEIPTS_TABLE = "CREATE TABLE " + DbConstants.ReceiptsEntry.TABLE_NAME + " (" +
                DbConstants.ReceiptsEntry._ID + " INTEGER PRIMARY KEY, " +
                DbConstants.ReceiptsEntry.COLUMN_DATE + " TEXT UNIQUE NOT NULL, " +
                DbConstants.ReceiptsEntry.COLUMN_MERCHANT_NAME + " TEXT NOT NULL, " +
                DbConstants.ReceiptsEntry.COLUMN_MONEY_SPENT + " REAL NOT NULL, " +
                DbConstants.ReceiptsEntry.COLUMN_TAX_PAID + " REAL NOT NULL " +
                //for debugging purposes we wont require the image to be in the db yet
                // TODO: downsize and save receipt image as a thumbnail in SQLite db or android system allocated app private storage
                //DbConstants.ReceiptsEntry.COLUMN_RECEIPT_IMG + " BLOB " +
                " );";

        sqLiteDatabase.execSQL(SQL_CREATE_RECEIPTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        //! Note: the current database upgrade policy is to simply to discard the data and start over
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + DbConstants.ReceiptsEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
