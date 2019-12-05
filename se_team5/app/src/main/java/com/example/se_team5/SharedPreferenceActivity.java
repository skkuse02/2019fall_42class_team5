package com.example.se_team5;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;


public class SharedPreferenceActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    public String getPreferences(String file, String key){
        SharedPreferences pref = getSharedPreferences(file, MODE_PRIVATE);
        return pref.getString(key, "");
    }

    public void savePreferences(String file, String key, String value){
        SharedPreferences pref = getSharedPreferences(file, MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public void savePreferences(String file, String key, int value){
        SharedPreferences pref = getSharedPreferences(file, MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt(key, value);
        editor.commit();
    }

    public void removePreferences(String file, String key){
        SharedPreferences pref = getSharedPreferences(file, MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.remove(key);
        editor.commit();
    }

}