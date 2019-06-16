package com.keehl.laromacorea;

/**
 * Created by user on 2016-12-26.
 */

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.navdrawer.SimpleSideDrawer;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.ArrayList;

public class PageFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemClickListener {
    private SimpleSideDrawer slideMenu;
    private SimpleSideDrawer slideMatch;
    private static String htmlPageUrl;
    private String title;
    private String sessionId;
    private String userId;
    private String content;
    private TextView titleTv;
    private WebView contentTv;
    private TextView writerTv;
    private ListView commentListView;
    private HomeListViewAdapter listViewAdapter;

    private LinearLayout container;
    private Button commentButton;
    private EditText commentEditText;
    private WebView matchInfo;
    private LinearLayout linearLayout;
    private ScrollView scrollView;
    private ScrollView scrollView2;
    private InputMethodManager imm;
    private ArrayList<HomeListViewItem> commentList;

    private ReadContentTask readContent;
    private ReadCommentTask readComment;
    private AddCommentTask addComment;
    private DeleteCommentTask deleteComment;
    private DeletePostTask deletePost;

    private ReadMatchInfoTask readMatchInfo;

    private String boardId;
    private int mPageNumber;
    private ContentActivity contentActivity;

    private boolean isReaded = false;


    private Button modifyButton;
    private Button deleteButton;

    private boolean isVisible;
    private boolean isStarted;

    private GestureDetector gestureScanner;

    public static PageFragment create(int pageNumber) {
        PageFragment fragment = new PageFragment();
        Bundle args = new Bundle();
        args.putInt("page", pageNumber);
        fragment.setArguments(args);
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


    //    gestureScanner = new GestureDetector(getActivity(), mGestureListener );
        init(viewGroup);

        clearPage();
        return viewGroup;
    }

    public void init(View view) {

        scrollView = (ScrollView)view.findViewById(R.id.scrollView);

        linearLayout = (LinearLayout)view.findViewById(R.id.contentView);

        titleTv = (TextView)view.findViewById(R.id.contentTitle);
        contentTv = (WebView)view.findViewById(R.id.content);
        commentButton = (Button)view.findViewById(R.id.commentButton);
        commentButton.setOnClickListener(this);
        commentEditText = (EditText)view.findViewById(R.id.commentEditText);
        //     matchInfo.getSettings().setJavaScriptEnabled(true);

// 스크롤바 없애기
        contentTv.setHorizontalScrollBarEnabled(true);
        contentTv.setVerticalScrollBarEnabled(true);
        contentTv.setBackgroundColor(0);


        contentTv.getSettings().setJavaScriptEnabled(true);
        contentTv.addJavascriptInterface(new MyJavaScriptInterface(), "HtmlViewer");
        contentTv.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);

                view.loadUrl("javascript:window.Android.getHtml(document.getElementsByTagName('html')[0].innerHTML);"); //<html></html> 사이에 있는 모든 html을 넘겨준다.
            }
        });

        contentTv.getSettings().setJavaScriptEnabled(true); //Javascript를 사용하도록 설정
        contentTv.addJavascriptInterface(new MyJavaScriptInterface(), "Android");


        writerTv = (TextView)view.findViewById(R.id.writer);


        listViewAdapter = new HomeListViewAdapter();
        listViewAdapter.setMode(HomeListViewAdapter.REPLY_MODE);
        commentListView = (ListView)view.findViewById(R.id.replyList);
        commentListView.setAdapter(listViewAdapter);
        commentListView.setOnItemClickListener(this);
        commentList = new ArrayList<HomeListViewItem>();

        commentEditText = (EditText)view.findViewById(R.id.commentEditText);

        modifyButton = view.findViewById(R.id.modifyButton);
        modifyButton.setOnClickListener(this);
        deleteButton = view.findViewById(R.id.deleteButton);
        deleteButton.setOnClickListener(this);
    //    getListViewSize(commentListView);
    }

    public void clearPage() {
        if (commentList != null) commentList.clear();
        if (readComment != null) readComment.clear();
        if (titleTv != null) titleTv.setText("");
        if (writerTv != null) writerTv.setText("");
        if (contentTv != null) contentTv.loadData("", "text/html", "UTF-8");
    }
    public void loadPage() {
        readContent = new ReadContentTask();
        readContent.execute();
        readComment = new ReadCommentTask();
        readComment.execute();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        this.isVisible = isVisibleToUser;

        if ((isVisible) && (isStarted)) {
            clearPage();
            loadPage();
        }
    }
    @Override
    public void onStart() {
        super.onStart();
        this.isStarted = true;

        if (isVisible) {
            clearPage();
            loadPage();
        }
    }
    public void onStop() {
        super.onStop();
        this.isStarted = false;
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
        private ProgressDialog pDialog;
        private String url;
        private String cookie;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            activity = (ContentActivity)getActivity();
            pDialog = new ProgressDialog(activity);
            pDialog.show();

            url = activity.getHtmlPageUrl();
            cookie = UserInfo.cookie;
        }


        @Override
        protected Void doInBackground(Void... voids) {

            try {
                if (url != null && cookie != null) {
                    Document matchDoc;

                    Elements notFound;
                    do {
                        url = activity.getHtmlPageUrl();
                        cookie = sessionId;
                        matchDoc = Jsoup.connect(url).cookie("PHPSESSID", sessionId)
                                .userAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/33.0.1750.152 Safari/537.36")
                                .timeout(5000).get();

                        notFound = matchDoc.select("table[width=974]").select("tbody")
                                .select("tr").select("td[width=640]").select("div[align=center]")
                                .select("form").select("table[width=300]").select("tbody").select("tr")
                                .select("td[align=center]");


                        String comments = notFound.text();


                        if (notFound.size() == 0
                                || activity.getCurrentPostNum() >= activity.getLastPostNum(boardId)
                                || activity.getCurrentPostNum() <= 0) {
                            UserInfo.currPostNum = "" + activity.getCurrentPostNum();
                            break;
                        } else {

                            if (comments.contains("권한이 없습니다")) {
                                Intent it = new Intent(getActivity(), AutoLoginActivity.class);
                                startActivity(it);
                                getActivity().finish();
                                break;
                            }


                            int direction = activity.getDirection();
                            if (direction == ContentActivity.LEFT) {
                                activity.moveLeft();
                            } else if (direction == ContentActivity.RIGHT) {
                                activity.moveRight();
                            }
                        }
                    } while(notFound.size() != 0);




                    if (boardId.equals("notice") || boardId.equals("Notice")) {

                        Elements totalElement = matchDoc.select("table[width=974]").select("tbody")
                                .select("tr").select("td[width=640]").select("div[align=center]").select("table[cellpadding=0]")
                                .select("tbody").select("tr").select("td");

                        Elements titleElements = totalElement.select("table[cellpadding=0]").select("tbody")
                                .select("tr").select("td[height=30]").select("table[cellpadding=0]").select("tbody").select("tr")
                                .select("td[style=word-break:break-all;]").select("b");
                        title = titleElements.text();

                        Elements writerElements = totalElement.select("table[cellpadding=0]").select("tbody")
                                .select("tr").select("td[height=30]").select("table[cellpadding=0]").select("tbody").select("tr")
                                .select("td[height=20]").select("a[href]");
                        writer = writerElements.text();

                        if (writer == null || writer.equals("")) writer = userId;

                        Elements contentElements = totalElement.select("table[border=0]").select("tbody").select("tr")
                                .select("td[valign=top]");

                        if (contentElements.size() >= 1) content = contentElements.get(0).html();
                        else content = contentElements.html();
                    } else {
                        Elements totalElement = matchDoc.select("table[width=974]").select("tbody")
                                .select("tr").select("td[width=640]").select("div[align=center]").select("table[cellpadding=0]")
                                .select("tbody").select("tr").select("td");

                        Elements titleElements = totalElement.select("table[cellpadding=0]").select("tbody")
                                .select("tr").select("td[height=30]").select("table[cellpadding=0]").select("tbody").select("tr")
                                .select("td[style=word-break:break-all;]").select("b");
                        title = titleElements.text();

                        Elements writerElements = totalElement.select("table[cellpadding=0]").select("tbody")
                                .select("tr").select("td[height=30]").select("table[cellpadding=0]").select("tbody").select("tr")
                                .select("td[height=20]").select("span");
                        writer = writerElements.text();

                        if (writer == null || writer.equals("")) writer = userId;

                        Elements contentElements = totalElement.select("table[border=0]").select("tbody").select("tr")
                                .select("td[valign=top]");

                        if (contentElements.size() >= 1) content = contentElements.get(0).html();
                        else content = contentElements.html();
                    }

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

            pDialog.cancel();
            if (writer == null || content == null) {
                Intent it = new Intent(getActivity(), AutoLoginActivity.class);
                startActivity(it);
                getActivity().finish();
            } else {
                writerTv.setText(writer + "님의 글 입니다.");
            //    contentTv.loadData(content, "text/html", "UTF-8");
                contentTv.loadDataWithBaseURL(null, "<style>img{display: inline;height: auto;max-width: 100%;} iframe{display: inline;height: auto;max-width: 100%;}</style>" + content, "text/html", "UTF-8", null);
                writer = "";
                content = "";
            }

        }
    }


    public class ReadCommentTask extends AsyncTask<Void, Void, Void> {
        private Document doc;
        private ContentActivity activity;
        private ProgressDialog pDialog;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            activity = (ContentActivity)getActivity();
            pDialog = new ProgressDialog(activity);
            pDialog.show();
            //   replyListView.setAdapter(listViewAdapter);
        }

        protected void clear() {
            commentList.clear();
            listViewAdapter.getArrayList().clear();
            listViewAdapter.notifyDataSetChanged();
            commentListView.setAdapter(null);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            //clear();
            try{

                String url = activity.getHtmlPageUrl();
                String cookie = sessionId;
                cookie = UserInfo.cookie;

                if (boardId.equals("notice") || boardId.equals("Notice")) {

                    Document doc = Jsoup.connect(url).cookie("PHPSESSID", cookie)
                            .userAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/33.0.1750.152 Safari/537.36").timeout(5000).get();

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


                        String iconUrl = comment.select("td[valign=top]").select("img").attr("src");
                        String userId = comment.select("td[valign=top]").select("b").text();
                        String contents = comment.select("td").select("td[style=word-break:break-all; padding:7 7 7 0;]").text();

                        ContentsData contentsData = new ContentsData();
                        contentsData.str = contents;
                        contentsData.id = userId;

                        item.setUserId(userId);
                        item.setImageUrl("http://www.laromacorea.com/bbs/" + iconUrl);
                        item.setContents(contentsData);

                        commentList.add(item);
                        i++;
                    }
                } else {
                    Document doc = Jsoup.connect(url).cookie("PHPSESSID", cookie)
                            .userAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/33.0.1750.152 Safari/537.36").timeout(5000).get();

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

                        commentList.add(item);
                        i++;
                    }
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
            listViewAdapter.getArrayList().clear();
            commentListView.setAdapter(null);
            commentListView.setAdapter(listViewAdapter);
            int size = commentList.size();
            for (int i = 0; i < size; i++) {
                listViewAdapter.addItem(
                        null,
                        commentList.get(i).getTitle(),
                        commentList.get(i).getContents(),
                        commentList.get(i).getUserId(),
                        commentList.get(i).getDate(),
                        null,
                        commentList.get(i).getImageUrl(),
                        commentList.get(i).getIsNotice()
                );
            }
            //    replyList.clear();
            listViewAdapter.notifyDataSetChanged();
            commentListView.setFocusable(true);

            pDialog.cancel();
        }
    }


    public class AddCommentTask extends AsyncTask<Void, Void, Void> {
        private HomeListViewItem item;
        private String htmlComment = "http://www.laromacorea.com/bbs/comment_ok.php";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }


        @Override
        protected Void doInBackground(Void... voids) {
            try {
                String comment = commentEditText.getText().toString();

                comment += "\n";
                comment += "-from laromacorea mobile-";

                String boardName = ((ContentActivity)getActivity()).getBoardName(boardId);
                int currPostNum = ((ContentActivity)getActivity()).getCurrentPostNum();

                Jsoup.connect(htmlComment)
                        .cookie("PHPSESSID", sessionId)
                        .postDataCharset("euc-kr")
                        .data("id", boardName)
                        .data("no", "" + currPostNum)
                        .data("memo", URLDecoder.decode(comment, "UTF-8"))
                        .referrer(htmlPageUrl)
                        .method(Connection.Method.POST)
                        .timeout(5000)
                        .execute();


            }catch(IOException ie) {
                Utils.showToast(getActivity(), ie.getMessage(), Toast.LENGTH_LONG);
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

    public class DeleteCommentTask extends AsyncTask<Void, Void, Void> {
        private HomeListViewItem item;
        private String htmlDeleteComment = "http://www.laromacorea.com/bbs/del_comment_ok.php";
        private int num;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        public void setNum(int num) {
            this.num = num;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                String boardName = ((ContentActivity)getActivity()).getBoardName(boardId);
                int currPostNum = ((ContentActivity)getActivity()).getCurrentPostNum();


                String param = Jsoup.connect(htmlPageUrl).cookie("PHPSESSID", sessionId).get().select("table[width=95%]")
                        .select("tbody").select("tr").select("td").select("table[width=100%]").select("tbody").select("tr")
                        .select("td").select("table[border=0]").select("tbody").select("tr").select("td[style]")
                        .select("a[onfocus=blur()]").get(num).attr("href").split("c_no=")[1];

                Jsoup.connect(htmlDeleteComment)
                        .cookie("PHPSESSID", sessionId)
                        .postDataCharset("euc-kr")
                        .data("id", boardName)
                        .data("no", "" + currPostNum)
                        .data("c_no", param)
                        .userAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/33.0.1750.152 Safari/537.36")
                        .referrer(htmlPageUrl)
                        .method(Connection.Method.POST)
                        .timeout(5000)
                        .execute();


            }catch(IOException ie) {
                Utils.showToast(getActivity(), ie.getMessage(), Toast.LENGTH_LONG);
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
            Utils.showToast(getActivity(), "댓글을 삭제합니다.", Toast.LENGTH_LONG);

            readComment.clear();
            clearPage();
            loadPage();
        }
    }

    public class DeletePostTask extends AsyncTask<Void, Void, Void> {
        private String deletePostUrl = "http://www.laromacorea.com/bbs/delete_ok.php?";
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {

                Jsoup.connect(deletePostUrl).cookie("PHPSESSID", sessionId)
                        .postDataCharset("euc-kr")
                        .data("id", ((ContentActivity)getActivity()).getBoardName(boardId))
                        .data("no", "" + ((ContentActivity)getActivity()).getCurrentPostNum())
                        .userAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/33.0.1750.152 Safari/537.36")
                        .referrer(htmlPageUrl)
                        .timeout(5000)
                        .method(Connection.Method.POST).execute();
            } catch (IOException ioex) {

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

            Intent it = new Intent(getActivity(), BoardActivity.class);
            String url = "http://www.laromacorea.com/bbs/zboard.php?id=" + ((ContentActivity)getActivity()).getBoardName(boardId);
            it.putExtra("url", url);
            it.putExtra("sessionId", sessionId);
            it.putExtra("text", boardId);
            startActivity(it);
            getActivity().finish();
        }
    }

    public class ReadMatchInfoTask extends AsyncTask<Void, Void, Void> {
        private String htmlTemp;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }


        @Override
        protected Void doInBackground(Void... voids) {
            try {
                Document matchDoc = Jsoup.connect(htmlPageUrl).cookie("PHPSESSID", sessionId)
                        .timeout(5000)
                        .userAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/33.0.1750.152 Safari/537.36")
                        .get();
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
            case R.id.commentButton:

                if (!commentEditText.getText().toString().equals("")) {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());

                    dialog.setTitle("알림");
                    dialog.setMessage("댓글을 남기시겠습니까?");
                    dialog.setCancelable(false);
                    dialog.setPositiveButton("예", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            addComment = new AddCommentTask();
                            addComment.execute();

                            readComment = new ReadCommentTask();
                            readComment.clear();
                            readComment.execute();
                            //    readReply.execute();
                            listViewAdapter.notifyDataSetChanged();
//                imm.hideSoftInputFromWindow(replyEditText.getWindowToken(), 0);
                            commentEditText.setText("");
                        }
                    });
                    dialog.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });

                    dialog.show();
                } else {
                    Utils.showToast(getActivity(), "댓글 내용을 입력해주세요.", Toast.LENGTH_LONG);
                }


                break;

            case R.id.modifyButton:
                Intent it = new Intent(getActivity(), ModifyActivity.class);
                String text = sessionId;
                String title = titleTv.getText().toString();

                it.putExtra("url", htmlPageUrl);
                it.putExtra("sessionId", sessionId);
                it.putExtra("boardId", boardId);
                it.putExtra("text", title);
                it.putExtra("content", content);
                it.putExtra("id", userId);
                startActivity(it);
                break;
            case R.id.deleteButton:
                String writer = writerTv.getText().toString();
                writer = writer.split("님의 글")[0];
                if (writer != null && writer.equals(UserInfo.userId)) {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());

                    dialog.setTitle("알림");
                    dialog.setMessage("글을 삭제하시겠습니까?");
                    dialog.setCancelable(false);
                    dialog.setPositiveButton("예", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Utils.showToast(getActivity(), "글을 삭제합니다.", Toast.LENGTH_LONG);
                            deletePost = new DeletePostTask();
                            deletePost.execute();
                        }
                    });
                    dialog.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //    updateCheck = true;
                        }
                    });
                    dialog.show();

                    //

                } else {
                    Utils.showToast(getActivity(), "본인 글이 아닙니다.", Toast.LENGTH_LONG);
                }
                break;
        }
    }

    int i = 0;
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long l_position) {
        final int pos = position;
        i++;
        if (i >= 2) {
            String userId = commentList.get(position).getContents().id;
            if (userId.equals(UserInfo.userId)) {

                AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());

                dialog.setTitle("알림");
                dialog.setMessage("댓글을 삭제하시겠습니까?");
                dialog.setPositiveButton("예", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        DeleteCommentTask deleteCommentTask = new DeleteCommentTask();
                        deleteCommentTask.setNum(pos);
                        deleteCommentTask.execute();
                        Utils.showToast(getActivity(), "댓글을 삭제합니다.", Toast.LENGTH_LONG);
                    }
                });
                dialog.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Utils.showToast(getActivity(), "댓글을 삭제를 취소합니다.", Toast.LENGTH_LONG);
                    }
                });

                dialog.show();
                return;
            } else {
                Utils.showToast(getActivity(), "본인 글이 아닙니다.", Toast.LENGTH_LONG);
                return;
            }
        } else {
            Utils.showToast(getActivity(), "빠르게 두번 터치하면 댓글을 삭제합니다.", Toast.LENGTH_LONG);
        }

        new Thread(new Runnable() {
            public void run() {
                try {
                    Thread.sleep(3000);
                    i = 0;
                } catch(Exception ex) {
                    Log.d("Runnable", ex.getMessage());
                }

            }
        }).start();
        return;
    }

    public class MyJavaScriptInterface {

        public MyJavaScriptInterface() {}
        @JavascriptInterface
        public void getHtml(String html) { //위 자바스크립트가 호출되면 여기로 html이 반환됨
            content = html;
            content = content.replaceAll("<head>", "").replaceAll("</head>", "")
                    .replaceAll("<body>", "").replaceAll("</body>", "")
                    .replaceAll("<br>", "\n").replaceAll("<!--", "")
            .replaceAll("-->", "").replaceAll("<-->", "")
                    .replaceAll("\"<", "");
        }
    }

    GestureDetector.OnGestureListener mGestureListener = new GestureDetector.OnGestureListener() {
        /* 아래의 모든 이벤트 핸들러들은 OnGestureListner에 포함되어 있는 메소드 */
        public boolean onDown(MotionEvent event) {
            Log.i("제스쳐 이벤트", "onDown()");
            return true;
        } /* 스크린 터치후에 떼지않고 이동하는 도중일 때 * e1:처음 다운 위치, e2:현재이동 중인 위치 * 거리:앞의 이동위치와 현재의 이동위치 간의 거리*/

        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            Log.i("제스쳐 이벤트", "onScroll()");
            return true;
        } /* 스크린 터치후에 떼지 않고 이동하여 떼었을 때 */
        public boolean onFling(MotionEvent e1, MotionEvent e2, float speedX, float speedY) {
            Log.i("제스쳐 이벤트", "onFling()");
            return true;
        }
        public void onLongPress(MotionEvent event) {
            clearPage();
            loadPage();
        }
        public void onShowPress(MotionEvent event) {

        }
        public boolean onSingleTapUp(MotionEvent event) {
            return true;
        }
    };

}