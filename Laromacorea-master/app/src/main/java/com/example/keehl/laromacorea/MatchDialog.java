package com.example.keehl.laromacorea;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.WindowManager;
import android.webkit.WebView;
public class MatchDialog extends Dialog {
    private WebView webView;
    private String htmlTemp;
    private Context context;

    public MatchDialog(Context context) {
        super(context);
        this.context = context;
    }
    public MatchDialog(Context context, String htmlTemp) {
        super(context);
        this.htmlTemp = htmlTemp;
        this.context = context;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_match);

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
        lp.gravity = Gravity.CENTER;
        getWindow().setAttributes(lp);


        imgcng(htmlTemp, webView);
    }
    public  String creHtmlBody(String imagUrl){
        StringBuffer sb = new StringBuffer("<HTML>");
        sb.append("<HEAD>");
        sb.append("</HEAD>");
        sb.append("<BODY style='margin:0; padding:0; text-align:center;'>");    //중앙정렬
    //    sb.append("<img src=\"" + imagUrl+"\">");    //지 비율에 맞게 나옴

        sb.append("<img width='100%' height='100%' src=\"" + imagUrl+"\">"); //가득차게 나옴

        sb.append("</BODY>");
        sb.append("</HTML>");
        return sb.toString();
    }

    public void imgcng(String url1, WebView webView){


        webView.setVerticalScrollBarEnabled(false);
        webView.setVerticalScrollbarOverlay(false);
        webView.setHorizontalScrollBarEnabled(false);
        webView.setHorizontalScrollbarOverlay(false);
    //    webview01.setInitialScale(100);
        webView.loadDataWithBaseURL(null,creHtmlBody(url1), "text/html", "utf-8", null);

    }
}
