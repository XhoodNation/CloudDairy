package com.afrikcode.alccodechallenge;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;

public class ALCCodeChallenge extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}
