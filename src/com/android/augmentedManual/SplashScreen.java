package com.android.augmentedManual;

import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.content.Intent;

public class SplashScreen extends Activity {

private static final int SPLASH_DISPLAY_TIME = 3500; /* 3 seconds */

public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_splash_screen);

    new Handler().postDelayed(new Runnable() {

        public void run() {

            Intent mainIntent = new Intent(SplashScreen.this,
                    MainActivity.class);
            SplashScreen.this.startActivity(mainIntent);

            SplashScreen.this.finish();
            overridePendingTransition(R.anim.mainfadein,
                    R.anim.splashfadeout);
        }
    }, SPLASH_DISPLAY_TIME);
}
}