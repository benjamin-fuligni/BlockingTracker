package io.github.benjamin_fuligni.blockingtracker;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.PointF;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Selena on 11/2/2017.
 */

public class FeedReaderDbHelper extends SQLiteOpenHelper {

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + FeedReaderContract.FeedEntry.TABLE_NAME + " (" +
                    FeedReaderContract.FeedEntry._ID + " INTEGER PRIMARY KEY," +
                    FeedReaderContract.FeedEntry.COLUMN_NAME_TITLE + " TEXT," +
                    FeedReaderContract.FeedEntry.COLUMN_NAME_SUBTITLE + " TEXT)";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + FeedReaderContract.FeedEntry.TABLE_NAME;

    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "FeedReader.db";

    public FeedReaderDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public void addPoint(PointF p) {
        SQLiteDatabase DB = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("X", p.x);
        values.put("Y", p.y);
        DB.insert(DATABASE_NAME, null, values);
        DB.close();
    }

    public List<PointF> getPoints() {
        SQLiteDatabase DB = this.getReadableDatabase();
        List<PointF> points = new ArrayList<PointF>();
        String query = "SELECT * FROM " + DATABASE_NAME;
        Cursor cursor = DB.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                PointF p = new PointF();
                p.set(cursor.getFloat(0),cursor.getFloat(1));
                points.add(p);
            } while (cursor.moveToNext());
        }
        DB.close();
        cursor.close();
        return points;
    }
}
