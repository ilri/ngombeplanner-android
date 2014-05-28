package org.cgiar.ilri.mistro.farmer.backend.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.Serializable;

/**
 * This class handles transactions to the SQLite database
 * Please handle with care and always call methods herein in an asynchronous thread (asynchronous to the UI thread)
 *
 * Created by jrogena on 03/04/14.
 */
public class DatabaseHelper extends SQLiteOpenHelper implements Serializable{

    public static final String DB_NAME = "ngombe_planner";
    public static final int DB_VERSION = 10;
    public static final String TABLE_FARMER = "farmer";
    public static final String TABLE_COW = "cow";
    public static final String TABLE_EVENT = "event";
    public static final String TABLE_CACHED_REQUESTS = "cached_requests";
    public static final String TABLE_MILK_PRODUCTION = "milk_production";
    public static final String TABLE_EVENTS_CONSTRAINTS = "events_constraints";

    private static final String TAG = "DatabaseHelper";

    /**
     * Constructor for the DatabaseHelper class
     *
     * @param context   Context e.g activity/service requesting for the data
     */
    public DatabaseHelper(Context context){
        super(context, DB_NAME, null, DB_VERSION);
        Log.d(TAG, "Database version = "+DB_VERSION);
    }

    /**
     * This should be called only when the database does not exist or a new version of the database has been defined
     *
     * @param db    The writable database
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_FARMER + " (id INTEGER PRIMARY KEY, name TEXT, mobile_no TEXT, location_county TEXT, location_district TEXT, gps_longitude TEXT, gps_latitude TEXT, date_added TEXT, sim_card_sn TEXT);");
		db.execSQL("CREATE TABLE " + TABLE_COW + " (id INTEGER PRIMARY KEY, farmer_id INTEGER, name TEXT, ear_tag_number TEXT, date_of_birth TEXT, age INTEGER, age_type TEXT, sex TEXT, sire_id INTEGER, dam_id INTEGER, date_added TEXT, service_type TEXT, country_id INTEGER, bull_owner TEXT, owner_name TEXT);");
        db.execSQL("CREATE TABLE "+TABLE_EVENT+" (id INTEGER PRIMARY KEY, cow_id INTEGER, event_name TEXT, remarks TEXT, event_date TEXT, birth_type TEXT, parent_cow_event INTEGER, bull_id INTEGER, servicing_days INTEGER, cod TEXT, no_of_live_births INTEGER, saved_on_server INTEGER, date_added TEXT)");
        db.execSQL("CREATE TABLE "+TABLE_CACHED_REQUESTS+" (id INTEGER PRIMARY KEY AUTOINCREMENT, url TEXT, json TEXT)");
        db.execSQL("CREATE TABLE "+TABLE_MILK_PRODUCTION+" (id INTEGER PRIMARY KEY, cow_id INTEGER, time TEXT, quantity INTEGER, date_added TEXT, date TEXT, quantity_type TEXT)");
        db.execSQL("CREATE TABLE "+TABLE_EVENTS_CONSTRAINTS+" (id INTEGER PRIMARY KEY, event TEXT, time INTEGER, time_units TEXT)");
        //insert any static data to the db now
    }

    /**
     * Called when a new version of the database is detected
     *      (i.e when the constructor is called with an updated version number)
     *
     * @param db    the writable database
     * @param oldVersion    the old version number for the database
     * @param newVersion    the new version number for the database
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(TAG, "About to update the database. All data will be lost");
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_COW);
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_FARMER);
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_EVENT);
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_CACHED_REQUESTS);
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_MILK_PRODUCTION);
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_EVENTS_CONSTRAINTS);

        //recreate the database
        onCreate(db);
    }

    /**
     * This method is use dto run select queries to the database
     *
     * @param db    The readable database
     * @param table The name of the table where the select query is to be run
     * @param columns   An array of column names to be fetched in the query
     * @param selection The selection criteria in the form column=value
     * @param selectionArgs You may include ?s in selection, which will be replaced by the values from selectionArgs, in order that they appear in the selection. The values will be bound as Strings.
     * @param groupBy   A filter declaring how to group rows, formatted as an SQL GROUP BY clause (excluding the GROUP BY itself). Passing null will cause the rows to not be grouped.
     * @param having    A filter declare which row groups to include in the cursor, if row grouping is being used, formatted as an SQL HAVING clause (excluding the HAVING itself). Passing null will cause all row groups to be included, and is required when row grouping is not being used.
     * @param orderBy   How to order the rows, formatted as an SQL ORDER BY clause (excluding the ORDER BY itself). Passing null will use the default sort order, which may be unordered.
     * @param limit Limits the number of rows returned by the query, formatted as LIMIT clause. Passing null denotes no LIMIT clause.
     *
     * @return  A multidimensional array in the form array[selected_rows][selected_columns]
     */
    public String[][] runSelectQuery(SQLiteDatabase db, String table, String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy, String limit) {

        Log.d(TAG, "About to run select query on " + table + " table");
        Cursor cursor=db.query(table, columns, selection, selectionArgs, groupBy, having, orderBy, limit);
        if(cursor.getCount()!=-1) {
            String[][] result=new String[cursor.getCount()][columns.length];
            Log.d(TAG, "number of rows " + String.valueOf(cursor.getCount()));
            int c1=0;
            cursor.moveToFirst();
            while(c1<cursor.getCount()) {
                int c2=0;
                while(c2<columns.length) {
                    String currResult = cursor.getString(c2);
                    if(currResult == null || currResult.equals("null"))
                        currResult = "";//nulls from server not handled well by json. Set 'null' and null to empty string

                    result[c1][c2] = currResult;
                    c2++;
                }
                if(c1!=cursor.getCount()-1) {//is not the last row
                    cursor.moveToNext();
                }
                c1++;
            }
            cursor.close();

            return result;
        }
        else {
            return null;
        }
    }

    /**
     * This method deletes rows form a table
     *
     * @param db    The writable database
     * @param table The table from which rows are to be deleted
     * @param referenceColumn   Column to be used as a reference for the delete
     * @param columnValues   The values of the reference column. All rows with these values will be deleted
     */
    public void runDeleteQuery(SQLiteDatabase db, String table, String referenceColumn, String[] columnValues) {
        Log.d(TAG, "About to run delete query on "+table+" table");

        db.delete(table, referenceColumn+"=?", columnValues);
    }

    /**
     * This method Runs an insert query (duh)
     *
     * @param table The table where you want to insert the data
     * @param columns   An array of the columns to be inserted
     * @param values    An array of the column values. Should correspond to the array of column names
     * @param uniqueColumnIndex Index of the unique key (primary key). Set this to -1 if none
     * @param db    The writable database
     */
    public void runInsertQuery(String table,String[] columns,String[] values, int uniqueColumnIndex,SQLiteDatabase db) {
        Log.d(TAG, "About to run insert query on "+table+" table");
        if(columns.length==values.length) {
            ContentValues cv=new ContentValues();
            int count=0;
            while(count<columns.length) {
                cv.put(columns[count], values[count]);
                count++;
            }

            //delete row with same unique key
            if(uniqueColumnIndex != -1){
                Log.w(TAG, "About to delete any row with "+columns[uniqueColumnIndex]+" = "+values[uniqueColumnIndex]);
                runDeleteQuery(db, table, columns[uniqueColumnIndex], new String[]{values[uniqueColumnIndex]});
            }

            db.insert(table, null, cv);

            cv.clear();
        }
    }

    /**
     * This method deletes all data in a table. Please be careful, this method will delete all the data in that table
     *
     * @param db    The writable database
     * @param table The table to truncate
     */
    public void runTruncateQuery(SQLiteDatabase db, String table){
        Log.w(TAG, "About to truncate table "+table);
        String query = "DELETE FROM "+table;
        runQuery(db, query);
    }

    /**
     * This method runs a generic query in the database.
     * If you want to run:
     *      select queries, please use runSelectQuery()
     *      insert queries, please use runInsertQuery()
     *      delete queries, please use runDeleteQuery()
     *
     * @param db    The readable/writable database to use depending on whether you need to write into the database
     * @param query The query that you want to run. Please use SQLite friendly queries
     */
    public void runQuery(SQLiteDatabase db, String query) {//non return queries
        Log.d(TAG, "about to run generic query on the database");
        db.execSQL(query);
    }
}
