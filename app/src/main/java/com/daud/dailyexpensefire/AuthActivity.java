package com.daud.dailyexpensefire;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;

public class AuthActivity extends AppCompatActivity {
    private FrameLayout authFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        authFragment = findViewById(R.id.authFragment);

        getSupportFragmentManager().beginTransaction().replace(R.id.authFragment, new OtpSendFragment()).commit();

        /*STATE = sharedPreferences.getInt("STATE",0);

        if (STATE == 1){
            startActivity(new Intent(AuthActivity.this,MainActivity.class));
            finish();
        }else{
            getSupportFragmentManager().beginTransaction().replace(R.id.authFragment, new OtpSendFragment()).commit();
        }*/
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = activity.getCurrentFocus();
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}