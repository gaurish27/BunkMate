package com.example.bunkmate.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import androidx.annotation.Nullable;

public class DBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "bunkmate.db";
    // Bump when schema changes
    public static final int DATABASE_VERSION = 3;

    // ===== Users =====
    public static final String TABLE_USERS = "users";
    public static final String COL_USER_ID = "id";
    public static final String COL_USER_NAME = "name";
    public static final String COL_USER_EMAIL = "email";
    public static final String COL_USER_PASSWORD = "password";
    public static final String COL_USER_CREATED_AT = "created_at";

    // ===== Subjects =====
    public static final String TABLE_SUBJECTS = "subjects";
    public static final String COL_SUBJ_ID = "id";
    public static final String COL_SUBJ_NAME = "name";
    public static final String COL_SUBJ_TOTAL = "total_classes";
    public static final String COL_SUBJ_ATTENDED = "attended_classes";
    public static final String COL_SUBJ_MIN_REQ = "minimum_required_percentage";
    public static final String COL_SUBJ_PROF = "professor_name";

    // ===== Timetable =====
    public static final String TABLE_TIMETABLE = "timetable";
    public static final String COL_TT_ID = "id";
    public static final String COL_TT_DAY = "day_name";            // Mon, Tue, Wed, Thu, Fri, Sat
    public static final String COL_TT_PERIOD = "period_number";     // 1,2,3...
    public static final String COL_TT_SUBJECT_ID = "subject_id";    // FK -> subjects.id
    public static final String COL_TT_TIME = "time";                // optional e.g. "9:00 AM"

    // ===== Attendance (per period, per day) =====
    public static final String TABLE_ATTENDANCE = "attendance";
    public static final String COL_ATT_ID = "id";
    public static final String COL_ATT_DATE = "date";               // YYYY-MM-DD
    public static final String COL_ATT_SUBJECT_ID = "subject_id";   // FK -> subjects.id
    public static final String COL_ATT_PERIOD = "period_number";    // period in timetable (nullable if ad-hoc)
    public static final String COL_ATT_STATUS = "status";           // Present / Absent / Cancelled
    public static final String COL_ATT_CREATED_AT = "created_at";

    public DBHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Enable foreign keys
    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        // USERS
        db.execSQL("CREATE TABLE " + TABLE_USERS + " ("
                + COL_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_USER_NAME + " TEXT NOT NULL, "
                + COL_USER_EMAIL + " TEXT NOT NULL UNIQUE, "
                + COL_USER_PASSWORD + " TEXT NOT NULL, "
                + COL_USER_CREATED_AT + " TEXT DEFAULT (datetime('now'))"
                + ")");

        // SUBJECTS
        db.execSQL("CREATE TABLE " + TABLE_SUBJECTS + " ("
                + COL_SUBJ_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_SUBJ_NAME + " TEXT NOT NULL UNIQUE, "
                + COL_SUBJ_TOTAL + " INTEGER NOT NULL DEFAULT 0, "
                + COL_SUBJ_ATTENDED + " INTEGER NOT NULL DEFAULT 0, "
                + COL_SUBJ_MIN_REQ + " INTEGER NOT NULL DEFAULT 75, "
                + COL_SUBJ_PROF + " TEXT"
                + ")");

        // TIMETABLE
        db.execSQL("CREATE TABLE " + TABLE_TIMETABLE + " ("
                + COL_TT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_TT_DAY + " TEXT NOT NULL, "
                + COL_TT_PERIOD + " INTEGER NOT NULL, "
                + COL_TT_SUBJECT_ID + " INTEGER NOT NULL, "
                + COL_TT_TIME + " TEXT, "
                + "FOREIGN KEY(" + COL_TT_SUBJECT_ID + ") REFERENCES " + TABLE_SUBJECTS + "(" + COL_SUBJ_ID + ") ON DELETE CASCADE"
                + ")");
        db.execSQL("CREATE INDEX idx_timetable_day ON " + TABLE_TIMETABLE + "(" + COL_TT_DAY + ")");

        // ATTENDANCE
        db.execSQL("CREATE TABLE " + TABLE_ATTENDANCE + " ("
                + COL_ATT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_ATT_DATE + " TEXT NOT NULL, "
                + COL_ATT_SUBJECT_ID + " INTEGER NOT NULL, "
                + COL_ATT_PERIOD + " INTEGER, "
                + COL_ATT_STATUS + " TEXT NOT NULL CHECK(" + COL_ATT_STATUS + " IN ('Present','Absent','Cancelled')), "
                + COL_ATT_CREATED_AT + " TEXT DEFAULT (datetime('now')), "
                + "FOREIGN KEY(" + COL_ATT_SUBJECT_ID + ") REFERENCES " + TABLE_SUBJECTS + "(" + COL_SUBJ_ID + ") ON DELETE CASCADE"
                + ")");
        db.execSQL("CREATE INDEX idx_attendance_date_subject ON " + TABLE_ATTENDANCE + "(" + COL_ATT_DATE + "," + COL_ATT_SUBJECT_ID + ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Early-stage dev: simple reset. (For production, write ALTER TABLE migrations.)
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ATTENDANCE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TIMETABLE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SUBJECTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(db);
    }
}
