package com.misedeg;

import android.app.Application;

import com.onesignal.Continue;
import com.onesignal.OneSignal;
import com.onesignal.debug.LogLevel;

public class OneSignalClass extends Application {

    private static final String ONESIGNAL_APP_ID = "91b3902e-2e62-4fd4-a5d1-f1c5eb1c7a15";

    @Override
    public void onCreate() {
        super.onCreate();
        OneSignal.getDebug().setLogLevel(LogLevel.VERBOSE);
        OneSignal.initWithContext(this, ONESIGNAL_APP_ID);
        OneSignal.getNotifications().requestPermission(false, Continue.none());
    }

}
