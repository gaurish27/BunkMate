package com.example.bunkmate.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AttendanceDAO {

    private final DBHelper dbHelper;

    public AttendanceDAO(Context ctx) {
        dbHelper = new DBHelper(ctx);
    }

    // --- Helpers ---
    private String keyWhere() {
        // Find a row uniquely by (date, subject, period)
        // Note: period can be NULL; we handle with IS NULL logic
        return DBHelper.COL_ATT_DATE + "=? AND " + DBHelper.COL_ATT_SUBJECT_ID + "=? AND (" +
                DBHelper.COL_ATT_PERIOD + "=? OR (" + DBHelper.COL_ATT_PERIOD + " IS NULL AND ? IS NULL))";
    }

    private String[] keyArgs(String date, long subjectId, Integer periodNumber) {
        String p = (periodNumber == null) ? null : String.valueOf(periodNumber);
        return new String[]{date, String.valueOf(subjectId), p, p};
    }

    private String getExistingStatus(SQLiteDatabase db, String date, long subjectId, Integer periodNumber) {
        String status = null;
        try (Cursor c = db.query(DBHelper.TABLE_ATTENDANCE, new String[]{DBHelper.COL_ATT_STATUS},
                keyWhere(), keyArgs(date, subjectId, periodNumber),
                null, null, null)) {
            if (c != null && c.moveToFirst()) {
                status = c.getString(0);
            }
        }
        return status;
    }

    // Add near other methods
    public String getExistingStatusForUI(String date, long subjectId, Integer periodNumber) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        try (android.database.Cursor c = db.query(DBHelper.TABLE_ATTENDANCE,
                new String[]{DBHelper.COL_ATT_STATUS},
                DBHelper.COL_ATT_DATE + "=? AND " + DBHelper.COL_ATT_SUBJECT_ID + "=? AND " +
                        "(" + DBHelper.COL_ATT_PERIOD + "=? OR (" + DBHelper.COL_ATT_PERIOD + " IS NULL AND ? IS NULL))",
                new String[]{date, String.valueOf(subjectId),
                        periodNumber == null ? null : String.valueOf(periodNumber),
                        periodNumber == null ? null : String.valueOf(periodNumber)},
                null,null,null)) {
            if (c != null && c.moveToFirst()) {
                return c.getString(0);
            }
            return null;
        } finally {
            db.close();
        }
    }


    private void applyDeltaForStatus(SQLiteDatabase db, long subjectId, String oldStatus, String newStatus) {
        // Effects on SUBJECTS counters:
        // Present   -> attended+1, total+1
        // Absent    -> total+1
        // Cancelled -> no effect
        // When changing status, we revert old effect then apply new effect.

        // Revert old
        if ("Present".equals(oldStatus)) {
            db.execSQL("UPDATE " + DBHelper.TABLE_SUBJECTS +
                    " SET " + DBHelper.COL_SUBJ_ATTENDED + " = " + DBHelper.COL_SUBJ_ATTENDED + " - 1, " +
                    DBHelper.COL_SUBJ_TOTAL + " = " + DBHelper.COL_SUBJ_TOTAL + " - 1 " +
                    " WHERE " + DBHelper.COL_SUBJ_ID + " = ?", new Object[]{subjectId});
        } else if ("Absent".equals(oldStatus)) {
            db.execSQL("UPDATE " + DBHelper.TABLE_SUBJECTS +
                    " SET " + DBHelper.COL_SUBJ_TOTAL + " = " + DBHelper.COL_SUBJ_TOTAL + " - 1 " +
                    " WHERE " + DBHelper.COL_SUBJ_ID + " = ?", new Object[]{subjectId});
        }

        // Apply new
        if ("Present".equals(newStatus)) {
            db.execSQL("UPDATE " + DBHelper.TABLE_SUBJECTS +
                    " SET " + DBHelper.COL_SUBJ_ATTENDED + " = " + DBHelper.COL_SUBJ_ATTENDED + " + 1, " +
                    DBHelper.COL_SUBJ_TOTAL + " = " + DBHelper.COL_SUBJ_TOTAL + " + 1 " +
                    " WHERE " + DBHelper.COL_SUBJ_ID + " = ?", new Object[]{subjectId});
        } else if ("Absent".equals(newStatus)) {
            db.execSQL("UPDATE " + DBHelper.TABLE_SUBJECTS +
                    " SET " + DBHelper.COL_SUBJ_TOTAL + " = " + DBHelper.COL_SUBJ_TOTAL + " + 1 " +
                    " WHERE " + DBHelper.COL_SUBJ_ID + " = ?", new Object[]{subjectId});
        }
    }

    private boolean upsertStatus(String date, long subjectId, Integer periodNumber, String newStatus) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.beginTransaction();
        try {
            String existing = getExistingStatus(db, date, subjectId, periodNumber);

            if (existing == null) {
                // Insert new record
                ContentValues cv = new ContentValues();
                cv.put(DBHelper.COL_ATT_DATE, date);
                cv.put(DBHelper.COL_ATT_SUBJECT_ID, subjectId);
                if (periodNumber != null) cv.put(DBHelper.COL_ATT_PERIOD, periodNumber);
                cv.put(DBHelper.COL_ATT_STATUS, newStatus);

                long row = db.insert(DBHelper.TABLE_ATTENDANCE, null, cv);
                if (row == -1) return false;

                // Apply delta for new status
                applyDeltaForStatus(db, subjectId, null, newStatus);

            } else {
                // Already exists
                if (existing.equals(newStatus)) {
                    // Nothing to update
                    db.setTransactionSuccessful();
                    return true;
                }

                // Update existing record
                ContentValues cv = new ContentValues();
                cv.put(DBHelper.COL_ATT_STATUS, newStatus);

                int updated = db.update(DBHelper.TABLE_ATTENDANCE, cv,
                        keyWhere(), keyArgs(date, subjectId, periodNumber));

                if (updated <= 0) return false;

                // Adjust attendance based on change
                applyDeltaForStatus(db, subjectId, existing, newStatus);
            }

            db.setTransactionSuccessful();
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;

        } finally {
            // Always executed once
            if (db.inTransaction()) {
                db.endTransaction();
            }
            db.close();
        }
    }


    public boolean markPresent(String date, long subjectId, Integer periodNumber) {
        return upsertStatus(date, subjectId, periodNumber, "Present");
    }
    public boolean markAbsent(String date, long subjectId, Integer periodNumber) {
        return upsertStatus(date, subjectId, periodNumber, "Absent");
    }
    public boolean markCancelled(String date, long subjectId, Integer periodNumber) {
        // Cancelled has no counters, but still upsert for editability
        return upsertStatus(date, subjectId, periodNumber, "Cancelled");
    }
    public List<Map<String, String>> getLogsForSubject(long subjectId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<Map<String, String>> logs = new ArrayList<>();

        Cursor c = db.query(DBHelper.TABLE_ATTENDANCE,
                new String[]{DBHelper.COL_ATT_DATE, DBHelper.COL_ATT_PERIOD, DBHelper.COL_ATT_STATUS},
                DBHelper.COL_ATT_SUBJECT_ID + "=?",
                new String[]{String.valueOf(subjectId)},
                null, null, DBHelper.COL_ATT_DATE + " DESC");

        if (c != null) {
            while (c.moveToNext()) {
                Map<String, String> map = new HashMap<>();
                map.put("date", c.getString(c.getColumnIndexOrThrow(DBHelper.COL_ATT_DATE)));
                map.put("period", String.valueOf(c.getInt(c.getColumnIndexOrThrow(DBHelper.COL_ATT_PERIOD))));
                map.put("status", c.getString(c.getColumnIndexOrThrow(DBHelper.COL_ATT_STATUS)));
                logs.add(map);
            }
            c.close();
        }
        db.close();
        return logs;
    }

}
