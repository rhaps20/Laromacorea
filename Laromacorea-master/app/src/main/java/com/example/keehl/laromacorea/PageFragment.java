package com.example.keehl.laromacorea;

/**
 * Created by user on 2016-12-26.
 */

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
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

public class PageFragment extends Fragment implements View.OnClickListener {
    private SimpleSideDrawer slideMenu;
    private SimpleSideDrawer slideMatch;
    private String htmlComment = "http://www.laromacorea.com/bbs/comment_ok.php";
    private static String htmlPageUrl;
    private String title;
    private String sessionId;
    private String userId;
    private TextView titleTv;
    private WebView contentTv;
    private TextView writerTv;
    private ListView replyListView;
    private HomeListViewAdapter listViewAdapter;

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
    private int mPageNumber;
    private ContentActivity contentActivity;


    public static PageFragment create(int pageNumber, String pageUrl) {
        PageFragment fragment = new PageFragment();
        Bundle args = new Bundle();
        args.putInt("page", pageNumber);
        fragment.setArguments(args);
        htmlPageUrl = pageUrl;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPageNumber = getArguments().getInt("page");
        contentActivity = (ContentActivity)getActivity();
        Bundle extra = getActivity().getIntent().getExtras();

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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View viewGroup = inflater.inflate(R.layout.content_page, container, false);

        init(viewGroup);

        loadPage();

        return viewGroup;
    }

    public void init(View view) {

        scrollView = (ScrollView)view.findViewById(R.id.scrollView);

        linearLayout = (LinearLayout)view.findViewById(R.id.contentView);
        titleTv = (TextView)view.findViewById(R.id.contentTitle);
        contentTv = (WebView)view.findViewById(R.id.content);
        replyButton = (Button)view.findViewById(R.id.replyButton);
        replyButton.setOnClickListener(this);
        replyEditText = (EditText)view.findViewById(R.id.replyEditText);
        //     matchInfo.getSettings().setJavaScriptEnabled(true);

// 스크롤바 없애기
        contentTv.setHorizontalScrollBarEnabled(true);
        contentTv.setVerticalScrollBarEnabled(true);
        contentTv.setBackgroundColor(0);

        writerTv = (TextView)view.findViewById(R.id.writer);

        titleTv.setText(title);


        listViewAdapter = new HomeListViewAdapter();
        listViewAdapter.setMode(HomeListViewAdapter.REPLY_MODE);
        replyListView = (ListView)view.findViewById(R.id.replyList);
        replyListView.setAdapter(listViewAdapter);
        replyList = new ArrayList<HomeListViewItem>();


        //   setListViewHeightBasedOnChildren(replyListView, scrollView2);
        //   replyListView.addHeaderView(scrollView2);
        getListViewSize(replyListView);
    }
    public void loadPage() {
        readContent = new ReadContentTask();
        readContent.execute();

        readReply = new ReadReplyTask();
        readReply.execute();
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
        private ContentActivity activity;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            activity = (ContentActivity)getActivity();
        }


        @Override
        protected Void doInBackground(java.lang.Void... voids) {

            try {
                String url = activity.getHtmlPageUrl();
                String cookie = sessionId;
                cookie = UserInfo.cookie;
                if (url != null && cookie != null) {
                    Document matchDoc = Jsoup.connect(url).cookie("PHPSESSID", cookie).get();

                    Element titleElement = matchDoc.select("table[width=974]").select("tbody")
                            .select("tr").select("td[width=640]").select("div[align=center]").select("table[cellpadding=0]")
                            .select("tbody").select("tr").select("td").select("table[cellpadding=0]").select("tbody")
                            .select("tr").select("td[height=30]").select("table[cellpadding=0]").select("tbody").select("tr")
                            .select("td[style=word-break:break-all;]").select("b").get(0);
                    Element writerElement = matchDoc.select("span[style]").get(4);
                    Element contentElement = matchDoc.select("td[valign]").get(3);

                    if (userId == null) writer = writerElement.text();
                    else writer = userId;
                    title = titleElement.text();
                    content = contentElement.html();
                } else {

                }

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
            titleTv.setText(title);
            writerTv.setText(writer + "님의 글 입니다.");
            contentTv.loadData(content, "text/html", "UTF-8");
        }
    }


    public class ReadReplyTask extends AsyncTask<Void, Void, Void> {
        private Document doc;
        private ContentActivity activity;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            activity = (ContentActivity)getActivity();
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

                String url = activity.getHtmlPageUrl();
                String cookie = sessionId;
                cookie = UserInfo.cookie;
                if (url != null && cookie != null) {

                    Document doc = Jsoup.connect(url).cookie("PHPSESSID", cookie).get();

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

                } else {

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

    @Override
    public void onClick(View view) {
        int id = view.getId();

        switch(id) {
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
}