package group0.eduworld;

import android.app.Application;
import android.content.Intent;
import android.content.res.Configuration;
import com.google.firebase.FirebaseApp;
import group0.eduworld.service.EWFirebaseMessagingService;

public class EWApplication extends Application {
    public static boolean teacher = false;
    // Called when the application is starting, before any other application objects have been created.
    // Overriding this method is totally optional!
    @Override
    public void onCreate() {
        FirebaseApp.initializeApp(this);
        super.onCreate();
        // Required initialization logic here!
        Intent fbmsIntent = new Intent(this, EWFirebaseMessagingService.class);
        startService(fbmsIntent);
    }

    // Called by the system when the device configuration changes while your component is running.
    // Overriding this method is totally optional!
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    // This is called when the overall system is running low on memory,
    // and would like actively running processes to tighten their belts.
    // Overriding this method is totally optional!
    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }
}