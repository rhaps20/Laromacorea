package com.example.keehl.laromacorea;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;


import com.navdrawer.SimpleSideDrawer;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.ArrayList;

public class ContentActivity_prev extends Activity implements View.OnClickListener{
    private SimpleSideDrawer slideMenu;
    private SimpleSideDrawer slideMatch;
    private String htmlComment = "http://www.laromacorea.com/bbs/comment_ok.php";
    private String htmlPageUrl;
    private String title;
    private String sessionId;
    private String userId;
    private TextView titleTv;
    private WebView contentTv;
    private TextView writerTv;
    private ListView replyListView;
    private HomeListViewAdapter listViewAdapter;

    private Button menuButton;
    private Button matchButton;


    private Button homeButton_menu;
    private Button noticeButton_menu;
    private Button squadButton_menu;
    private Button clubutton_menu;
    private Button matchButton_menu;
    private Button calcioButton_menu;
    private Button freeButton_menu;
    private Button specialButton_menu;
    private Button mediaButton_menu;
    private Button iconButton_menu;
    private Button replyButton;
    private EditText replyEditText;
    private WebView matchInfo;
    private LinearLayout linearLayout;
    private ScrollView scrollView;
    private ScrollView scrollView2;
    private InputMethodManager imm;
    private ArrayList<HomeListViewItem> replyList;

    private ReadContentTask readContent;
    private ReadReplyTask readReply;
    private AddReplyTask addReply;
    private ReadMatchInfoTask readMatchInfo;

    private String boardId;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content_prev);

        imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
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

        init();

        sessionId = DataContainer.cookies;
        readContent = new ReadContentTask();
        readContent.execute();
        readMatchInfo = new ReadMatchInfoTask();
        readMatchInfo.execute();
        readReply = new ReadReplyTask();
        readReply.execute();
    }
    public void init() {
        scrollView = (ScrollView)findViewById(R.id.scrollView);

        linearLayout = (LinearLayout)findViewById(R.id.contentView);
        titleTv = (TextView)findViewById(R.id.contentTitle);
        contentTv = (WebView)findViewById(R.id.contentText);
        replyButton = (Button)findViewById(R.id.replyButton);
        replyButton.setOnClickListener(this);
        replyEditText = (EditText)findViewById(R.id.replyEditText);

        matchInfo = (WebView)findViewById(R.id.matchInfo);

        //     matchInfo.getSettings().setJavaScriptEnabled(true);

// 스크롤바 없애기
        contentTv.setHorizontalScrollBarEnabled(true);
        contentTv.setVerticalScrollBarEnabled(true);
        contentTv.setBackgroundColor(0);

        writerTv = (TextView)findViewById(R.id.writer);

        //   scrollView2 = (ScrollView)findViewById(R.id.scrollView2);


        menuButton = (Button)findViewById(R.id.menuButton_content);
        matchButton = (Button)findViewById(R.id.matchButton_content);


        titleTv.setText(title);

        slideMenu = new SimpleSideDrawer(this);
        slideMenu.setLeftBehindContentView(R.layout.slide_menu);

        slideMatch = new SimpleSideDrawer(this);
        slideMatch.setRightBehindContentView(R.layout.slide_game);

        matchInfo = (WebView)findViewById(R.id.matchInfo);
        menuButton.setOnClickListener(this);
        matchButton.setOnClickListener(this);

        homeButton_menu = (Button)findViewById(R.id.homeButton_menu);
        homeButton_menu.setOnClickListener(this);
        noticeButton_menu = (Button)findViewById(R.id.noticeButton_menu);
        noticeButton_menu.setOnClickListener(this);
        squadButton_menu= (Button)findViewById(R.id.squadButton_menu);
        squadButton_menu.setOnClickListener(this);;
        clubutton_menu = (Button)findViewById(R.id.clubButton_menu);
        clubutton_menu.setOnClickListener(this);
        matchButton_menu = (Button)findViewById(R.id.matchButton_menu);
        matchButton_menu.setOnClickListener(this);
        calcioButton_menu = (Button)findViewById(R.id.calcioButton_menu);
        calcioButton_menu.setOnClickListener(this);
        freeButton_menu =(Button)findViewById(R.id.freeButton_menu);
        freeButton_menu.setOnClickListener(this);
        specialButton_menu = (Button)findViewById(R.id.specialButton_menu);
        specialButton_menu.setOnClickListener(this);
        mediaButton_menu = (Button)findViewById(R.id.mediaButton_menu);
        mediaButton_menu.setOnClickListener(this);
        iconButton_menu = (Button)findViewById(R.id.iconButton_menu);
        iconButton_menu.setOnClickListener(this);


        listViewAdapter = new HomeListViewAdapter();
        listViewAdapter.setMode(HomeListViewAdapter.REPLY_MODE);
        replyListView = (ListView)findViewById(R.id.replyList);
        replyListView.setAdapter(listViewAdapter);
        replyList = new ArrayList<HomeListViewItem>();


        //   setListViewHeightBasedOnChildren(replyListView, scrollView2);
        //   replyListView.addHeaderView(scrollView2);
        getListViewSize(replyListView);
    }
    public static void getListViewSize(ListView myListView) {
        ListAdapter myListAdapter = myListView.getAdapter();
        if (myListAdapter == null) {
            // do nothing return null
            return;
        }
        // set listAdapter in loop for getting final size
        int totalHeight = 0;
        int desiredWidth = View.MeasureSpec.makeMeasureSpec(myListView.getWidth(), View.MeasureSpec.AT_MOST);
// 각 item view 마다 크기가 다를 수 있음으로 각 item view 의 size 만큼 더한다.
        for (int size = 0; size < myListAdapter.getCount(); size++) {
            View listItem = myListAdapter.getView(size, null, myListView);
            listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += listItem.getMeasuredHeight();
        }

        // setting listview item in adapter
        ViewGroup.LayoutParams params = myListView.getLayoutParams();
// divider 도 크기가 있기 때문에 따로 더해줘야 한다.
        params.height = totalHeight + (myListView.getDividerHeight() * (myListAdapter.getCount() - 1));
        myListView.setLayoutParams(params);
        // layout view 모양이 바꼇다고 알려준다. onlayout 이 호출 된다.
        myListView.requestLayout();
        // print height of adapter on log
        Log.i("height of listItem:", String.valueOf(totalHeight));
    }

    public void setListViewHeightBasedOnChildren(ListView listView, ScrollView scrollView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = 0;
        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.AT_MOST);

        View listItem = null;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            listItem = listAdapter.getView(i, null, listView);
            listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();


        ViewGroup.LayoutParams params2 = scrollView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        scrollView.setLayoutParams(params2);
        scrollView.requestLayout();
    }

    public class ReadContentTask extends AsyncTask<Void, Void, Void> {
        private String writer;
        private String content;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }


        @Override
        protected Void doInBackground(java.lang.Void... voids) {

            try {
                Document matchDoc = Jsoup.connect(htmlPageUrl).cookie("PHPSESSID", sessionId).get();

                Element writerElement = matchDoc.select("span[style]").get(4);
                Element contentElement = matchDoc.select("td[valign]").get(3);

                if (userId == null) writer = writerElement.text();
                else writer = userId;
                content = contentElement.html();
            }catch(IOException ie) {
                Log.d("ContentActivity_ReadContentTask", ie.getMessage());
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
            writerTv.setText(writer + "님의 글 입니다.");
            contentTv.loadData(content, "text/html", "UTF-8");
        }
    }


    public class ReadReplyTask extends AsyncTask<Void, Void, Void> {
        private Document doc;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //   replyListView.setAdapter(listViewAdapter);
        }

        protected void clear() {
            replyList.clear();
            listViewAdapter.getArrayList().clear();
            listViewAdapter.notifyDataSetChanged();
            replyListView.setAdapter(null);
        }

        @Override
        protected Void doInBackground(java.lang.Void... voids) {
            //clear();
            try{

                Document doc = Jsoup.connect(htmlPageUrl).cookie("PHPSESSID", sessionId).get();

                Elements commentContents = doc.select("table[width=974]").select("tbody")
                        .select("tr").select("td[width=640]")
                        .select("div[align=center]").select("table[cellpadding=0][style=table-layout:fixed]")
                        .select("tbody").select("tr").select("td")
                        .select("table[cellpadding=0]").select("tbody")
                        .select("tr").select("td").select("table[cellpadding=0]")
                        .select("tbody").select("tr[height=22]").select("td")
                        .select("table[border=0]").select("tbody").select("tr");

                int i = 0;
                for (Element comment : commentContents) {
                    HomeListViewItem item = new HomeListViewItem();


                    String iconUrl = comment.select("td[nowrap]").select("img").attr("src");
                    String userId = comment.select("td[nowrap]").select("b").select("span[onmousedown]").text();
                    String contents = comment.select("td[style]").get(1).text();

                    ContentsData contentsData = new ContentsData();
                    contentsData.str = contents;
                    contentsData.id = userId;

                    item.setUserId(userId);
                    item.setImageUrl("http://www.laromacorea.com/bbs/" + iconUrl);
                    item.setContents(contentsData);

                    replyList.add(item);
                    i++;
                }


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
            listViewAdapter.setMode(HomeListViewAdapter.REPLY_MODE);
            replyListView.setAdapter(listViewAdapter);
            int size = replyList.size();
            for (int i = 0; i < size; i++) {
                listViewAdapter.addItem(
                        null,
                        replyList.get(i).getTitle(),
                        replyList.get(i).getContents(),
                        replyList.get(i).getUserId(),
                        replyList.get(i).getDate(),
                        null,
                        replyList.get(i).getImageUrl(),
                        replyList.get(i).getIsNotice()
                );
            }
            //    replyList.clear();
            listViewAdapter.notifyDataSetChanged();
            replyListView.setFocusable(true);
        }
    }


    public class AddReplyTask extends AsyncTask<Void, Void, Void> {
        private HomeListViewItem item;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }


        @Override
        protected Void doInBackground(java.lang.Void... voids) {
            try {
                String reply = replyEditText.getText().toString();

                String boardName = htmlPageUrl.split("=")[1].split("&")[0];
                String postNum = htmlPageUrl.split("=")[2].split("\\(")[0];
                Document doc = Jsoup.connect(htmlPageUrl).cookie("PHPSESSID", sessionId).get();
                Element names = doc.select("form[name=write]").first();


                Connection.Response replyForm = Jsoup.connect(htmlComment)
                        .cookie("PHPSESSID", sessionId)
                        .postDataCharset("euc-kr")
                        .data("id", boardName)
                        .data("no", postNum)
                        .data("memo", URLDecoder.decode(reply, "UTF-8"))
                        .referrer(htmlPageUrl)
                        .method(Connection.Method.POST)
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
        }
    }

    public class ReadMatchInfoTask extends AsyncTask<Void, Void, Void> {
        private String htmlTemp;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }


        @Override
        protected Void doInBackground(java.lang.Void... voids) {
            try {
                Document matchDoc = Jsoup.connect(htmlPageUrl).cookie("PHPSESSID", sessionId).get();
                Elements matchInfo = matchDoc.select("td[width=204]");

                for (Element ele : matchInfo ) {
                    htmlTemp += ele.html();
                }
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
            matchInfo.loadData(htmlTemp, "text/html", "UTF-8");

        }
    }
    /**************************************************************************************/


    @Override
    public void onClick(View view) {
        int id = view.getId();
        Intent it;

        switch(id) {
            case R.id.menuButton_content:
                slideMenu.toggleLeftDrawer();
                break;
            case R.id.matchButton_content:
                new MatchDialog(this, DataContainer.matchInfoHtml).show();
                break;
            case R.id.homeButton_menu:
                it = new Intent(this, HomeActivity.class);

                it.putExtra("url", "http://www.laromacorea.com/index1.html");
                it.putExtra("sessionId", sessionId);
                startActivity(it);
                //    finish();
                finish();
                break;
            case R.id.noticeButton_menu:
                it = new Intent(this, BoardActivity.class);

                it.putExtra("url", "http://www.laromacorea.com/bbs/zboard.php?id=notice");
                it.putExtra("sessionId", sessionId);
                it.putExtra("text", boardId);
                startActivity(it);
                //    finish();
                finish();
                break;
            case R.id.clubButton_menu:
                it = new Intent(this, BoardActivity.class);
                it.putExtra("url", "http://www.laromacorea.com/bbs/zboard.php?id=notice");
                it.putExtra("sessionId", sessionId);
                it.putExtra("text", boardId);
                startActivity(it);
                //   finish();
                finish();
                break;
            case R.id.squadButton_menu:
                it = new Intent(this, BoardActivity.class);
                it.putExtra("url", "http://www.laromacorea.com/bbs/zboard.php?id=squad");
                it.putExtra("sessionId", sessionId);
                it.putExtra("text", boardId);
                startActivity(it);
                //    finish();
                finish();
                break;
            case R.id.matchButton_menu:
                it = new Intent(this, BoardActivity.class);

                it.putExtra("url", "http://www.laromacorea.com/bbs/zboard.php?id=match");
                it.putExtra("sessionId", sessionId);
                it.putExtra("text", boardId);
                startActivity(it);
                //    finish();
                finish();
                break;
            case R.id.calcioButton_menu:
                it = new Intent(this, BoardActivity.class);

                it.putExtra("url", "http://www.laromacorea.com/bbs/zboard.php?id=calcio");
                it.putExtra("sessionId", sessionId);
                it.putExtra("text", boardId);
                startActivity(it);
                //    finish();
                finish();
                break;
            case R.id.freeButton_menu:
                it = new Intent(this, BoardActivity.class);

                it.putExtra("url", "http://www.laromacorea.com/bbs/zboard.php?id=free2");
                it.putExtra("sessionId", sessionId);
                it.putExtra("text", boardId);
                startActivity(it);
                // finish();
                finish();
                break;
            case R.id.specialButton_menu:
                it = new Intent(this, BoardActivity.class);

                it.putExtra("url", "http://www.laromacorea.com/bbs/zboard.php?id=sp");
                it.putExtra("sessionId", sessionId);
                it.putExtra("text", boardId);
                startActivity(it);
                //    finish();
                finish();
                break;
            case R.id.mediaButton_menu:
                it = new Intent(this, BoardActivity.class);

                it.putExtra("url", "http://www.laromacorea.com/bbs/zboard.php?id=media");
                it.putExtra("sessionId", sessionId);
                it.putExtra("text", boardId);
                startActivity(it);
                //     finish();
                finish();
                break;
            case R.id.iconButton_menu:
                it = new Intent(this, BoardActivity.class);
                it.putExtra("sessionId", sessionId);
                it.putExtra("text", boardId);
                startActivity(it);
                //   finish();
                finish();
                break;

            case R.id.replyButton:
                addReply = new AddReplyTask();
                addReply.execute();

                readReply = new ReadReplyTask();
                readReply.clear();
                readReply.execute();
                //    readReply.execute();
                listViewAdapter.notifyDataSetChanged();
                imm.hideSoftInputFromWindow(replyEditText.getWindowToken(), 0);
                replyEditText.setText("");
                break;
        }
    }
    /*********************************************************************************************/
    public int getWidth() {
        DisplayMetrics dm = getApplicationContext().getResources().getDisplayMetrics();
        int width = dm.widthPixels;

        return width;
    }

    public int getHeight() {
        DisplayMetrics dm = getApplicationContext().getResources().getDisplayMetrics();
        int height = dm.heightPixels;

        return height;
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
