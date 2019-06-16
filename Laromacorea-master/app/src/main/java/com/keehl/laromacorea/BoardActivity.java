package com.keehl.laromacorea;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
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

import java.util.ArrayList;

public class BoardActivity extends Activity implements View.OnClickListener {
    private SimpleSideDrawer slideMenu;
    private SimpleSideDrawer slideMatch;
    private String htmlPageUrl;
    private String title;
    private String sessionId;
    private String boardId;
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
    private Button matchButton_menu;
    private Button calcioButton_menu;
    private Button freeButton_menu;
    private Button specialButton_menu;
    private Button mediaButton_menu;

    private Button leagueTableButton;
    private Button personalTableButton;
    private Button nextMatchButton;

    private TextView page1;
    private TextView page2;
    private TextView page3;
    private TextView page4;
    private TextView page5;
    private TextView page6;
    private TextView page7;
    private TextView page8;
    private TextView page9;
    private TextView page10;

    private TextView prevTv;
    private TextView nextTv;

    private int currPage;

    private int pageOfPage = 0;
    private int maxPage = 100;

    private WebView matchInfo;
    private HomeListViewAdapter listViewAdapter;
    private ListView boardListView;
    private View header;
    private View footer;
    private ArrayList<HomeListViewItem> arrayList;
    private ItemClickListener itemClickListener;

    private String boardUrl = "http://www.laromacorea.com/bbs/zboard.php?";
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
                currPage = 0;
                boardId = null;
            } else {
                htmlPageUrl = extra.getString("url");
                title = extra.getString("text");
                sessionId = extra.getString("sessionId");
                currPage = extra.getInt("page");
                boardId = extra.getString("boardId");
            }
        } else {
            htmlPageUrl = savedInstanceState.getString("url");
            title = extra.getString("text");
            sessionId = extra.getString("sessionId");
            currPage = extra.getInt("page");
            boardId = extra.getString("boardId");
        }

        init();
        initPageButton();

        htmlPageUrl = boardUrl + "id=" + getBoardName(title) + "&page=" + currPage;

        sessionId = DataContainer.cookies;
        readData = new MAsyncTask();
        readData.execute();
    }
    public void init() {
        slideMenu = new SimpleSideDrawer(this);
        slideMenu.setLeftBehindContentView(R.layout.slide_menu);

        slideMatch = new SimpleSideDrawer(this);
        slideMatch.setRightBehindContentView(R.layout.slide_game);


        leagueTableButton = findViewById(R.id.leagueTable);
        leagueTableButton.setOnClickListener(this);

        personalTableButton = findViewById(R.id.personalTable);
        personalTableButton.setOnClickListener(this);

        nextMatchButton = findViewById(R.id.nextMatch);
        nextMatchButton.setOnClickListener(this);

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
        titleTv.setText(getBoardTitle(title));

        homeButton_menu = (Button)findViewById(R.id.homeButton_menu);
        homeButton_menu.setOnClickListener(this);
        noticeButton_menu = (Button)findViewById(R.id.noticeButton_menu);
        noticeButton_menu.setOnClickListener(this);
        squadButton_menu= (Button)findViewById(R.id.squadButton_menu);
        squadButton_menu.setOnClickListener(this);
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
        writePostButton = (Button)footer.findViewById(R.id.writePost);
        writePostButton.setOnClickListener(this);

        boardListView.addHeaderView(header);
        boardListView.addFooterView(footer);

        boardListView.setAdapter(listViewAdapter);

        itemClickListener = new ItemClickListener();
        boardListView.setOnItemClickListener(itemClickListener);


        prevTv = findViewById(R.id.pagePrev);
        prevTv.setOnClickListener(this);

        nextTv = findViewById(R.id.pageNext);
        nextTv.setOnClickListener(this);

        page1 = findViewById(R.id.page1);
        page1.setOnClickListener(this);


        page2 = findViewById(R.id.page2);
        page2.setOnClickListener(this);


        page3 = findViewById(R.id.page3);
        page3.setOnClickListener(this);


        page4 = findViewById(R.id.page4);
        page4.setOnClickListener(this);


        page5 = findViewById(R.id.page5);
        page5.setOnClickListener(this);


        page6 = findViewById(R.id.page6);
        page6.setOnClickListener(this);


        page7 = findViewById(R.id.page7);
        page7.setOnClickListener(this);


        page8 = findViewById(R.id.page8);
        page8.setOnClickListener(this);


        page9 = findViewById(R.id.page9);
        page9.setOnClickListener(this);


        page10 = findViewById(R.id.page10);
        page10.setOnClickListener(this);

    //    matchInfo = (WebView)findViewById(R.id.matchInfo);
    }

    public class MAsyncTask extends AsyncTask<Void, Void, Void> {
        private String htmlTemp;
        private final String homeUrl =  "http://www.laromacorea.com/bbs/";
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
                doc = Jsoup.connect(htmlPageUrl).cookie("PHPSESSID", sessionId)
                        .timeout(5000)
                        .userAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/33.0.1750.152 Safari/537.36").get();
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

                        int size = post.select("td[style=word-break:break-all;]").select("a[href]").size();

                        if (size > 1) {
                            imageUrl = homeUrl + post.select("td[style=word-break:break-all;]").select("a[href]").get(1).previousElementSibling().attr("src");
                        } else {
                            imageUrl = null;
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

            if (arrayList.size() == 0) {
                pDialog.cancel();
                Intent it = new Intent(BoardActivity.this, AutoLoginActivity.class);
                startActivity(it);
                finish();
            } else {
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
                }
            //    arrayList.clear();
                listViewAdapter.notifyDataSetChanged();
                pDialog.cancel();
            }
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
                slideMatch.toggleRightDrawer();
                break;
            case R.id.leagueTable:
                new MatchDialog(this, DataContainer.leagueTableHtml).show();
                break;
            case R.id.personalTable:
                new PersonalTableDialog(this, DataContainer.playerInfoHtml).show();
                break;
            case R.id.nextMatch:
                new NextMatchDialog(this, DataContainer.matchInfoHtml).show();
                break;
            case R.id.homeButton_menu:
                it = new Intent(this, HomeActivity.class);

                it.putExtra("url", "http://www.laromacorea.com/index1.html");
                it.putExtra("sessionId", sessionId);

                arrayList.clear();
                boardListView.setAdapter(null);
                startActivity(it);
                finish();
                break;
            case R.id.noticeButton_menu:
                it = new Intent(this, BoardActivity.class);

                it.putExtra("url", "http://www.laromacorea.com/bbs/zboard.php?id=notice");
                it.putExtra("sessionId", sessionId);
                it.putExtra("text", "Notice");


                arrayList.clear();
                boardListView.setAdapter(null);
                startActivity(it);
                finish();
                break;
            case R.id.squadButton_menu:
                it = new Intent(this, BoardActivity.class);
                it.putExtra("url", "http://www.laromacorea.com/bbs/zboard.php?id=squad");
                it.putExtra("sessionId", sessionId);
                it.putExtra("text", "Squad");


                arrayList.clear();
                boardListView.setAdapter(null);
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


                arrayList.clear();
                boardListView.setAdapter(null);
                startActivity(it);
                finish();
                break;
            case R.id.freeButton_menu:
                it = new Intent(this, BoardActivity.class);

                it.putExtra("url", "http://www.laromacorea.com/bbs/zboard.php?id=free2");
                it.putExtra("sessionId", sessionId);
                it.putExtra("text", "Free");


                arrayList.clear();
                boardListView.setAdapter(null);
                startActivity(it);
                finish();
                break;
            case R.id.specialButton_menu:
                it = new Intent(this, BoardActivity.class);

                it.putExtra("url", "http://www.laromacorea.com/bbs/zboard.php?id=sp");
                it.putExtra("sessionId", sessionId);
                it.putExtra("text", "Special");


                arrayList.clear();
                boardListView.setAdapter(null);
                startActivity(it);
                finish();
                break;
            case R.id.mediaButton_menu:
                it = new Intent(this, BoardActivity.class);

                it.putExtra("url", "http://www.laromacorea.com/bbs/zboard.php?id=media");
                it.putExtra("sessionId", sessionId);
                it.putExtra("text", "Media");


                arrayList.clear();
                boardListView.setAdapter(null);
                startActivity(it);
                finish();
                break;
            case R.id.writePost:
                it = new Intent(this, WriteActivity.class);
                it.putExtra("sessionId", sessionId);
                it.putExtra("boardId", title);
                it.putExtra("url", "http://www.laromacorea.com/index1.html");


                arrayList.clear();
                boardListView.setAdapter(null);
                startActivity(it);
                break;

            case R.id.pagePrev:
                if (pageOfPage > 0) pageOfPage--;
                initPageButton();
                break;

            case R.id.pageNext:
                if (pageOfPage < maxPage) pageOfPage++;
                initPageButton();
                break;

            case R.id.page1:
                initPageButton();
                page1.setTextColor(Color.BLUE);
                it = new Intent(this, BoardActivity.class);
                it.putExtra("url", htmlPageUrl);
                it.putExtra("sessionId", sessionId);
                it.putExtra("text", getBoardName(title));
                it.putExtra("boardId", getBoardName(title));
                it.putExtra("page", 1 + (10 * pageOfPage));
                arrayList.clear();
                boardListView.setAdapter(null);
                startActivity(it);
                finish();
                break;

            case R.id.page2:
                initPageButton();
                page2.setTextColor(Color.BLUE);
                it = new Intent(this, BoardActivity.class);
                it.putExtra("url", htmlPageUrl);
                it.putExtra("sessionId", sessionId);
                it.putExtra("text", getBoardName(title));
                it.putExtra("boardId", getBoardName(title));
                it.putExtra("page", 2 + (10 * pageOfPage));
                arrayList.clear();
                boardListView.setAdapter(null);
                startActivity(it);
                finish();
                break;

            case R.id.page3:
                initPageButton();
                page3.setTextColor(Color.BLUE);
                it = new Intent(this, BoardActivity.class);
                it.putExtra("url", htmlPageUrl);
                it.putExtra("sessionId", sessionId);
                it.putExtra("text", getBoardName(title));
                it.putExtra("boardId", getBoardName(title));
                it.putExtra("page", 3 + (10 * pageOfPage));
                arrayList.clear();
                boardListView.setAdapter(null);
                startActivity(it);
                finish();
                break;

            case R.id.page4:
                initPageButton();
                page4.setTextColor(Color.BLUE);
                it = new Intent(this, BoardActivity.class);
                it.putExtra("url", htmlPageUrl);
                it.putExtra("sessionId", sessionId);
                it.putExtra("text", getBoardName(title));
                it.putExtra("boardId", getBoardName(title));
                it.putExtra("page", 4 + (10 * pageOfPage));
                arrayList.clear();
                boardListView.setAdapter(null);
                startActivity(it);
                finish();
                break;


            case R.id.page5:
                initPageButton();
                page5.setTextColor(Color.BLUE);
                it = new Intent(this, BoardActivity.class);
                it.putExtra("url", htmlPageUrl);
                it.putExtra("sessionId", sessionId);
                it.putExtra("text", getBoardName(title));
                it.putExtra("boardId", getBoardName(title));
                it.putExtra("page", 5 + (10 * pageOfPage));
                arrayList.clear();
                boardListView.setAdapter(null);
                startActivity(it);
                finish();
                break;


            case R.id.page6:
                initPageButton();
                page6.setTextColor(Color.BLUE);
                it = new Intent(this, BoardActivity.class);
                it.putExtra("url", htmlPageUrl);
                it.putExtra("sessionId", sessionId);
                it.putExtra("text", getBoardName(title));
                it.putExtra("boardId", getBoardName(title));
                it.putExtra("page", 6 + (10 * pageOfPage));
                arrayList.clear();
                boardListView.setAdapter(null);
                startActivity(it);
                finish();
                break;


            case R.id.page7:
                initPageButton();
                page7.setTextColor(Color.BLUE);
                it = new Intent(this, BoardActivity.class);
                it.putExtra("url", htmlPageUrl);
                it.putExtra("sessionId", sessionId);
                it.putExtra("text", getBoardName(title));
                it.putExtra("boardId", getBoardName(title));
                it.putExtra("page", 7 + (10 * pageOfPage));
                arrayList.clear();
                boardListView.setAdapter(null);
                startActivity(it);
                finish();
                break;


            case R.id.page8:
                initPageButton();
                page8.setTextColor(Color.BLUE);
                it = new Intent(this, BoardActivity.class);
                it.putExtra("url", htmlPageUrl);
                it.putExtra("sessionId", sessionId);
                it.putExtra("text", getBoardName(title));
                it.putExtra("boardId", getBoardName(title));
                it.putExtra("page", 8 + (10 * pageOfPage));
                arrayList.clear();
                boardListView.setAdapter(null);
                startActivity(it);
                finish();
                break;


            case R.id.page9:
                initPageButton();
                page9.setTextColor(Color.BLUE);
                it = new Intent(this, BoardActivity.class);
                it.putExtra("url", htmlPageUrl);
                it.putExtra("sessionId", sessionId);
                it.putExtra("text", getBoardName(title));
                it.putExtra("boardId", getBoardName(title));
                it.putExtra("page", 9 + (10 * pageOfPage));
                arrayList.clear();
                boardListView.setAdapter(null);
                startActivity(it);
                finish();
                break;


            case R.id.page10:
                initPageButton();
                page10.setTextColor(Color.BLUE);
                it = new Intent(this, BoardActivity.class);
                it.putExtra("url", htmlPageUrl);
                it.putExtra("sessionId", sessionId);
                it.putExtra("text", getBoardName(title));
                it.putExtra("boardId", getBoardName(title));
                it.putExtra("page", 10 + (10 * pageOfPage));

                arrayList.clear();
                boardListView.setAdapter(null);
                startActivity(it);
                finish();
                break;
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

        public void onItemClick(AdapterView<?> parentView, View clickedView, int position, long id) {
            Intent it = new Intent(BoardActivity.this, ContentActivity.class);


            position -= 1;
            String text = arrayList.get(position).getContents().str;
            String url = arrayList.get(position).getContents().url;

            String tempUrl = "http://www.laromacorea.com/bbs/view.php?";
            String boardName = getBoardName(BoardActivity.this.title);
            int currNum = getCurrentPostNum(url);

            tempUrl += "id=" + boardName;
            tempUrl += "&no=" + currNum;

            String userId = arrayList.get(position).getContents().id;

            it.putExtra("text", text);
            it.putExtra("url", tempUrl);
            it.putExtra("id", userId);
            it.putExtra("sessionId", sessionId);
            it.putExtra("boardId", title);
            startActivity(it);

            arrayList.clear();
            boardListView.setAdapter(null);
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent it = new Intent(BoardActivity.this, HomeActivity.class);

        it.putExtra("sessionId", sessionId);
        it.putExtra("boardId", title);
        startActivity(it);
        finish();
    }

    public String getBoardName(String boardId) {
        if (boardId.equals("Notice") || boardId.equals("notice")) return "notice";
        else if (boardId.equals("Squad") || boardId.equals("squad")) return "squad";
        else if (boardId.equals("Match") || boardId.equals("match") || boardId.equals("1213match")) return "1213match";
        else if (boardId.equals("Calcio") || boardId.equals("calcio")) return "calcio";
        else if (boardId.equals("Free") || boardId.equals("free") || boardId.equals("free2")) return "free2";
        else if (boardId.equals("Special") || boardId.equals("special") || boardId.equals("sp")) return "sp";
        else if (boardId.equals("Media") || boardId.equals("media")) return "media";

        return null;
    }

    public int getCurrentPostNum(String htmlPageUrl) {
        String test = htmlPageUrl.split("no=")[1];
        test = test.split("\\(")[0];

        return Integer.parseInt(test);
    }

    public void initPageButton() {
        page1.setTextColor(Color.BLACK);
        page1.setText("[" + ((pageOfPage * 10) + 1) + "]");
        page1.setTextSize(13.0f);

        page2.setTextColor(Color.BLACK);
        page2.setText("[" + ((pageOfPage * 10) + 2) + "]");
        page2.setTextSize(13.0f);

        page3.setTextColor(Color.BLACK);
        page3.setText("[" + ((pageOfPage * 10) + 3) + "]");
        page3.setTextSize(13.0f);

        page4.setTextColor(Color.BLACK);
        page4.setText("[" + ((pageOfPage * 10) + 4) + "]");
        page4.setTextSize(13.0f);

        page5.setTextColor(Color.BLACK);
        page5.setText("[" + ((pageOfPage * 10) + 5) + "]");
        page5.setTextSize(13.0f);

        page6.setTextColor(Color.BLACK);
        page6.setText("[" + ((pageOfPage * 10) + 6) + "]");
        page6.setTextSize(13.0f);

        page7.setTextColor(Color.BLACK);
        page7.setText("[" + ((pageOfPage * 10) + 7) + "]");
        page7.setTextSize(13.0f);

        page8.setTextColor(Color.BLACK);
        page8.setText("[" + ((pageOfPage * 10) + 8) + "]");
        page8.setTextSize(13.0f);

        page9.setTextColor(Color.BLACK);
        page9.setText("[" + ((pageOfPage * 10) + 9) + "]");
        page9.setTextSize(13.0f);

        page10.setTextColor(Color.BLACK);
        page10.setText("[" + ((pageOfPage * 10) + 10) + "]");
        page10.setTextSize(13.0f);
    }

    public String getBoardTitle(String boardId) {
        if (boardId.equals("Notice") || boardId.equals("notice")) return "Notice";
        else if (boardId.equals("Squad") || boardId.equals("squad")) return "Squad";
        else if (boardId.equals("Match") || boardId.equals("match") || boardId.equals("1213match")) return "Match";
        else if (boardId.equals("Calcio") || boardId.equals("calcio")) return "Calcio";
        else if (boardId.equals("Free") || boardId.equals("free") || boardId.equals("free2")) return "Free";
        else if (boardId.equals("Special") || boardId.equals("special") || boardId.equals("sp")) return "Special";
        else if (boardId.equals("Media") || boardId.equals("media")) return "Media";

        return null;
    }

}
