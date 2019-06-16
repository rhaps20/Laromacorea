package com.keehl.laromacorea;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.WindowManager;
import android.webkit.WebView;

public class PersonalTableDialog extends Dialog {
    private WebView webView;
    private String htmlTemp;
    private Context context;

    public PersonalTableDialog(Context context, String htmlTemp) {
        super(context);
        this.htmlTemp = htmlTemp;
        this.context = context;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    //    setContentView(R.layout.dialog_match);

        // webView = findViewById(R.id.matchInfo);
        webView = new WebView(context);
        //    webView.loadData(htmlTemp, "text/html", "UTF-8");

        setContentView(webView);
        /*
        * WindowManager.LayoutParams lp = getWindow().getAttributes();
   //     getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        lp.alpha = 0.5f;
        lp.gravity = Gravity.CENTER;
        getWindow().setAttributes(lp);

        * */
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        //  getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        lp.alpha = 0.5f;
    //    lp.gravity = Gravity.CENTER;
        getWindow().setAttributes(lp);


        imgcng(htmlTemp, webView);
    }
    public  String creHtmlBody(String imagUrl){
        StringBuffer sb = new StringBuffer("<HTML>");
        sb.append("<HEAD>");
        sb.append("</HEAD>");
        sb.append("<BODY style='margin:0; padding:0; text-align:left;'>");
        sb.append(imagUrl);
        sb.append("</BODY>");
        sb.append("</HTML>");
        return sb.toString();
    }

    public void imgcng(String url1, WebView webView){


        webView.setVerticalScrollBarEnabled(false);
        webView.setVerticalScrollbarOverlay(false);
        webView.setHorizontalScrollBarEnabled(false);
        webView.setHorizontalScrollbarOverlay(false);
        webView.loadDataWithBaseURL(null,creHtmlBody(url1), "text/html", "utf-8", null);

    }
}
