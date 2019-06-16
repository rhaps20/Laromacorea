package com.keehl.laromacorea;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class ModifyActivity extends Activity implements View.OnClickListener{
    private String htmlPageUrl;
    private String title;
    private String sessionId;
    private String boardId;
    private String userId;
    private String content;

    private EditText titleEditText;
    private EditText contentEditText;

    private Button imageButton;
    private Button previewButton;
    private Button confirmButton;
    private Button cancelButton;


    private ModifyPostTask modifyPostTask;
    private ImageUploadTask imageUploadTask;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write);

        Bundle extra = getIntent().getExtras();

        if (savedInstanceState == null) {
            if (extra == null) {
                htmlPageUrl = null;
                title = null;
                sessionId = null;
                boardId = null;
                userId = null;
                content = null;
            } else {
                htmlPageUrl = extra.getString("url");
                title = extra.getString("text");
                sessionId = extra.getString("sessionId");
                boardId = extra.getString("boardId");
                userId = extra.getString("id");
                content = extra.getString("content");
            }
        } else {
            htmlPageUrl = savedInstanceState.getString("url");
            title = extra.getString("text");
            sessionId = extra.getString("sessionId");
            boardId = extra.getString("boardId");
            userId = extra.getString("id");
            content = extra.getString("content");
        }

        titleEditText = findViewById(R.id.titleEditText);
        titleEditText.setText(title);

        contentEditText = findViewById(R.id.contentEditText);
        contentEditText.setText(content);

        confirmButton = findViewById(R.id.confirmButton);
        confirmButton.setOnClickListener(this);
        cancelButton = findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(this);

        imageButton = findViewById(R.id.imageButton);
        imageButton.setOnClickListener(this);
        previewButton = findViewById(R.id.previewButton);
        previewButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

        switch (id) {

            case R.id.confirmButton:
                if (userId.equals(UserInfo.userId)) {
                    modifyPostTask = new ModifyPostTask();
                    modifyPostTask.execute();
                    Utils.showToast(this, "글을 수정합니다.", Toast.LENGTH_LONG);
                    finish();
                } else {
                    Utils.showToast(this, "본인 글이 아닙니다.", Toast.LENGTH_LONG);
                }
                break;

            case R.id.cancelButton:
                finish();
                break;

            case R.id.imageButton:
                uploadeImage();
                break;

            case R.id.previewButton:
                new MatchDialog(this, contentEditText.getText().toString()).show();
                break;
        }
    }


    public class ModifyPostTask extends AsyncTask<Void, Void, Void> {
        private ProgressDialog pDialog;
        private String writePostUrl = "http://www.laromacorea.com/bbs/write_ok.php";
        private String modifyPostUrl = "http://www.laromacorea.com/bbs/modify_ok.php";
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(ModifyActivity.this);
        //    pDialog.show();
        }

        @Override
        protected Void doInBackground(Void... integers) {
            try {
                String title = titleEditText.getText().toString();
                String memo = contentEditText.getText().toString();
                String num = UserInfo.currPostNum;

                if (!memo.contains("-from Laromacorea mobile-")) {
                    memo += "-from Laromacorea mobile-";
                }
                Jsoup.connect(writePostUrl).cookie("PHPSESSID", sessionId)
                        .postDataCharset("euc-kr")
                        .data("id", getBoardName(boardId))
                        .data("no", num)
                        .data("mode", "modify")
                        .data("subject", title)
                        .data("memo", memo)
                        .data("use_html", "ok")
                        .referrer(modifyPostUrl)
                        .method(Connection.Method.POST).execute();
            } catch (Exception ex) {

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
            finish();
        }
    }

    public void uploadeImage() {
        startActivityForResult(Intent.createChooser(new Intent().setType("*/*")
                        .setAction("android.intent.action.GET_CONTENT"),
                "Select a image file"), 123);

    }

    public void onActivityResult(int paramInt, int paramInt2, Intent paramIntent ) {
        super.onActivityResult(paramInt, paramInt2,paramIntent);

        if ((paramInt == 123) && (paramInt2 == -1)) {
            String path = Utils.getPath(this, paramIntent.getData());
            imageUploadTask = new ImageUploadTask();
            imageUploadTask.setPath(path);
            imageUploadTask.execute();
        }
    }

    public class ImageUploadTask extends AsyncTask<Void, Void, Void> {
        private String url = "http://www.zpat.info";
        private ProgressDialog pDialog;
        private String path;
        private String imageUrl;
        private File file;
        public ImageUploadTask(){}


        @Override
        protected void onPreExecute () {
            super.onPreExecute();
            pDialog = new ProgressDialog(ModifyActivity.this);
            pDialog.show();
            file = new File(path);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {

                Connection.Response imageUploadForm = Jsoup.connect(url)
                        .postDataCharset("euc-kr")
                        .data("upfile[]", file.getName(), new FileInputStream(file))
                        .followRedirects(true)
                        .method(Connection.Method.POST)
                        .execute();


                Document doc = imageUploadForm.parse();
/********************************************************************/

                Elements imageUrlContainer = doc.select("section[class=bg-primary]")
                        .select("div[class=container-fluid]")
                        .select("div[class=row]")
                        .select("div[class=col-xs-12 col-md-8 col-md-offset-2]")
                        .select("textarea[class=form-control]");


                Element ele = imageUrlContainer.get(1);

                imageUrl = ele.text();



            }catch(IOException ie) {

            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Void... params) {
            super.onProgressUpdate();
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            pDialog.cancel();

            if (imageUrl == null) imageUrl = "";
            contentEditText.setText(contentEditText.getText().toString() + "\n" + imageUrl);
        }

        public void setPath(String path) {
            this.path = path;
        }
    }



    public String getBoardName(String boardId) {
        if (boardId.equals("Noitce") || boardId.equals("notice")) return "notice";
        else if (boardId.equals("Squad") || boardId.equals("squad")) return "squad";
        else if (boardId.equals("Match") || boardId.equals("match") || boardId.equals("1213match")) return "1213match";
        else if (boardId.equals("Calcio") || boardId.equals("calcio")) return "calcio";
        else if (boardId.equals("Free") || boardId.equals("free")) return "free2";
        else if (boardId.equals("Special") || boardId.equals("special") || boardId.equals("sp")) return "sp";
        else if (boardId.equals("Media") || boardId.equals("media")) return "media";

        return null;
    }
}


