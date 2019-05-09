package com.example.keehl.laromacorea;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.InputStream;
import java.util.ArrayList;


public class HomeListViewAdapter extends BaseAdapter {
    private ArrayList<HomeListViewItem> listViewItemlist = new ArrayList<HomeListViewItem>();
    public static final int HOME_MODE = 1;
    public static final int BOARD_MODE = 2;
    public static final int REPLY_MODE = 3;

    private int mode = 1;
    public HomeListViewAdapter() {}


    public void setMode(int mode) {
        this.mode = mode;
    }

    @Override
    public int getCount() {
        return listViewItemlist.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (mode == 3) {
            final int pos = position;
            final Context context = parent.getContext();

            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.listview_item_reply, parent, false);
            }
            ImageView userIcon = (ImageView) convertView.findViewById(R.id.userIcon);

            TextView idTv = (TextView) convertView.findViewById(R.id.id);
            idTv.setGravity(Gravity.LEFT);

            TextView contentTv = (TextView) convertView.findViewById(R.id.replyContent);
            contentTv.setGravity(Gravity.LEFT);

            TextView timeTv = (TextView) convertView.findViewById(R.id.replyTime);
            timeTv.setGravity(Gravity.LEFT);

            HomeListViewItem item = listViewItemlist.get(pos);

            String temp = item.getContents().str;
            String contents = "";
            String times = "";
            for (int i = 0; i < temp.length(); i++) {
                if (i + 28 < temp.length()) contents += temp.charAt(i);
                else times += temp.charAt(i);
            }

            String imageUrl = item.getImageUrl();
            new DownloadImageTask((ImageView)convertView.findViewById(R.id.userIcon))
                    .execute(imageUrl);

            userIcon.getLayoutParams().height = 50;
            idTv.setText(item.getUserId());
            contentTv.setText(contents);
            timeTv.setText(times);

        } else if (mode == 2) {
            final int pos = position;
            final Context context = parent.getContext();

            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.listview_item, parent, false);
            }
//userId, date, views

            TextView numTv = (TextView) convertView.findViewById(R.id.number);
        //    numTv.setGravity(Gravity.LEFT);
            TextView titleTv = (TextView) convertView.findViewById(R.id.title);
            titleTv.setGravity(Gravity.LEFT);

            ImageView userIcon = (ImageView) convertView.findViewById(R.id.userIcon);
            TextView userIdTv = (TextView) convertView.findViewById(R.id.userId);
            userIdTv.setGravity(Gravity.LEFT);
            TextView dateTv = (TextView) convertView.findViewById(R.id.date);
        //    dateTv.setGravity(Gravity.LEFT);
            TextView viewsTv = (TextView) convertView.findViewById(R.id.views);
        //    viewsTv.setGravity(Gravity.LEFT);


            HomeListViewItem item = listViewItemlist.get(pos);

            if (item.getIsNotice()) {
                convertView.setBackgroundColor(Color.LTGRAY);
            }

            if (!item.getNumber().equals("0")) numTv.setText(item.getNumber());
            else numTv.setText("");

            titleTv.setText(item.getTitle() + "   " + item.getContents().comments);

            new DownloadImageTask((ImageView)convertView.findViewById(R.id.userIcon))
                    .execute(item.getImageUrl());

            userIcon.getLayoutParams().height = 50;
            userIdTv.setText(item.getUserId());
            dateTv.setText(item.getDate());
            viewsTv.setText(item.getViews());
        } else if (mode == 1) {
            final int pos = position;
            final Context context = parent.getContext();

            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.listview_item_home, parent, false);
            }
//userId, date, views

            TextView titleTv = (TextView) convertView.findViewById(R.id.title);
            titleTv.setGravity(Gravity.LEFT);
            HomeListViewItem item = listViewItemlist.get(pos);

            titleTv.setText(item.getTitle() + " " + item.getContents().comments);
        }
        return convertView;
    }

    @Override
    public long getItemId(int position) {
        return position ;
    }

    // 지정한 위치(position)에 있는 데이터 리턴 : 필수 구현
    @Override
    public Object getItem(int position) {
        return listViewItemlist.get(position) ;
    }

    // 아이템 데이터 추가를 위한 함수. 개발자가 원하는대로 작성 가능.
    public void addItem(String num, String title, ContentsData contents, String userId, String date, String views, String imageUrl, boolean isNotice) {
        HomeListViewItem item = new HomeListViewItem();

        item.setNumber(num);
        item.setTitle(title);
        item.setContents(contents);
        item.setUserId(userId);
        item.setDate(date);
        item.setViews(views);
        item.setImageUrl(imageUrl);
        item.setNotice(isNotice);

        listViewItemlist.add(item);
    }


    public ArrayList<HomeListViewItem> getArrayList() {
        return listViewItemlist;
    }


    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }
}
