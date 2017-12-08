package io.github.benjamin_fuligni.blockingtracker;

/**
 * Created by Selena on 11/3/2017.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

// A Wrapper class to make database management in the app far easier
public class DBManager {

    private FeedReaderDbHelper dbHelper;
    private Context context;
    private SQLiteDatabase database;

    //No default constructor
    public DBManager(Context c) { context = c; }

    //opens a writable database so user doesn't need to keep track of readable/writable
    public DBManager open() throws SQLException {
        dbHelper = new FeedReaderDbHelper(context);
        database = dbHelper.getWritableDatabase();
        return this;
    }

    //pretty self explanatory
    public void close() {
        dbHelper.close();
    }

    //returns the index of insertion if successful; -1 if not
    //if an entry with the same title exists, changes data for that entry rather than creating a new one
    public int insert(String title, String subtitle) {
        Cursor cursor = this.fetch();
        int index = -1;
        cursor.moveToFirst();
        while (!cursor.isAfterLast()){
            if (title.equals(((Integer)cursor.getInt(1)).toString())) {
                index = 1;
                break;
            }
            cursor.move(1);
        }
        cursor.close();
        if (index == -1) {
            ContentValues contentValue = new ContentValues();
            contentValue.put(FeedReaderContract.FeedEntry.COLUMN_NAME_TITLE, title);
            contentValue.put(FeedReaderContract.FeedEntry.COLUMN_NAME_SUBTITLE, subtitle);
            database.insert(FeedReaderContract.FeedEntry.TABLE_NAME, null, contentValue);
        } else {
            this.update(title, subtitle);
        }
        return index;
    }

    //again, abstracts away readble/writable databases for user
    public Cursor fetch() {
        String[] columns = new String[] { FeedReaderContract.FeedEntry._ID, FeedReaderContract.FeedEntry.COLUMN_NAME_TITLE, FeedReaderContract.FeedEntry.COLUMN_NAME_SUBTITLE};
        Cursor cursor = database.query(FeedReaderContract.FeedEntry.TABLE_NAME, columns, null, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;
    }


    //returns the data associated with a specific title, to avoid fenagling with indices
    public String get(String title) {
        Cursor cursor = this.fetch();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()){
            if (title.equals(cursor.getString(cursor.getColumnIndex(FeedReaderContract.FeedEntry.COLUMN_NAME_TITLE)))) {
                String text = cursor.getString(cursor.getColumnIndex(FeedReaderContract.FeedEntry.COLUMN_NAME_SUBTITLE));
                cursor.close();
                return text;
            }
            cursor.move(1);
        }
        cursor.close();
        return null;
    }

    //updates the db entry with "name" title
    public int update(String name, String desc) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(FeedReaderContract.FeedEntry.COLUMN_NAME_TITLE, name);
        contentValues.put(FeedReaderContract.FeedEntry.COLUMN_NAME_SUBTITLE, desc);
        int i = database.update(FeedReaderContract.FeedEntry.TABLE_NAME, contentValues, FeedReaderContract.FeedEntry.COLUMN_NAME_TITLE + " = '" + name + "'", null);
        return i;
    }


    //delete from a specific index in the database
    public void delete(long _id) {
        database.delete(FeedReaderContract.FeedEntry.TABLE_NAME, FeedReaderContract.FeedEntry._ID + " = " + _id, null);
    }

    //empties database without deleting the address
    public void deleteAll() {
        database.delete(FeedReaderContract.FeedEntry.TABLE_NAME, null, null);
    }
}