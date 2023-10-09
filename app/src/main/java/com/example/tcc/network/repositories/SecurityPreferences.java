package com.example.tcc.network.repositories;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.tcc.ui.constants.TaskConstants;

public class SecurityPreferences {
    private static final String PREF_NAME = "SecurityPreferences";
    private SharedPreferences.Editor editor;
    private final SharedPreferences preferences;

    public SecurityPreferences(Context context) {
        preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public void store(String authToken) {
        editor = preferences.edit();
        editor.putString(TaskConstants.SHARED.TOKEN_KEY, authToken);
        editor.apply();
    }

    public String getAuthToken() {
        return preferences.getString(TaskConstants.SHARED.TOKEN_KEY, null);
    }

    public void clearAuthToken() {
        editor = preferences.edit();
        editor.remove(TaskConstants.SHARED.TOKEN_KEY);
        editor.apply();
    }
}
