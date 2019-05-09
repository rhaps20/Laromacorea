package com.example.keehl.laromacorea;

import android.app.Activity;
import android.os.Bundle;

public class WriteActivity extends Activity {
    private String sessionId = null;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write);

        sessionId = DataContainer.cookies;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
