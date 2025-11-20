package com.example.bunkmate;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {

    private static final String PREF_NAME = "bunkmate_session";
    private static final String KEY_LOGGED_IN = "logged_in";
    private static final String KEY_USER_ID = "user_id";

    private final SharedPreferences prefs;

    public SessionManager(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public void saveLogin(long userId) {
        prefs.edit()
                .putBoolean(KEY_LOGGED_IN, true)
                .putLong(KEY_USER_ID, userId)
                .apply();
    }

    public boolean isLoggedIn() {
        return prefs.getBoolean(KEY_LOGGED_IN, false);
    }

    public long getUserId() {
        return prefs.getLong(KEY_USER_ID, -1);
    }

    public void logout() {
        prefs.edit().clear().apply();
    }
}
