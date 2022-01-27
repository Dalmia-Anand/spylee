package com.spylee;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class LostModeActivity extends BackgroundActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lost_mode);
    }
    void goHome(View view) {
        Intent intent = new Intent(LostModeActivity.this, MainActivity.class);
        startActivity(intent);
    }
}
