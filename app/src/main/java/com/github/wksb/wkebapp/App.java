package com.github.wksb.wkebapp;

import android.app.Application;

/**
 * Created by Michael on 04.01.2016.
 */
public class App extends Application {
    private static App SingletonInstance;

    public static App get() {
        return SingletonInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        SingletonInstance = this;
    }
}
