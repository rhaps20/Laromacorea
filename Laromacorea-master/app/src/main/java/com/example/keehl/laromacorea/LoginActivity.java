package com.example.keehl.laromacorea;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

import static android.content.ContentValues.TAG;

public class LoginActivity extends Activity implements View.OnClickListener {
    private Button loginButton;
    private Button exitButton;
    private EditText idForm;
    private EditText passwordForm;
    private String sessionId;
    private String zbsessionId;
    private NetworkAsync async;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        idForm = (EditText)findViewById(R.id.idForm);
        passwordForm = (EditText)findViewById(R.id.passwordForm);

        loginButton = (Button)findViewById(R.id.loginButton);
        loginButton.setOnClickListener(this);

        exitButton = (Button)findViewById(R.id.exitButton);
        exitButton.setOnClickListener(this);

        idForm.setText("lalupo");
        passwordForm.setText("dkv#ckrl12");


        grantInternetAccessPermission();


        async = new NetworkAsync();
        async.execute();

        Intent it = new Intent(LoginActivity.this, HomeActivity.class);

        String text = sessionId;

        it.putExtra("sessionId", sessionId);

        startActivity(it);
        finish();
    }

    public void onClick(View view) {
        int id = view.getId();

        switch (id) {
            case R.id.loginButton:
                async = new NetworkAsync();
                async.execute();

                break;
            case R.id.exitButton:
                finish();
            default:
                break;
        }

    }
    public static int checkSelfPermission(Context context, String permission) {
        if (permission == null) {
            throw new IllegalArgumentException("permission is null");
        }

        return context.checkPermission(permission, android.os.Process.myPid(), android.os.Process.myUid());
    }

    //	@Override
    private boolean grantInternetAccessPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(getApplicationContext(), Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG,"Permission is granted");

                return true;
            }else{
                Log.v(TAG,"Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                //	ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);

                return false;
            }

        }else{
            Toast.makeText(this, "External Storage Permission is Grant", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "External Storage Permission is Grant ");

            return true;
        }
        //return true;
    }

    public class NetworkAsync extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }


        @Override
        protected Void doInBackground(Void... integers) {
        //    Document doc = Jsoup.connect("http://www.laromacorea.com").get();

            try{
                //Document doc1 = Jsoup.connect("http://www.laromacorea.com/").get();

                Connection.Response loginForm = Jsoup.connect("http://www.laromacorea.com/bbs/login_check.php")
                        .data("user_id", "lalupo")
                        .data("password", "dkv#ckrl12")
                        .method(Connection.Method.POST)
                        .execute();

// 세션 유지를 위한 세션 아이디
                sessionId = loginForm.cookie("PHPSESSID");
                UserInfo.cookie = sessionId;
                finish();
            } catch(IOException ex) {
                Log.d("Login method", "error - " + ex.getMessage());

            } finally {
                finish();
            }
            return null;
        }



        @Override
        protected void onProgressUpdate(Void... params) {

        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            Intent it = new Intent(LoginActivity.this, HomeActivity.class);

            String text = sessionId;

            it.putExtra("sessionId", sessionId);

            startActivity(it);

            finish();
        }


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
