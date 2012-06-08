package net.surguy.winememory;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Implements database CRUD operations for storing and retrieving {@link Bottle bottles}.
 *
 * @author Inigo Surguy
 */
public class DatabaseHandler extends SQLiteOpenHelper  {

    // Using tutorial at http://www.androidhive.info/2011/11/android-sqlite-database-tutorial/

    private static final String TABLE_BOTTLE = "Bottle";

    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_DESCRIPTION = "description";
    private static final String KEY_RATING = "rating";
    private static final String KEY_FILEPATH = "filepath";

    public static final String DB_NAME = "WineMemory";
    public static final int DB_VERSION = 1;

    public DatabaseHandler(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.i("DatabaseHandler", "Creating database");
        String createBottleTable = "CREATE TABLE " + TABLE_BOTTLE + "("
                + KEY_ID + " INTEGER PRIMARY KEY"
                + " , " + KEY_NAME + " TEXT"
                + " , " + KEY_DESCRIPTION + " TEXT"
                + " , " + KEY_RATING + " FLOAT"
                + " , " + KEY_FILEPATH + " TEXT"
                + " )";
        db.execSQL(createBottleTable);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BOTTLE);
        // Create tables again
        onCreate(db);
    }

    public synchronized void addBottle(Bottle bottle) {
        SQLiteDatabase db = this.getWritableDatabase();

        try {
            ContentValues values = new ContentValues();
            values.put(KEY_NAME, bottle.getName());
            values.put(KEY_DESCRIPTION, bottle.getDescription());
            values.put(KEY_RATING, ""+bottle.getRating());
            values.put(KEY_FILEPATH, "" + bottle.getFilePath());

            db.insert(TABLE_BOTTLE, null, values);
        } finally {
            db.close();
        }
    }

    public synchronized int countBottles() {
        SQLiteDatabase db = this.getReadableDatabase();
        try {
            return (int) DatabaseUtils.queryNumEntries(db, TABLE_BOTTLE);
        } finally {
            db.close();
        }
    }

    public synchronized List<Bottle> getAllBottles() {
        List<Bottle> bottleList = new ArrayList<Bottle>();

        SQLiteDatabase db = this.getReadableDatabase();
        try {
            Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_BOTTLE, null);

            if (cursor.moveToFirst()) {
                do {
                    Bottle bottle = new Bottle();
                    // @todo Change this to not depend on column order
                    bottle.setId(cursor.getInt(0));
                    bottle.setName(cursor.getString(1));
                    bottle.setDescription(cursor.getString(2));
                    bottle.setRating(cursor.getFloat(3));
                    bottle.setFilePath(cursor.getString(4));
                    bottleList.add(bottle);
                } while (cursor.moveToNext());
            }
        } finally {
            db.close();
        }
        Log.i("DatabaseHandler", "Getting all bottles - there are " + bottleList.size());
        return bottleList;
    }

    public synchronized int updateBottle(Bottle bottle) {
        SQLiteDatabase db = this.getWritableDatabase();

        try {
            ContentValues values = new ContentValues();
            values.put(KEY_NAME, bottle.getName());
            values.put(KEY_DESCRIPTION, bottle.getDescription());
            values.put(KEY_RATING, ""+bottle.getRating());
            values.put(KEY_FILEPATH, "" + bottle.getFilePath());

            return db.update(TABLE_BOTTLE, values, KEY_ID + " = ?", new String[]{"" + bottle.getId() });
        } finally {
            db.close();
        }
    }

    public synchronized Bottle getBottle(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        try {
            Cursor cursor = db.query(TABLE_BOTTLE, new String[] { KEY_ID, KEY_NAME, KEY_DESCRIPTION, KEY_RATING, KEY_FILEPATH },
                    KEY_ID + "=?", new String[]{ ""+(id+1) }, null, null, null, null);
            if (cursor != null) {
                cursor.moveToFirst();
            } else {
                throw new NullPointerException("Cannot find bottle with id " + id);
            }

            return new Bottle(cursor.getInt(0), cursor.getString(1), cursor.getString(2), cursor.getFloat(3), cursor.getString(4));
        } finally {
            db.close();
        }
    }

    public synchronized void deleteBottle(Bottle bottle) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            db.delete(TABLE_BOTTLE, KEY_ID + " = ?", new String[]{"" + bottle.getId()});
        } finally {
            db.close();
        }
    }

}
