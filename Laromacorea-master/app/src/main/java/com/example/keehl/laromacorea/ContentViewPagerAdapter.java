package com.example.keehl.laromacorea;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class ContentViewPagerAdapter extends FragmentStatePagerAdapter {

    private Context mContext = null ;

    ContentActivity contentActivity;
    CustomViewPager viewPager;

    ContentViewPagerAdapter(FragmentManager fragmentManager) {
        super(fragmentManager);
    }

    ContentViewPagerAdapter(FragmentManager fragmentManager, ContentActivity contentActivity) {
        super(fragmentManager);
        this.contentActivity = contentActivity;
    }

    ContentViewPagerAdapter(FragmentManager fragmentManager, ContentActivity contentActivity, CustomViewPager viewPager) {
        super(fragmentManager);
        this.contentActivity = contentActivity;
        this.viewPager = viewPager;
    }

    @Override
    public int getCount() {
        return Integer.MAX_VALUE;
    }

    @Override
    public Fragment getItem(int position) {
        return PageFragment.create(position, contentActivity.getHtmlPageUrl());
    }

    @Override
    public int getItemPosition(Object object) {
        PageFragment pageFragment = (PageFragment) object;

        if (pageFragment != null) {

        }

        return 1;
    }
}