package com.soikonomakis.rxfirebase;

import android.app.Application;

public class ApplicationStub extends Application {

  @Override public void onCreate() {
    super.onCreate();
    //Firebase.setAndroidContext(this);
  }
}
