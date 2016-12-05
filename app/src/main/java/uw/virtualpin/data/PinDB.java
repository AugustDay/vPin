package uw.virtualpin.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import uw.virtualpin.R;
import uw.virtualpin.pin.Pin;

/**
 * Created by opeoluwabada on 11/27/16.
 */

public class PinDB {
    public static final int DB_VERSION = 1;
    public static final String DB_NAME = "Course.db";

    private PinDBHelper mCourseDBHelper;
    private SQLiteDatabase mSQLiteDatabase;

    private PinDB mPinDB;
    private List<Pin> mCourseList;

    private static final String PIN_TABLE = "Pin";

    public PinDB(Context context) {
        mCourseDBHelper = new PinDBHelper(context, DB_NAME, null, DB_VERSION);
        mSQLiteDatabase = mCourseDBHelper.getWritableDatabase();
    }

    /**
     * Returns the list of courses from the local Course table.
     * @return list
     */
    public List<Pin> getPins() {

        String[] columns = {
                "id", "creator", "latitude", "longitude", "message"
        };

        Cursor c = mSQLiteDatabase.query(
                PIN_TABLE,  // The table to query
                columns,                               // The columns to return
                null,                                // The columns for the WHERE clause
                null,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null                                 // The sort order
        );
        c.moveToFirst();
        List<Pin> list = new ArrayList<Pin>();
        for (int i=0; i<c.getCount(); i++) {
            int id = c.getInt(0);
            String creator = c.getString(1);
            double latitude = c.getDouble(2);
            double longitude = c.getDouble(3);
            String message = c.getString(4);

            Pin course = new Pin(id, creator, latitude, longitude, message);
            list.add(course);
            c.moveToNext();
        }

        return list;
    }


    /**
     * Inserts the course into the local sqlite table. Returns true if successful, false otherwise.
     * @param id
     * @param creator
     * @param latitude
     * @param longitude
     * @param message
     * @return true or false
     */
    public boolean insertCourse(int id, String creator, double latitude, double longitude, String message) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("id", id);
        contentValues.put("creator", creator);
        contentValues.put("latitude", latitude);
        contentValues.put("longitude", longitude);
        contentValues.put("message", message);

        long rowId = mSQLiteDatabase.insert("Pin", null, contentValues);
        return rowId != -1;
    }

    /**
     * Delete all the data from the COURSE_TABLE
     */
    public void deletePins() {
        mSQLiteDatabase.delete(PIN_TABLE, null, null);
    }

    public void closeDB() {
        mSQLiteDatabase.close();
    }


    class PinDBHelper extends SQLiteOpenHelper
    {
        private final String CREATE_PIN_SQL;
        private final String DROP_PIN_SQL;


        public PinDBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
            CREATE_PIN_SQL = context.getString(R.string.CREATE_PIN_SQL);
            DROP_PIN_SQL = context.getString(R.string.DROP_PIN_SQL);

        }
        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(CREATE_PIN_SQL);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL(DROP_PIN_SQL);
            onCreate(db);
        }
    }
}
