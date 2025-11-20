package com.example.bunkmate.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.security.MessageDigest;

public class UserDAO {

    private final DBHelper dbHelper;

    public UserDAO(Context context) {
        dbHelper = new DBHelper(context);
    }

    // Register user (returns new userId or -1 if fail)
    public long registerUser(String name, String email, String plainPassword) {
        if (isEmailExists(email)) return -1;

        String hash = sha256Hex(plainPassword);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(DBHelper.COL_USER_NAME, name);
        cv.put(DBHelper.COL_USER_EMAIL, email);
        cv.put(DBHelper.COL_USER_PASSWORD, hash);

        long id = db.insert(DBHelper.TABLE_USERS, null, cv);
        db.close();
        return id;
    }

    // Login: return userId if ok, else -1
    public long login(String email, String plainPassword) {
        String hash = sha256Hex(plainPassword);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor c = db.query(
                DBHelper.TABLE_USERS,
                new String[]{DBHelper.COL_USER_ID},
                DBHelper.COL_USER_EMAIL + "=? AND " + DBHelper.COL_USER_PASSWORD + "=?",
                new String[]{email, hash},
                null, null, null
        );

        long userId = -1;
        if (c != null) {
            if (c.moveToFirst()) {
                int idx = c.getColumnIndex(DBHelper.COL_USER_ID);
                if (idx >= 0) userId = c.getLong(idx);
            }
            c.close();
        }
        db.close();
        return userId;
    }

    public boolean isEmailExists(String email) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db.query(
                DBHelper.TABLE_USERS,
                new String[]{DBHelper.COL_USER_ID},
                DBHelper.COL_USER_EMAIL + "=?",
                new String[]{email},
                null, null, null
        );
        boolean exists = (c != null && c.moveToFirst());
        if (c != null) c.close();
        db.close();
        return exists;
    }

    // SHA-256 → HEX
    private String sha256Hex(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] bytes = md.digest(input.getBytes("UTF-8"));
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) {
                sb.append(String.format("%02x", b)); // HEX
            }
            return sb.toString();
        } catch (Exception e) {
            return ""; // fallback
        }
    }
}
