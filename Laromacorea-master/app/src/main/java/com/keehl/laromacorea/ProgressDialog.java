package com.keehl.laromacorea;

import android.app.Dialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.ProgressBar;



public class ProgressDialog  extends Dialog {
    private ProgressBar pbar;

    public ProgressDialog(Context context) {
        super(context);
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_progressbar);
        pbar = findViewById(R.id.progressBar);

        WindowManager.LayoutParams lp = getWindow().getAttributes();
        //  getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        lp.gravity = Gravity.CENTER;
        getWindow().setAttributes(lp);

        this.setCancelable(false);
        new ProgressTask().execute();
    }


    public class ProgressTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pbar.setMax(1000);
        }


        @Override
        protected Void doInBackground(Void... voids) {

            return null;
        }

        @Override
        protected void onProgressUpdate(Void... params) {
            super.onProgressUpdate();
            for (int i = 0; i <= 1000; i++) {
                pbar.setProgress(i);
            }
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

        }
    }

    protected ProgressBar getProgressBar() {
        return pbar;
    }
}
