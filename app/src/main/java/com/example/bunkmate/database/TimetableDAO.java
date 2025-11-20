package com.example.bunkmate.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class TimetableDAO {

    private final DBHelper dbHelper;

    public static class PeriodRow {
        public long id;
        public String dayName;
        public int periodNumber;
        public long subjectId;
        public String time;
    }

    public TimetableDAO(Context ctx) { dbHelper = new DBHelper(ctx); }

    public long addPeriod(String dayName, int periodNumber, long subjectId, String time) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(DBHelper.COL_TT_DAY, dayName);
        cv.put(DBHelper.COL_TT_PERIOD, periodNumber);
        cv.put(DBHelper.COL_TT_SUBJECT_ID, subjectId);
        cv.put(DBHelper.COL_TT_TIME, time);
        long id = db.insert(DBHelper.TABLE_TIMETABLE, null, cv);
        db.close();
        return id;
    }

    public List<PeriodRow> getByDay(String dayName) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db.query(DBHelper.TABLE_TIMETABLE, null,
                DBHelper.COL_TT_DAY + "=?", new String[]{dayName},
                null, null, DBHelper.COL_TT_PERIOD + " ASC");
        List<PeriodRow> list = new ArrayList<>();
        if (c != null) {
            while (c.moveToNext()) {
                PeriodRow r = new PeriodRow();
                r.id = c.getLong(c.getColumnIndexOrThrow(DBHelper.COL_TT_ID));
                r.dayName = c.getString(c.getColumnIndexOrThrow(DBHelper.COL_TT_DAY));
                r.periodNumber = c.getInt(c.getColumnIndexOrThrow(DBHelper.COL_TT_PERIOD));
                r.subjectId = c.getLong(c.getColumnIndexOrThrow(DBHelper.COL_TT_SUBJECT_ID));
                r.time = c.getString(c.getColumnIndexOrThrow(DBHelper.COL_TT_TIME));
                list.add(r);
            }
            c.close();
        }
        db.close();
        return list;
    }
}
