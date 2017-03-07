package com.nasty.receiptly;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

import com.nasty.receiptly.data.DbConstants;
import com.nasty.receiptly.data.DbSchema;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

public class TestDb extends AndroidTestCase {

    public static final String LOG_TAG = TestDb.class.getSimpleName();

    // Since we want each test to start with a clean slate
    void deleteTheDatabase() {
        mContext.deleteDatabase(DbSchema.DATABASE_NAME);
    }

    public void setUp() {
        deleteTheDatabase();
    }

    static ContentValues createTestContentValues() {
        ContentValues testValues = new ContentValues();

        testValues.put(DbConstants.ReceiptsEntry.COLUMN_DATE, "02-21-17 5:34 PM");
        testValues.put(DbConstants.ReceiptsEntry.COLUMN_MERCHANT_NAME, "Merchant Name");
        testValues.put(DbConstants.ReceiptsEntry.COLUMN_MONEY_SPENT, "2.22");
        testValues.put(DbConstants.ReceiptsEntry.COLUMN_TAX_PAID, "0.22");

        return testValues;
    }

    static void validateCurrentRecord(String error, Cursor valueCursor, ContentValues expectedValues) {
        Set<Map.Entry<String, Object>> valueSet = expectedValues.valueSet();
        for (Map.Entry<String, Object> entry : valueSet) {
            String columnName = entry.getKey();
            int idx = valueCursor.getColumnIndex(columnName);
            assertFalse("Column '" + columnName + "' not found. " + error, idx == -1);
            String expectedValue = entry.getValue().toString();
            assertEquals("Value '" + entry.getValue().toString() +
                    "' did not match the expected value '" +
                    expectedValue + "'. " + error, expectedValue, valueCursor.getString(idx));
        }
    }

    public void testCreateDb() throws Throwable {
        // Build a HashSet of all of the table names we wish to look for
        final HashSet<String> tableNameHashSet = new HashSet<String>();
        tableNameHashSet.add(DbConstants.ReceiptsEntry.TABLE_NAME);

        mContext.deleteDatabase(DbSchema.DATABASE_NAME);
        SQLiteDatabase db = new DbSchema(
                this.mContext).getWritableDatabase();
        assertEquals(true, db.isOpen());

        // Have we created the tables we want?
        Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);

        assertTrue("Error: Database has not been created correctly",
                c.moveToFirst());

        // Verify that the tables have been created
        do {
            tableNameHashSet.remove(c.getString(0));
        } while (c.moveToNext());

        assertTrue("Error: Database was created without tables",
                tableNameHashSet.isEmpty());

        // Do our tables contain the correct columns?
        c = db.rawQuery("PRAGMA table_info(" + DbConstants.ReceiptsEntry.TABLE_NAME + ")",
                null);

        assertTrue("Error: Unable to query the database for table information.",
                c.moveToFirst());

        // Build a HashSet of all of the column names we want to look for
        final HashSet<String> locationColumnHashSet = new HashSet<String>();
        locationColumnHashSet.add(DbConstants.ReceiptsEntry._ID);
        locationColumnHashSet.add(DbConstants.ReceiptsEntry.COLUMN_DATE);
        locationColumnHashSet.add(DbConstants.ReceiptsEntry.COLUMN_MERCHANT_NAME);
        locationColumnHashSet.add(DbConstants.ReceiptsEntry.COLUMN_MONEY_SPENT);
        locationColumnHashSet.add(DbConstants.ReceiptsEntry.COLUMN_TAX_PAID);

        int columnNameIndex = c.getColumnIndex("name");
        do {
            String columnName = c.getString(columnNameIndex);
            locationColumnHashSet.remove(columnName);
        } while (c.moveToNext());

        assertTrue("Error: The database doesn't contain all of the defined columns",
                locationColumnHashSet.isEmpty());
        db.close();
    }

    public void testBulkInsert() {
        // Clear the db to test the bulk insert functionality properly
        setUp();
        // Get reference to writable database
        DbSchema dbHelper = new DbSchema(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Create a test vector to throw our test cv's into for bulk insert testing purposes
        Vector<ContentValues> testVector = new Vector<ContentValues>(4);
        testVector.add(createTestContentValues());
        ContentValues[] testCvArray;
        if (testVector.size() > 0)
        {
            testCvArray = new ContentValues[testVector.size()];
            testVector.toArray(testCvArray);
        }
        else
        {
            testCvArray = null;
        }

        assertFalse("Error: Somehow the content values did not make it into the vector", testCvArray == null);

        // Insert ContentValues via db.bulkInsert and get a row ID back
        long locationRowIdBulkInsert = getContext().getContentResolver().bulkInsert(DbConstants.ReceiptsEntry.CONTENT_URI, testCvArray);

        // Verify that a row was returned
        assertTrue(locationRowIdBulkInsert != -1);

        // Get a cursor to validate the data
        Cursor cursor = db.query(
                DbConstants.ReceiptsEntry.TABLE_NAME,  // Table to Query
                null, // all columns
                null, // Columns for the "where" clause
                null, // Values for the "where" clause
                null, // columns to group by
                null, // columns to filter by row groups
                null // sort order
        );

        // Move the cursor to a valid database row and check to see if we got any records back
        // from the query
        assertTrue( "Error: No Records returned from table query", cursor.moveToFirst());

        // Validate data in resulting Cursor with the original ContentValues
        validateCurrentRecord("Error: Somehow different values exist in the db than were put in",
                cursor, createTestContentValues());

        // Move the cursor to demonstrate that there is only one record in the database
        assertFalse( "Error: More than one record returned from table query",
                cursor.moveToNext() );

        cursor.close();
        db.close();
    }

    public void testReceiptsTable() {
        // Clear the db to test insert functionality properly
        setUp();
        // Get reference to writable database
        DbSchema dbHelper = new DbSchema(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Insert ContentValues via db.insert into database and get a row ID back
        long locationRowId = db.insert(DbConstants.ReceiptsEntry.TABLE_NAME, null, createTestContentValues());

        // Verify we got a row back.
        assertTrue(locationRowId != -1);

        // Get a cursor to validate the data
        Cursor cursor = db.query(
                DbConstants.ReceiptsEntry.TABLE_NAME,  // Table to Query
                null, // all columns
                null, // Columns for the "where" clause
                null, // Values for the "where" clause
                null, // columns to group by
                null, // columns to filter by row groups
                null // sort order
        );

        // Move the cursor to a valid database row and check to see if we got any records back
        // from the query
        assertTrue( "Error: No Records returned from table query", cursor.moveToFirst() );

        // Validate data in resulting Cursor with the original ContentValues
        validateCurrentRecord("Error: Somehow different values exist in the db than were put in",
                cursor, createTestContentValues());

        // Move the cursor to demonstrate that there is only one record in the database
        assertFalse( "Error: More than one record returned from table query",
                cursor.moveToNext() );

        cursor.close();
        db.close();
    }
}