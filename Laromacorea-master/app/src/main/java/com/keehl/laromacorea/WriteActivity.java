package com.keehl.laromacorea;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;


import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URLDecoder;

public class WriteActivity extends Activity implements View.OnClickListener{
    private String sessionId = null;
    private String htmlPageUrl;
    private String title;
    private String boardId;
    private String userId;

    private EditText titleEditText;
    private EditText contentEditText;

    private Button confirmButton;
    private Button cancelButton;

    private Button previewButton;
    private Button imageButton;
    private RadioGroup radioGroup;

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
            } else {
                htmlPageUrl = extra.getString("url");
                title = extra.getString("text");
                sessionId = extra.getString("sessionId");
                boardId = extra.getString("boardId");
                userId = extra.getString("id");
            }
        } else {
            htmlPageUrl = savedInstanceState.getString("url");
            title = extra.getString("text");
            sessionId = extra.getString("sessionId");
            boardId = extra.getString("boardId");
            userId = extra.getString("id");
        }
        sessionId = DataContainer.cookies;

        titleEditText = findViewById(R.id.titleEditText);
        contentEditText = findViewById(R.id.contentEditText);

        confirmButton = findViewById(R.id.confirmButton);
        confirmButton.setOnClickListener(this);
        cancelButton = findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(this);

        previewButton = findViewById(R.id.previewButton);
        previewButton.setOnClickListener(this);
        imageButton = findViewById(R.id.imageButton);
        imageButton.setOnClickListener(this);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }


    @Override
    public void onClick(View view) {
        int id = view.getId();
        Intent it;
        switch(id) {
            case R.id.confirmButton:
                new WritePostTask().execute();
                break;
            case R.id.cancelButton:
                finish();
                break;
            case R.id.previewButton:
                new MatchDialog(this, contentEditText.getText().toString()).show();
                break;
            case R.id.imageButton:
                uploadeImage();
                break;
        }
    }
    public class WritePostTask extends AsyncTask<Void, Void, Void> {
        private String writePostUrl = "http://www.laromacorea.com/bbs/write_ok.php";
        private ProgressDialog pDialog;

        @Override
        protected void onPreExecute () {
            super.onPreExecute();
            pDialog = new ProgressDialog(WriteActivity.this);
            pDialog.show();
            contentEditText.append("<br><b><br>-from laromacorea mobile-</b>");
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {

                String id = getBoardName(boardId);
                 Jsoup.connect(writePostUrl)
                        .cookie("PHPSESSID", sessionId)
                        .postDataCharset("euc-kr")
                        .data("id", id)
                        .data("subject",
                                URLDecoder.decode(titleEditText.getText().toString(), "UTF-8"))
                        .data("memo",
                                URLDecoder.decode(contentEditText.getText().toString(), "UTF-8"))
                        .data("use_html",
                                "ok")
                        .method(Connection.Method.POST)
                        .referrer(htmlPageUrl)
                        .execute();
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
            Intent it = new Intent(WriteActivity.this, BoardActivity.class);

            String url = "http://www.laromacorea.com/bbs/zboard.php?id=";
            String id = getBoardName(boardId);
            url += id;
            it.putExtra("url", url);
            it.putExtra("sessionId", sessionId);
            it.putExtra("text", id);
            startActivity(it);
            finish();
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
            pDialog = new ProgressDialog(WriteActivity.this);
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
}
