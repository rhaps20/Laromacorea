package com.keehl.laromacorea;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;

import static android.content.ContentValues.TAG;

public class LoginActivity extends Activity implements View.OnClickListener {
    private Button loginButton;
    private Button exitButton;
    private TextView joinButton;
    private EditText idForm;
    private EditText passwordForm;
    private String sessionId;
    private CheckBox saveIdCheckBox;
    private LoginAsync async;
    private UpdateCheckAsync updateCheckAsync;
    private DBHelper dbHelper;
    private boolean updateCheck = false;
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

        joinButton = findViewById(R.id.joinButton);
        joinButton.setOnClickListener(this);


        saveIdCheckBox = findViewById(R.id.saveIdCheck);
        saveIdCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    dbHelper.insert(idForm.getText().toString(), passwordForm.getText().toString());
                } else {
                    dbHelper.delete(idForm.getText().toString());
                    dbHelper.clearDB();
                    idForm.setText("");
                    passwordForm.setText("");
                }
            }
        });
        dbHelper = new DBHelper(getApplicationContext(), "userInfo.db", null, 1);


        String temp = dbHelper.getResult();
        String userId = "";
        String pass = "";

        if (!temp.equals("")) {
            saveIdCheckBox.setChecked(true);
            userId = temp.split(":")[0];
            pass = temp.split(":")[1].split(" ")[0];
        }

        idForm.setText(userId);
        passwordForm.setText(pass);

        grantInternetAccessPermission();
        grantExternalStoragePermission();


    }

    public void onClick(View view) {
        int id = view.getId();

        switch (id) {
            case R.id.loginButton:

                if (Connectivity.isConnected(this)) {

                    updateCheckAsync = new UpdateCheckAsync();
                    updateCheckAsync.execute();

                    if (saveIdCheckBox.isChecked()) {
                        dbHelper.clearDB();
                        String userId = idForm.getText().toString();
                        String userPass = passwordForm.getText().toString();
                        dbHelper.insert(userId, userPass);
                    }
                } else {
                    Utils.showToast(this, "인터넷 연결 상태를 확인해주세요.", Toast.LENGTH_LONG);
                }

                break;
            case R.id.exitButton:
                android.os.Process.killProcess(android.os.Process.myPid());
                break;
            case R.id.joinButton:
                Utils.showToast(this, "지원하지 않는 기능입니다. 업데이트 준비중입니다.", Toast.LENGTH_LONG);
            //    Intent it = new Intent(LoginActivity.this, JoinActivity.class);
            //    startActivity(it);
                break;
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


    private boolean grantExternalStoragePermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG,"Permission is granted");

                return true;
            }else{
                Log.v(TAG,"Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
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
    public class LoginAsync extends AsyncTask<Void, Void, Void> {
        private Connection.Response loginForm;
        private String loginResult;
        private ProgressDialog pDialog;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(LoginActivity.this);
            pDialog.show();
        }


        @Override
        protected Void doInBackground(Void... integers) {
        //    Document doc = Jsoup.connect("http://www.laromacorea.com").get();

            try{
                //Document doc1 = Jsoup.connect("http://www.laromacorea.com/").get();

                loginForm = Jsoup.connect("http://www.laromacorea.com/bbs/login_check.php")
                        .data("user_id", idForm.getText().toString())
                        .data("password", passwordForm.getText().toString())
                        .method(Connection.Method.POST)
                        .execute();


// 세션 유지를 위한 세션 아이디
                sessionId = loginForm.cookie("PHPSESSID");
                UserInfo.cookie = sessionId;
                loginResult = loginForm.parse().text();
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
            pDialog.cancel();
            try {

                if (loginResult.contains("실패")) {
                    Utils.showToast(LoginActivity.this, "아이디 비밀번호를 확인해주세요", Toast.LENGTH_LONG);
                } else {
                    Intent it = new Intent(LoginActivity.this, HomeActivity.class);
                    //    String text = sessionId;
                    it.putExtra("sessionId", sessionId);
                    startActivity(it);
                    finish();
                }

            } catch (Exception ex) {
                Log.d("LoginActivity", ex.getMessage());
            }


        }


    }


    public class UpdateCheckAsync extends AsyncTask<Void, Void, Void> {
        private String url = "https://play.google.com/store/apps/details?id=com.keehl.laromacorea";
        private String versionCode;
        private ProgressDialog pDialog;
        @Override
        protected void onPreExecute() {

            super.onPreExecute();
            pDialog = new ProgressDialog(LoginActivity.this);
            pDialog.show();

            Utils.showToast(LoginActivity.this, "버전체크 중 입니다.", Toast.LENGTH_LONG);
        }


        @Override
        protected Void doInBackground(Void... integers) {
            //    Document doc = Jsoup.connect("http://www.laromacorea.com").get();

            try{
                //Document doc1 = Jsoup.connect("http://www.laromacorea.com/").get();

                Document doc = Jsoup.connect(url)
                        .method(Connection.Method.POST)
                        .get();

                Elements version = doc.select("div[class=T4LgNb]").select("div[class=ZfcPIb]").select("div[class=UTg3hd]")
                .select("div[class=JNury Ekdcne]").select("div[class=LXrl4c]")
                .select("div[class=W4P4ne ]").select("div[class=JHTxhe IQ1z0d]").select("div[class=IxB2fe]")
                .select("div[class=hAyfc]").select("span[class=htlgb]").select("div[class=IQ1z0d]").select("span[class=htlgb]");

                versionCode = version.get(3).text();

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

            try {
                pDialog.cancel();
                String versionName = BuildConfig.VERSION_NAME;

                 if (!versionName.equals(versionCode)) {
                     Utils.showToast(LoginActivity.this, "업데이트가 필요합니다.", Toast.LENGTH_LONG);

                     AlertDialog.Builder dialog = new AlertDialog.Builder(LoginActivity.this);

                     dialog.setTitle("알림");
                     dialog.setMessage("플레이스토어로 이동하시겠습니까?");
                     dialog.setCancelable(false);
                     dialog.setPositiveButton("예", new DialogInterface.OnClickListener() {
                         @Override
                         public void onClick(DialogInterface dialog, int which) {
                             final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
                             try {
                                 startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                             } catch (android.content.ActivityNotFoundException anfe) {
                                 startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                             }
                         //    updateCheck = true;
                         }
                     });
                     dialog.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                         @Override
                         public void onClick(DialogInterface dialog, int which) {
                             async = new LoginAsync();
                             async.execute();
                         }
                     });
                     dialog.show();
                 } else {
                     async = new LoginAsync();
                     async.execute();
                 }
            } catch (Exception ex) {
                Log.d("LoginActivity", ex.getMessage());
            }
        }


    }
    @Override
    public void onBackPressed() {
    //    super.onBackPressed();
    //    finish();
    }
}
