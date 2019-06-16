package com.keehl.laromacorea;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.IOException;


public class AutoLoginActivity extends Activity implements View.OnClickListener {
    private Button loginButton;
    private Button exitButton;
    private EditText idForm;
    private EditText passwordForm;
    private String sessionId;
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


    //    grantInternetAccessPermission();
    //    grantExternalStoragePermission();


        async = new NetworkAsync();
        async.execute();
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

    //	@Override

    public class NetworkAsync extends AsyncTask<Void, Void, Void> {
        private DBHelper dbHelper;
        private String userId;
        private String pass;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dbHelper = new DBHelper(getApplicationContext(), "userInfo.db", null, 1);


            String temp = dbHelper.getResult();

            userId = temp.split(":")[0];
            pass = temp.split(":")[1];
            Utils.showToast(AutoLoginActivity.this, "로그인이 해제되어 재로그인 합니다.", Toast.LENGTH_LONG);
        }


        @Override
        protected Void doInBackground(Void... integers) {
            //    Document doc = Jsoup.connect("http://www.laromacorea.com").get();

            try{
                //Document doc1 = Jsoup.connect("http://www.laromacorea.com/").get();
                Connection.Response loginForm = Jsoup.connect("http://www.laromacorea.com/bbs/login_check.php")
                        .data("user_id", userId)
                        .data("password", pass)
                        .method(Connection.Method.POST)
                        .execute();


// 세션 유지를 위한 세션 아이디
                sessionId = loginForm.cookie("PHPSESSID");
                UserInfo.cookie = sessionId;
                //    finish();
            } catch(IOException ex) {
                Log.d("Login method", "error - " + ex.getMessage());

            } finally {
                //    finish();
            }
            return null;
        }



        @Override
        protected void onProgressUpdate(Void... params) {

        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            Intent it = new Intent(AutoLoginActivity.this, HomeActivity.class);

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
