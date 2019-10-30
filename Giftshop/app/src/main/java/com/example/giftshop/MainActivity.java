package com.example.giftshop;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.rbddevs.splashy.Splashy;

import java.util.Locale;


public class MainActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private static final String TAG = "MainActivity";

    public MainActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.e(TAG, "onCreate: " + BuildConfig.VERSION_NAME);

        new Splashy(this)
                .setTitle(getString(R.string.app_name) + " v " + BuildConfig.VERSION_NAME)
                .setLogo(R.drawable.ic_logo)
                .setTitleColor(R.color.primaryLightColor)
                .showProgress(true)
                .setAnimation(Splashy.Animation.GLOW_LOGO, 1000)
                .setSubTitle(getString(R.string.coppyright))
                .setFullScreen(true)
                .setProgressColor(R.color.primaryDarkColor)
                .setTitleSize(22)
                .show();
        Splashy.Companion.onComplete(new Splashy.OnComplete() {
            @Override
            public void onComplete() {
                Log.e(TAG, "onComplete: ");
                checkUser_noloading();
            }
        });

        SharedPreferences pref = getApplicationContext().getSharedPreferences("LANGUAGE", 0);
        final SharedPreferences.Editor editor = pref.edit();

        String lang = pref.getString("lang", null);
        if (lang == null) {
            editor.putString("lang", "en");
            editor.apply();
        } else {
            Locale locale = new Locale(lang);
            Locale.setDefault(locale);
            Configuration configuration = new Configuration();
            configuration.locale = locale;
            getBaseContext().getResources().updateConfiguration(configuration
                    , getBaseContext().getResources().getDisplayMetrics());
        }


        firebaseAuth = FirebaseAuth.getInstance();


    }


    private void checkUser_noloading() {
        firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser != null) {
            final Intent home_intent = new Intent(MainActivity.this, HomeActivity.class);
            home_intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            home_intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(home_intent);
            finish();

        } else {
            Log.e(TAG, "checkUser: NoUser");
            final Intent login_intent = new Intent(MainActivity.this, LoginActivity.class);
            login_intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            login_intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(login_intent);
            finish();
        }
    }


}
