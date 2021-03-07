package com.example.comfortchat.InternalStorage;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferenceManager {

    private SharedPreferences sharedPreferences;

    public PreferenceManager(Context context){

        sharedPreferences = context.getSharedPreferences("name", Context.MODE_PRIVATE);

    }

    public void putString(String key, String value){

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();

    }

    public String getString(String key){

        return sharedPreferences.getString(key, null);

    }

}
