package com.ptithcm.lottemart;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

public class CrashReportingApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        // IP Discovery moved to MainActivity to prevent race conditions with UI loading
        final Thread.UncaughtExceptionHandler defaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread thread, Throwable throwable) {
                // Save crash log to SharedPreferences
                SharedPreferences prefs = getSharedPreferences("CrashLogs", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("last_crash", android.util.Log.getStackTraceString(throwable));
                editor.commit(); // Use commit to ensure it's saved synchronously before the process dies

                // Call the default handler to actually crash the app
                if (defaultHandler != null) {
                    defaultHandler.uncaughtException(thread, throwable);
                } else {
                    System.exit(2);
                }
            }
        });
    }
}
