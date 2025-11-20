package com.example.bunkmate.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.bunkmate.model.Subject;

import java.util.ArrayList;
import java.util.List;

public class SubjectDAO {

    private final DBHelper dbHelper;

    public SubjectDAO(Context ctx) {
        dbHelper = new DBHelper(ctx);
    }

    public long add(String name, int minRequired, String professorName) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(DBHelper.COL_SUBJ_NAME, name);
        cv.put(DBHelper.COL_SUBJ_MIN_REQ, minRequired);
        if (professorName != null) cv.put(DBHelper.COL_SUBJ_PROF, professorName);
        long id = db.insert(DBHelper.TABLE_SUBJECTS, null, cv);
        db.close();
        return id;
    }

    public List<Subject> getAll() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db.query(DBHelper.TABLE_SUBJECTS, null, null, null, null, null,
                DBHelper.COL_SUBJ_NAME + " ASC");
        List<Subject> list = new ArrayList<>();
        if (c != null) {
            while (c.moveToNext()) {
                long id = c.getLong(c.getColumnIndexOrThrow(DBHelper.COL_SUBJ_ID));
                String name = c.getString(c.getColumnIndexOrThrow(DBHelper.COL_SUBJ_NAME));
                int attended = c.getInt(c.getColumnIndexOrThrow(DBHelper.COL_SUBJ_ATTENDED));
                int total = c.getInt(c.getColumnIndexOrThrow(DBHelper.COL_SUBJ_TOTAL));
                int minReq = c.getInt(c.getColumnIndexOrThrow(DBHelper.COL_SUBJ_MIN_REQ));
                String prof = c.getString(c.getColumnIndexOrThrow(DBHelper.COL_SUBJ_PROF));
                list.add(new Subject(id, name, attended, total, minReq, prof));
            }
            c.close();
        }
        db.close();
        return list;
    }

    public int updateCounts(long subjectId, int attended, int total) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(DBHelper.COL_SUBJ_ATTENDED, attended);
        cv.put(DBHelper.COL_SUBJ_TOTAL, total);
        int rows = db.update(DBHelper.TABLE_SUBJECTS, cv,
                DBHelper.COL_SUBJ_ID + "=?", new String[]{String.valueOf(subjectId)});
        db.close();
        return rows;
    }

    public Subject getById(long id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db.query(DBHelper.TABLE_SUBJECTS, null,
                DBHelper.COL_SUBJ_ID + "=?", new String[]{String.valueOf(id)},
                null, null, null);
        Subject s = null;
        if (c != null && c.moveToFirst()) {
            s = new Subject(
                    c.getLong(c.getColumnIndexOrThrow(DBHelper.COL_SUBJ_ID)),
                    c.getString(c.getColumnIndexOrThrow(DBHelper.COL_SUBJ_NAME)),
                    c.getInt(c.getColumnIndexOrThrow(DBHelper.COL_SUBJ_ATTENDED)),
                    c.getInt(c.getColumnIndexOrThrow(DBHelper.COL_SUBJ_TOTAL)),
                    c.getInt(c.getColumnIndexOrThrow(DBHelper.COL_SUBJ_MIN_REQ)),
                    c.getString(c.getColumnIndexOrThrow(DBHelper.COL_SUBJ_PROF))
            );
            c.close();
        }
        db.close();
        return s;
    }

    // SubjectDAO.java
    public int getWeeklyClassCount(long subjectId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db.rawQuery(
                "SELECT COUNT(*) FROM " + DBHelper.TABLE_TIMETABLE +
                        " WHERE " + DBHelper.COL_TT_SUBJECT_ID + " = ?",
                new String[]{String.valueOf(subjectId)}
        );
        int count = 0;
        if (c.moveToFirst()) count = c.getInt(0);
        c.close();
        db.close();
        return count;
    }

}
