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

public class DBManager {

    private FeedReaderDbHelper dbHelper;
    private Context context;
    private SQLiteDatabase database;

    public DBManager(Context c) { context = c; }

    public DBManager open() throws SQLException {
        dbHelper = new FeedReaderDbHelper(context);
        database = dbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        dbHelper.close();
    }

    public void insert(String title, String subtitle) {
        ContentValues contentValue = new ContentValues();
        contentValue.put(FeedReaderContract.FeedEntry.COLUMN_NAME_TITLE, title);
        contentValue.put(FeedReaderContract.FeedEntry.COLUMN_NAME_SUBTITLE, subtitle);
        database.insert(FeedReaderContract.FeedEntry.TABLE_NAME, null, contentValue);
    }

    public Cursor fetch() {
        String[] columns = new String[] { FeedReaderContract.FeedEntry._ID, FeedReaderContract.FeedEntry.COLUMN_NAME_TITLE, FeedReaderContract.FeedEntry.COLUMN_NAME_SUBTITLE};
        Cursor cursor = database.query(FeedReaderContract.FeedEntry.TABLE_NAME, columns, null, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;
    }

    public int update(long _id, String name, String desc) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(FeedReaderContract.FeedEntry.COLUMN_NAME_TITLE, name);
        contentValues.put(FeedReaderContract.FeedEntry.COLUMN_NAME_SUBTITLE, desc);
        int i = database.update(FeedReaderContract.FeedEntry.TABLE_NAME, contentValues, FeedReaderContract.FeedEntry._ID + " = " + _id, null);
        return i;
    }

    public void delete(long _id) {
        database.delete(FeedReaderContract.FeedEntry.TABLE_NAME, FeedReaderContract.FeedEntry._ID + "=" + _id, null);
    }

    public void deleteAll() {
        database.delete(FeedReaderContract.FeedEntry.TABLE_NAME, null, null);
    }
}