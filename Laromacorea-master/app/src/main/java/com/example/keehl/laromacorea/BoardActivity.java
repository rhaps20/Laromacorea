package com.example.keehl.laromacorea;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;


import com.navdrawer.SimpleSideDrawer;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

public class BoardActivity extends Activity implements View.OnClickListener {
    private SimpleSideDrawer slideMenu;
    private SimpleSideDrawer slideMatch;
    private String htmlPageUrl;
    private String title;
    private String sessionId;
    private TextView titleTv;
    private TextView contentTv;
    private TextView writerTv;
    private MAsyncTask readData;

    private Button menuButton;
    private Button matchButton;

    private Button writePostButton;

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
    private WebView matchInfo;
    private HomeListViewAdapter listViewAdapter;
    private ListView boardListView;
    private View header;
    private View footer;
    private ArrayList<HomeListViewItem> arrayList;
    private ItemClickListener itemClickListener;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board);

        Bundle extra = getIntent().getExtras();

        if (savedInstanceState == null) {
            if (extra == null) {
                htmlPageUrl = null;
                title = null;
                sessionId = null;
            } else {
                htmlPageUrl = extra.getString("url");
                title = extra.getString("text");
                sessionId = extra.getString("sessionId");
            }
        } else {
            htmlPageUrl = savedInstanceState.getString("url");
            title = extra.getString("text");
            sessionId = extra.getString("sessionId");
        }

        init();


        sessionId = DataContainer.cookies;
        readData = new MAsyncTask();
        readData.execute();
    }
    public void init() {
        slideMenu = new SimpleSideDrawer(this);
        slideMenu.setLeftBehindContentView(R.layout.slide_menu);

        slideMatch = new SimpleSideDrawer(this);
        slideMatch.setRightBehindContentView(R.layout.slide_game);





        listViewAdapter = new HomeListViewAdapter();
        listViewAdapter.setMode(HomeListViewAdapter.REPLY_MODE);


        boardListView = (ListView)findViewById(R.id.boardList);

        header = getLayoutInflater().inflate(R.layout.activity_board_header, null, false);
        footer = getLayoutInflater().inflate(R.layout.activity_board_footer, null, false);

        menuButton = header.findViewById(R.id.menuButton_board);
        matchButton = header.findViewById(R.id.matchButton_board);
        menuButton.setOnClickListener(this);
        matchButton.setOnClickListener(this);


        titleTv = (TextView)header.findViewById(R.id.boardTitle1);
        titleTv.setText(title);

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
        writePostButton = (Button)footer.findViewById(R.id.writePost);
        writePostButton.setOnClickListener(this);

        boardListView.addHeaderView(header);
        boardListView.addFooterView(footer);

        boardListView.setAdapter(listViewAdapter);

        itemClickListener = new ItemClickListener();
        boardListView.setOnItemClickListener(itemClickListener);

        matchInfo = (WebView)findViewById(R.id.matchInfo);
    }

    public class MAsyncTask extends AsyncTask<Void, Void, Void> {
        private String htmlTemp;
        private String homeUrl =  "http://www.laromacorea.com/bbs/";
        private String imageUrl = "http://www.laromacorea.com/bbs/";
        private Document doc;
        private ProgressDialog pDialog;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            arrayList = new ArrayList<HomeListViewItem>();
            pDialog = new ProgressDialog(BoardActivity.this);
            pDialog.show();
        }

        @Override
        protected Void doInBackground(Void... integers) {
            try {
                doc = Jsoup.connect(htmlPageUrl).cookie("PHPSESSID", sessionId).get();
/********************************************************************/
                if (!title.equals("Notice")) {
                    Elements allPosts = doc.select("td[width=640]").select("div[align=center]")
                            .select("table[border=0]").select("tbody").select("tr").select("td").select("table[border=0]")
                            .select("tbody");

                    Elements noticesPost = allPosts.select("tr[height=30]");
                    Elements normalPost = allPosts.select("tr[height=28]");


                    for (Element post : noticesPost) {
                        String title = post.select("b").text();

                        String url = post.select("b").select("a").attr("href");
                        String id = post.select("span[onmousedown]").text();
                        String comment = post.select("span[class=comment_number]").text();
                        String time = post.select("td[class=ver7]").select("span[title]").text();

                        int size = post.select("img[src]").size();
                        if (size > 2) {
                            imageUrl = homeUrl
                                    + post.select("img[src]").get(2).attr("src");
                        } else {
                            imageUrl = "";
                        }

                        HomeListViewItem item = new HomeListViewItem();
                        ContentsData contents = new ContentsData();
                        contents.str = title;
                        contents.url = homeUrl + url;
                        contents.comments = comment;
                        contents.id = id;
                        item.setNotice(true);
                        item.setNumber("0");
                        item.setTitle(title);
                        item.setContents(contents);
                        item.setUserId(id);
                        item.setDate(time);

                        item.setImageUrl(imageUrl);
                        arrayList.add(item);
                    }

                    for (Element post : normalPost) {
                        String num = post.select("td[class=ver7]").text().split(" ")[0];
                        String title = post.select("a[href]").text();
                        String url = post.select("a").attr("href");
                        String id = post.select("span[onmousedown]").text();
                        String comment = post.select("span[class=comment_number]").text();
                        String time = post.select("td[class=ver7]").select("span[title]").text();
                        String views = post.select("td[class=ver7]").text().split(" ")[2];
                        /*
                        String imageUrl;
                        try {
                            imageUrl = homeUrl
                                    + post.select("img[src]").get(1).attr("src");
                        } catch(ArrayIndexOutOfBoundsException ai) {
                            imageUrl = "";
                        }

                         */
                        int size = post.select("img[src]").size();
                        if (size > 1) {
                            imageUrl = homeUrl
                                    + post.select("img[src]").get(1).attr("src");
                        } else {
                            imageUrl = "";
                        }

                        HomeListViewItem item = new HomeListViewItem();
                        ContentsData contents = new ContentsData();
                        contents.str = title;
                        contents.url = homeUrl + url;
                        contents.comments = comment;
                        contents.id = id;
                        item.setNotice(false);
                        item.setNumber(num);
                        item.setTitle(title);
                        item.setContents(contents);
                        item.setUserId(id);
                        item.setDate(time);
                        item.setViews(views);

                        item.setImageUrl(imageUrl);
                        arrayList.add(item);
                    }
                } else {

                    Elements allPosts = doc.select("td[width=640]").select("div[align=center]")
                            .select("table[border=0]").select("tbody").select("tr").select("td").select("table[border=0]")
                            .select("tbody");

                    Elements noticesPost = allPosts.select("tr[height=30]");
                    Elements normalPost = allPosts.select("tr[height=28]");

                    for (Element post : noticesPost) {
                        String title = noticesPost.select("td[align=left]").select("b").text();
                        String url = noticesPost.select("td[align=left]").select("b").select("a").attr("href");
                        String comments = noticesPost.select("td[align=left]").select("b").get(0).nextElementSibling().text();
                        String id = noticesPost.select("td[nowrap]").select("a[href]").text();
                        String time = noticesPost.select("td[nowrap][class=ver7]").select("span[title]").get(0).text();
                        /*
                        String imageUrl;
                        try {
                            imageUrl = homeUrl + post.select("td[style=word-break:break-all;]").select("a[href]").get(1).previousElementSibling().attr("src");

                        }catch(ArrayIndexOutOfBoundsException ai) {
                            imageUrl = "";
                        }

                         */
                        int size = post.select("td[style=word-break:break-all;]").select("a[href]").size();

                        if (size > 1) {
                            imageUrl = homeUrl + post.select("td[style=word-break:break-all;]").select("a[href]").get(1).previousElementSibling().attr("src");
                        } else {
                            imageUrl = "";
                        }

                        HomeListViewItem item = new HomeListViewItem();
                        ContentsData contents = new ContentsData();
                        contents.str = title;
                        contents.comments = comments;
                        contents.url = homeUrl + url;
                        contents.id = id;
                        item.setNotice(true);
                        item.setNumber("0");
                        item.setTitle(title);
                        item.setContents(contents);
                        item.setUserId(id);
                        item.setDate(time);
                        item.setImageUrl(imageUrl);
                        arrayList.add(item);
                    }

                    for (Element post : normalPost) {
                        String num = post.select("td[class=ver7]").text().split(" ")[0];
                        String title = post.select("td[align=left]").select("a[href]").text();
                        String url = post.select("td[align=left]").select("a").attr("href");
                        String id = post.select("td[style=word-break:break-all;]").select("a[href]").get(1).text();

                        /*
                        String imageUrl;
                        try {
                            imageUrl = homeUrl + post.select("td[style=word-break:break-all;]").select("a[href]").get(1).previousElementSibling().attr("src");

                        }catch(ArrayIndexOutOfBoundsException ai) {
                            imageUrl = "";
                        }
                         */

                        int size = post.select("td[style=word-break:break-all;]").select("a[href]").size();

                        if (size > 1) {
                            imageUrl = homeUrl + post.select("td[style=word-break:break-all;]").select("a[href]").get(1).previousElementSibling().attr("src");
                        } else {
                            imageUrl = "";
                        }

                        String comment = post.select("span[class=comment_number]").text();
                        String time = post.select("td[class=ver7]").select("span[title]").text();
                        String views = post.select("td[class=ver7]").text().split(" ")[2];

                        HomeListViewItem item = new HomeListViewItem();
                        ContentsData contents = new ContentsData();
                        contents.str = title;
                        contents.url = homeUrl + url;
                        contents.id = id;
                        contents.comments = comment;

                        item.setNotice(false);
                        item.setNumber(num);
                        item.setTitle(title);
                        item.setContents(contents);
                        item.setUserId(id);
                        item.setDate(time);
                        item.setViews(views);

                        item.setImageUrl(imageUrl);
                        arrayList.add(item);
                    }
                }

                arrayList.remove(0);
            }catch (Exception io) {
                Log.d("BoardActivity_MAsyncTask", io.getMessage());
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
            //   titleTv.setText(doc.title());
            //    contentTv.setText(temp);
            listViewAdapter.setMode(HomeListViewAdapter.BOARD_MODE);
            for (int i = 0;  i < arrayList.size(); i++) {

                listViewAdapter.addItem(
                        arrayList.get(i).getNumber(),
                        arrayList.get(i).getTitle(),
                        arrayList.get(i).getContents(),
                        arrayList.get(i).getUserId(),
                        arrayList.get(i).getDate(),
                        arrayList.get(i).getViews(),
                        arrayList.get(i).getImageUrl(),
                        arrayList.get(i).getIsNotice()
                );

                listViewAdapter.notifyDataSetChanged();
            }
            matchInfo.loadData(htmlTemp, "text/html", "UTF-8");
        }


    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        Intent it;

        switch(id) {
            case R.id.menuButton_board:
                slideMenu.toggleLeftDrawer();
                break;
            case R.id.matchButton_board:
                new MatchDialog(this, DataContainer.matchInfoHtml).show();
                break;
            case R.id.homeButton_menu:
                it = new Intent(this, HomeActivity.class);

                it.putExtra("url", "http://www.laromacorea.com/index1.html");
                it.putExtra("sessionId", sessionId);
                startActivity(it);
                finish();
                break;
            case R.id.noticeButton_menu:
                it = new Intent(this, BoardActivity.class);

                it.putExtra("url", "http://www.laromacorea.com/bbs/zboard.php?id=notice");
                it.putExtra("sessionId", sessionId);
                it.putExtra("text", "Notice");
                startActivity(it);
                finish();
                break;
            case R.id.clubButton_menu:
                it = new Intent(this, BoardActivity.class);
                it.putExtra("url", "http://www.laromacorea.com/bbs/zboard.php?id=club");
                it.putExtra("sessionId", sessionId);
                it.putExtra("text", "Club");
                startActivity(it);
                finish();
                break;
            case R.id.squadButton_menu:
                it = new Intent(this, BoardActivity.class);
                it.putExtra("url", "http://www.laromacorea.com/bbs/zboard.php?id=squad");
                it.putExtra("sessionId", sessionId);
                it.putExtra("text", "Squad");
                startActivity(it);
                finish();
                break;
            case R.id.matchButton_menu:
                it = new Intent(this, BoardActivity.class);

                it.putExtra("url", "http://www.laromacorea.com/bbs/zboard.php?id=1213match");
                it.putExtra("sessionId", sessionId);
                it.putExtra("text", "Match");
                startActivity(it);
                finish();
                break;
            case R.id.calcioButton_menu:
                it = new Intent(this, BoardActivity.class);

                it.putExtra("url", "http://www.laromacorea.com/bbs/zboard.php?id=calcio");
                it.putExtra("sessionId", sessionId);
                it.putExtra("text", "Calcio");
                startActivity(it);
                finish();
                break;
            case R.id.freeButton_menu:
                it = new Intent(this, BoardActivity.class);

                it.putExtra("url", "http://www.laromacorea.com/bbs/zboard.php?id=free2");
                it.putExtra("sessionId", sessionId);
                it.putExtra("text", "Free");
                startActivity(it);
                // finish();
                break;
            case R.id.specialButton_menu:
                it = new Intent(this, BoardActivity.class);

                it.putExtra("url", "http://www.laromacorea.com/bbs/zboard.php?id=sp");
                it.putExtra("sessionId", sessionId);
                it.putExtra("text", "Special");
                startActivity(it);
                finish();
                break;
            case R.id.mediaButton_menu:
                it = new Intent(this, BoardActivity.class);

                it.putExtra("url", "http://www.laromacorea.com/bbs/zboard.php?id=media");
                it.putExtra("sessionId", sessionId);
                it.putExtra("text", "Media");
                startActivity(it);
                finish();
                break;
            case R.id.iconButton_menu:
                it = new Intent(this, BoardActivity.class);
                it.putExtra("sessionId", sessionId);
                it.putExtra("text", "Icon");
                startActivity(it);
                finish();
                break;
            case R.id.writePost:
                it = new Intent(this, WriteActivity.class);
                startActivity(it);
                finish();

        }
    }

    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = 0;
        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.AT_MOST);

        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }

    public class ItemClickListener implements AdapterView.OnItemClickListener {
        private String str;

        public void onItemClick(AdapterView<?> parentView, View clickedView, int position, long id) {
            Intent it = new Intent(BoardActivity.this, ContentActivity.class);

            position -= 1;
            String text = arrayList.get(position).getContents().str;
            String url = arrayList.get(position).getContents().url;
            String userId = arrayList.get(position).getContents().id;

            it.putExtra("text", text);
            it.putExtra("url", url);
            it.putExtra("id", userId);
            it.putExtra("sessionId", sessionId);
            it.putExtra("boardId", title);
            startActivity(it);
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
