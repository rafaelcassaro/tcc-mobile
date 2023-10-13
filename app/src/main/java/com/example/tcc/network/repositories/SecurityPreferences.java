package com.example.tcc.network.repositories;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.tcc.ui.constants.TaskConstants;

public class SecurityPreferences {

    private SharedPreferences.Editor editor;
    private final SharedPreferences preferences;

    public SecurityPreferences(Context context) {
        preferences = context.getSharedPreferences(TaskConstants.SECURITY.PREF_NAME  , Context.MODE_PRIVATE);
    }

    public void store(String key, String authToken) {
        editor = preferences.edit();
        editor.putString(key, authToken);
        editor.apply();
    }



    public String getAuthToken(String data) {
        return preferences.getString(data, null);
    }

    public void clearAuthToken() {
        editor = preferences.edit();
        editor.remove(TaskConstants.SHARED.TOKEN_KEY);
        editor.apply();
    }
}
