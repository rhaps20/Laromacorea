package com.example.keehl.laromacorea;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
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

public class ContentActivity extends FragmentActivity implements View.OnClickListener{
    private CustomViewPager viewPager ;
    private ContentViewPagerAdapter pagerAdapter ;

    private String htmlPageUrl;
    private String title;
    private String sessionId;
    private String userId;
    private String boardId;

    private SimpleSideDrawer slideMenu;
    private SimpleSideDrawer slideMatch;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content);

        viewPager = (CustomViewPager) findViewById(R.id.viewPager) ;
        pagerAdapter = new ContentViewPagerAdapter(this.getSupportFragmentManager(), this, viewPager);
        viewPager.setAdapter(pagerAdapter) ;
        viewPager.setContentActivity(this);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener()
        {
            private int prevPosition;
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels)
            {

            }

            @Override
            public void onPageSelected(int position)
            {

                if (position > prevPosition) {
                    moveLeft();
                } else {
                    moveRight();
                }
                prevPosition = position;
            }

            @Override
            public void onPageScrollStateChanged(int state)
            {

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
    public void moveLeft() {
        String test = htmlPageUrl.split("no=")[1];
        test = test.split("\\(")[0];

        int postNum = Integer.parseInt(test);
        postNum--;

        String postUrl = "http://www.laromacorea.com/bbs/view.php?";
        postUrl += "id=" + getBoardName(boardId) + "&no=" + postNum;

        htmlPageUrl = postUrl;
    }
    public void moveRight() {
        String test = htmlPageUrl.split("no=")[1];
        test = test.split("\\(")[0];

        int postNum = Integer.parseInt(test);
        postNum++;

        String postUrl = "http://www.laromacorea.com/bbs/view.php?";
        postUrl += "id=" + getBoardName(boardId) + "&no=" + postNum;

        htmlPageUrl = postUrl;
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
    public String getHtmlPageUrl() {
        return htmlPageUrl;
    }
}
