package com.prashantsolanki.cochack;

import android.app.Application;
import android.content.Context;

public class AppController extends Application {

    private static AppController mInstance;

    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        context = getApplicationContext();

    }

    public static synchronized AppController getInstance(){
        return mInstance;
    }

    public static Context getContext(){
        return context;
    }
}