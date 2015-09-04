package com.lena.splashscreenapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.util.Timer;
import java.util.TimerTask;


public class SplashScreen extends AppCompatActivity {

    public static final String APP_PREFERENCES = "mysettings";//файл настроек
    public static final String TIME_OF_PERIOD = "time_of_period";//то, что храним в нем - для опознания

    long timeStart;
    Timer timer;
    SharedPreferences sPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        timer = new Timer();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_splash_screen, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onPause(){
        super.onPause();

        timer.cancel();
        long timeEnd = System.currentTimeMillis();
        long timeOfPeriod = timeEnd - timeStart;

        sPref = getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE);

        timeOfPeriod += sPref.getLong(TIME_OF_PERIOD, 0);
        Log.d("HHHHH", "---onPause:    (before in sPref)SPREF = " + String.valueOf(sPref.getLong(TIME_OF_PERIOD, 0)));
        Log.d("HHHHH", "---onPause:   (write to sPref)timeOfp = " + String.valueOf(timeOfPeriod));
        sPref = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);//имя файла настроек, режим
        Editor editor = sPref.edit();
        editor.putLong(TIME_OF_PERIOD, timeOfPeriod);
        editor.apply();//editor.commit();

        //выйти из активити, финиш
        //super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        sPref = getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE);

        long timeRemaining = 2000 - sPref.getLong(TIME_OF_PERIOD, 0);
        Log.d("HHHHH", "onResume:   sPref = " + sPref.getLong(TIME_OF_PERIOD, 0));

        if(timeRemaining < 0) {
            Editor ed = sPref.edit().clear();
            ed.commit();
            Log.d("HHHHH", "onResume:  (clear)sPref = " + sPref.getLong(TIME_OF_PERIOD, 0));
            timeRemaining = 2000;
            Log.d("HHHHH", "onResume:   timeRemaining = " + timeRemaining);
        }
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashScreen.this, HomeScreen.class);
                timer.cancel();
                startActivity(intent);
            }
        }, timeRemaining);

        timeStart = System.currentTimeMillis();
    }
}
