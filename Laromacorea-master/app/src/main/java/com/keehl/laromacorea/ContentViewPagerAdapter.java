package com.keehl.laromacorea;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class ContentViewPagerAdapter extends FragmentStatePagerAdapter {

    private Context mContext = null ;


    ContentViewPagerAdapter(FragmentManager fragmentManager) {
        super(fragmentManager);
    }

    @Override
    public int getCount() {
        return Integer.MAX_VALUE;
    }

    @Override
    public Fragment getItem(int position) {
        PageFragment fragment = PageFragment.create(position);
        fragment.setUserVisibleHint(true);
        return fragment;
    }

    @Override
    public int getItemPosition(Object object) {
        PageFragment pageFragment = (PageFragment) object;

        if (pageFragment != null) {

        }

        return 1;
    }
}