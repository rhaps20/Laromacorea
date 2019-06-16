package com.keehl.laromacorea;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.IOException;


public class JoinActivity extends Activity implements View.OnClickListener {
    private Button joinButton;
    private Button exitButton;
    private Button duplicateCheckButton;
    private EditText idForm;
    private EditText passwordForm1;
    private EditText passwordForm2;
    private EditText nameForm;
    private EditText emailForm;
    private EditText homepageForm;
    private EditText introduceForm;

    private CheckBox mailingCheckBox;
    private CheckBox openInformationCheckBox;

    private JoinAsync joinAsync;
    private DuplicateCheck duplicateCheck;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);

        idForm = (EditText)findViewById(R.id.idForm);
        passwordForm1 = (EditText)findViewById(R.id.passwordForm1);
        passwordForm2 = (EditText)findViewById(R.id.passwordForm2);

        nameForm = findViewById(R.id.nameForm);
        emailForm = findViewById(R.id.emailForm);
        homepageForm = findViewById(R.id.homepageForm);
        introduceForm = findViewById(R.id.introduceEditText);

        mailingCheckBox = findViewById(R.id.mailingCheckBox);
        openInformationCheckBox = findViewById(R.id.openInfoCheckBox);

        duplicateCheckButton = findViewById(R.id.duplicateCheck);
        duplicateCheckButton.setOnClickListener(this);

        joinButton = (Button)findViewById(R.id.joinButton);
        joinButton.setOnClickListener(this);

        exitButton = (Button)findViewById(R.id.exitButton);
        exitButton.setOnClickListener(this);

        idForm.setText("dkdlel123");
        passwordForm1.setText("1324abc");
        passwordForm2.setText("1324abc");
        nameForm.setText("이름이");
        emailForm.setText("hansun90620@naver.com");
        homepageForm.setText("");
        introduceForm.setText("");
        mailingCheckBox.setChecked(true);
        openInformationCheckBox.setChecked(true);
    }

    public void onClick(View view) {
        int id = view.getId();

        switch (id) {
            case R.id.duplicateCheck:
                Utils.showToast(this, "중복체크 합니다.", Toast.LENGTH_LONG);
                duplicateCheck = new DuplicateCheck();
                duplicateCheck.execute();
                break;
            case R.id.joinButton:
                Utils.showToast(this, "회원가입 합니다.", Toast.LENGTH_LONG);
                joinAsync = new JoinAsync();
                joinAsync.execute();
                break;
            case R.id.exitButton:
                finish();
            default:
                break;
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    public class DuplicateCheck extends AsyncTask<Void, Void, Void> {
        private String url = "http://www.laromacorea.com/bbs/check_user_id.php";
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        public String getCheckBoxState(CheckBox checkBox) {
            if (checkBox.isChecked()) return "ok";
            else return "no";
        }
        @Override
        protected Void doInBackground(Void... integers) {
            try{

                Jsoup.connect(url)
                        .data("id", idForm.getText().toString())
                        .data("group_no", "" + 1)
                        .data("mode", "join")
                        .referrer("http://www.laromacorea.com/index1.html")
                        .method(Connection.Method.POST)
                        .execute();
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
        }
    }

    public class JoinAsync extends AsyncTask<Void, Void, Void> {
        private String url = "http://www.laromacorea.com/bbs/member_join_ok.php";
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        public String getCheckBoxState(CheckBox checkBox) {
            if (checkBox.isChecked()) return "ok";
            else return "no";
        }
        @Override
        protected Void doInBackground(Void... integers) {
            try{

                Jsoup.connect(url)
                        .data("user_id", idForm.getText().toString())
                        .data("password", passwordForm1.getText().toString())
                        .data("password1", passwordForm2.getText().toString())
                        .data("name", nameForm.getText().toString())
                        .data("email", emailForm.getText().toString())
                        .data("homepage", homepageForm.getText().toString())
                        .data("mailing", getCheckBoxState(mailingCheckBox))
                        .data("comment", introduceForm.getText().toString())
                        .data("openinfo", getCheckBoxState(openInformationCheckBox))
                        .method(Connection.Method.POST)
                        .execute();

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
            Intent it = new Intent(JoinActivity.this, LoginActivity.class);
            startActivity(it);
            finish();
        }


    }
}
