package com.keehl.laromacorea;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.Toast;

import com.navdrawer.SimpleSideDrawer;

public class ContentActivity extends FragmentActivity implements View.OnClickListener{
    private SimpleSideDrawer slideMenu;
    private SimpleSideDrawer slideMatch;

    private Button menuButton;
    private Button matchButton;

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

    private CustomViewPager viewPager ;
    private ContentViewPagerAdapter pagerAdapter ;
    private InfiniteViewPagerAdapter infiniteViewPagerAdapter;
    private MinFragmentPagerAdapter minFragmentPagerAdapter;

    private String htmlPageUrl;
    private String title;
    private String sessionId;
    private String userId;
    private String boardId;


    public static final int LEFT = 1;
    public static final int RIGHT = 2;
    public static final int NONE = 0;

    private int direction;
    private int prevDirection;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content);


        viewPager = (CustomViewPager) findViewById(R.id.viewPager);

        FragmentPagerAdapter adapter = new FragmentPagerAdapter(getSupportFragmentManager()) {

            @Override
            public int getCount() {
                return Integer.MAX_VALUE;
            }

            @Override
            public Fragment getItem(int position) {
                PageFragment fragment = PageFragment.create(position);
                Bundle args = new Bundle();
                fragment.setArguments(args);
                return fragment;
            }

        };

        // wrap pager to provide a minimum of 4 pages
        MinFragmentPagerAdapter wrappedMinAdapter = new MinFragmentPagerAdapter(getSupportFragmentManager());
        wrappedMinAdapter.setAdapter(adapter);

        // wrap pager to provide infinite paging with wrap-around
        PagerAdapter wrappedAdapter = new InfiniteViewPagerAdapter(wrappedMinAdapter);

        viewPager.setAdapter(wrappedAdapter);
        viewPager.setContentActivity(this);
        viewPager.setCurrentItem(1000);
        viewPager.setOffscreenPageLimit(1);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener()
        {
            private boolean checkDirection;
            private int lastPostNum;
            private boolean scrollStarted;
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels)
            {
                this.lastPostNum = getLastPostNum(boardId);
                if (checkDirection) {
                    if (0.5f > positionOffset) {
                        if (getCurrentPostNum() > 0) {
                            viewPager.setAllowedSwipeDirection(SwipeDirection.all);
                            ContentActivity.this.setDirection(ContentActivity.LEFT);
                            ContentActivity.this.moveLeft();
                            Utils.showToast(ContentActivity.this.getApplicationContext(), "LEFT", Toast.LENGTH_LONG);
                        } else {
                            viewPager.setAllowedSwipeDirection(SwipeDirection.left);
                            Utils.showToast(ContentActivity.this.getApplicationContext(), "다음글이없습니다.", Toast.LENGTH_LONG);
                        }
                    } else if (0.5f <= positionOffset) {
                        if (getCurrentPostNum() < lastPostNum) {
                            viewPager.setAllowedSwipeDirection(SwipeDirection.all);
                            ContentActivity.this.setDirection(ContentActivity.RIGHT);
                            ContentActivity.this.moveRight();
                            Utils.showToast(ContentActivity.this.getApplicationContext(), "RIGHT", Toast.LENGTH_LONG);
                        } else {
                            viewPager.setAllowedSwipeDirection(SwipeDirection.right);
                            Utils.showToast(ContentActivity.this.getApplicationContext(), "다음글이 없습니다.", Toast.LENGTH_LONG);
                        }
                    }
                    this.checkDirection = false;
                }
            }

            @Override
            public void onPageSelected(int position)
            {

            }

            @Override
            public void onPageScrollStateChanged(int state)
            {
                if ((!this.scrollStarted) && (state == 1)) {
                    this.scrollStarted = true;
                    this.checkDirection = true;
                    return;
                }
                this.scrollStarted = false;
            }
        });


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


        slideMenu = new SimpleSideDrawer(this);
        slideMenu.setLeftBehindContentView(R.layout.slide_menu);

        slideMatch = new SimpleSideDrawer(this);
        slideMatch.setRightBehindContentView(R.layout.slide_game);

        menuButton = findViewById(R.id.menuButton_content);
        menuButton.setOnClickListener(this);

        matchButton = findViewById(R.id.matchButton_content);
        matchButton.setOnClickListener(this);

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


        leagueTableButton = findViewById(R.id.leagueTable);
        leagueTableButton.setOnClickListener(this);

        personalTableButton = findViewById(R.id.personalTable);
        personalTableButton.setOnClickListener(this);

        nextMatchButton = findViewById(R.id.nextMatch);
        nextMatchButton.setOnClickListener(this);
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
                slideMatch.toggleRightDrawer();
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

                /*
                  htmlPageUrl = null;
                title = null;
                sessionId = null;
            } else {
                htmlPageUrl = extra.getString("url");
                title = extra.getString("text");
                sessionId = extra.getString("sessionId");
                 */
                it.putExtra("url", "http://www.laromacorea.com/bbs/zboard.php?id=notice");
                it.putExtra("sessionId", sessionId);
                it.putExtra("text", "Notice");
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
                finish();
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
            case R.id.leagueTable:
                new MatchDialog(this, DataContainer.leagueTableHtml).show();
                break;
            case R.id.personalTable:
                new PersonalTableDialog(this, DataContainer.playerInfoHtml).show();
                break;
            case R.id.nextMatch:
                new NextMatchDialog(this, DataContainer.matchInfoHtml).show();
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

        Intent it = new Intent(ContentActivity.this, BoardActivity.class);
        String url = "http://www.laromacorea.com/bbs/zboard.php?id=" + getBoardName(boardId);
        it.putExtra("url", url);
        it.putExtra("sessionId", sessionId);
        it.putExtra("text", boardId);
        startActivity(it);
        finish();
    }
    public void moveLeft() {
        String test = htmlPageUrl.split("no=")[1];
        test = test.split("\\(")[0];

        int postNum = Integer.parseInt(test);
        postNum--;

        if (postNum >= 0) {
            String postUrl = "http://www.laromacorea.com/bbs/view.php?";
            postUrl += "id=" + getBoardName(boardId) + "&no=" + postNum;

            htmlPageUrl = postUrl;
        }
    }
    public void moveRight() {
        String test = htmlPageUrl.split("no=")[1];
        test = test.split("\\(")[0];

        int postNum = Integer.parseInt(test);
        postNum++;

        if (postNum <= getLastPostNum(boardId)) {
            String postUrl = "http://www.laromacorea.com/bbs/view.php?";
            postUrl += "id=" + getBoardName(boardId) + "&no=" + postNum;

            htmlPageUrl = postUrl;
        }
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
    public String getHtmlPageUrl() {
        return htmlPageUrl;
    }

    public int getDirection() {
        return direction;
    }
    public void setDirection(int direction) {
        prevDirection = this.direction;
        this.direction = direction;
    }

    public int getPrevDirection () {
        return prevDirection;
    }
    public int getLastPostNum(String boardId) {
        if (boardId.equals("Notice") || boardId.equals("notice")) {
            return UserInfo.noticeBoardLastPost;
        } else if (boardId.equals("Squad") || boardId.equals("sqaud")) {
            return UserInfo.squadBoardLastPost;
        } else if (boardId.equals("Match") || boardId.equals("match") || boardId.equals("1213match")) {
            return UserInfo.matchBoardLastPost;
        } else if (boardId.equals("Calcio") || boardId.equals("calcio")) {
            return UserInfo.calcioBoardLastPost;
        } else if (boardId.equals("Free") || boardId.equals("free") || boardId.equals("free2"))  {
            return UserInfo.freeBoardLastPost;
        } else if (boardId.equals("Special") || boardId.equals("special") || boardId.equals("sp")) {
            return UserInfo.specialBoardLastPost;
        } else if (boardId.equals("Media") || boardId.equals("media")) {
            return UserInfo.mediaBoardLastPost;
        }
        return -1;
    }

    public int getCurrentPostNum() {
        String test = htmlPageUrl.split("no=")[1];
        test = test.split("\\(")[0];

        return Integer.parseInt(test);
    }
}
