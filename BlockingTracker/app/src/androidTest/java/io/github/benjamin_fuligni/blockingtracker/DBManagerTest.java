package io.github.benjamin_fuligni.blockingtracker;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

/**
 * Created by bibli on 11/10/2017.
 */

@RunWith(AndroidJUnit4.class)
public class DBManagerTest {
    @Test
    public void open() throws Exception {
        Context appContext = InstrumentationRegistry.getTargetContext();
        DBManager db = new DBManager(appContext);
        db = db.open();
        assert (db != null);
    }

    @Test
    public void close() throws Exception {
        Context appContext = InstrumentationRegistry.getTargetContext();
        DBManager db = new DBManager(appContext);
        db = db.open();
        db.close();
        assert (db == null);
    }

    @Test
    public void insert() throws Exception {
        Context appContext = InstrumentationRegistry.getTargetContext();
        DBManager db = new DBManager(appContext);
        db = db.open();
        assert (db.insert("TEST", "test") == -1);
        assert (db.insert("TEST", "test") == 1);
    }

    @Test
    public void fetch() throws Exception {
        Context appContext = InstrumentationRegistry.getTargetContext();
        DBManager db = new DBManager(appContext);
        db = db.open();
        Cursor cursor = db.fetch();
        assert (cursor != null);
    }

    @Test
    public void get() throws Exception {
        Context appContext = InstrumentationRegistry.getTargetContext();
        DBManager db = new DBManager(appContext);
        db = db.open();
        String s = db.get("TEST");
        assert (s.equals("test"));
    }

    @Test
    public void update() throws Exception {
        Context appContext = InstrumentationRegistry.getTargetContext();
        DBManager db = new DBManager(appContext);
        db = db.open();
        db.update("TEST", "test2");
        String s = db.get("TEST");
        assert (s.equals("test2"));
    }

    /*
    @Test
    public void delete() throws Exception {
        Context appContext = InstrumentationRegistry.getTargetContext();
        DBManager db = new DBManager(appContext);
        db = db.open();
    }
    */

    @Test
    public void deleteAll() throws Exception {
        Context appContext = InstrumentationRegistry.getTargetContext();
        DBManager db = new DBManager(appContext);
        db = db.open();
        db.deleteAll();
        String s = db.get("TEST");
        assert (s.equals(null));
    }

}