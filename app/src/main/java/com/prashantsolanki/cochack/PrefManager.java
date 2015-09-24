package com.prashantsolanki.cochack;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

public class PrefManager {
    // Shared Preferences
    static SharedPreferences pref;
    // Editor for Shared preferences
    static SharedPreferences.Editor editor;
    //Accessible from outside
    public static final String KEY_RESOURCE_COLLECTION = "coc_resource_collection_interval";
    public static final String KEY_SCREEN_TIMEOUT = "coc_screen_time_out";

    // Constructor
    public PrefManager(Context context){
        pref = PreferenceManager.getDefaultSharedPreferences(context);
        editor = pref.edit();
    }

    public static PrefManager init(Context context){
        PrefManager prefManager = new PrefManager(context);
       return prefManager;
    }


    public static long getResoucrceTime(){
        init(AppController.getContext());
        return Long.valueOf(pref.getString(KEY_RESOURCE_COLLECTION, "800000"));
    }
    public static long getScreenTime(){
        init(AppController.getContext());
        return Long.valueOf(pref.getString(KEY_SCREEN_TIMEOUT, "25000"));
    }
}