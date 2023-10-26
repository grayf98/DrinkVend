package com.example.btcontroll;

import android.app.Application;
import android.content.Context;
import com.squareup.sdk.reader.ReaderSdk;

public class Payment extends Application {

    @Override public void onCreate() {
        super.onCreate();
        ReaderSdk.initialize(this);
    }

    @Override protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
    }
}



