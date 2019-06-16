package com.keehl.laromacorea;

public class HomeListViewItem {
    private String number;
    private String title;
    private String userId;
    private String date;
    private String views;
    private ContentsData contents;
    private String imageUrl;
    private boolean isNotice = false;

    public void setNumber(String num) {
        number = num;
    }

    public void setTitle(String title) {
        this.title = title;
    }
    public void setUserId(String id) {
        this.userId = id;
    }

    public void setDate(String date) {
        this.date = date;
    }
    public void setViews(String views) {
        this.views = views;
    }
    public void setContents(ContentsData contents) {
        this.contents = contents;
    }
    public ContentsData getContents() {
        return contents;
    }

    public String getNumber() {
        return number;
    }

    public String getTitle() {
        return title;
    }
    public String getUserId() {
        return userId;
    }

    public String getDate() {
        return date;
    }
    public String getViews() {
        return views;
    }
    public void setNotice(boolean check) {
        this.isNotice = check;
    }
    public boolean getIsNotice() {
        return isNotice;
    }

    public void setImageUrl(String url) {
        this.imageUrl = url;
    }

    public String getImageUrl() {
        return imageUrl;
    }
}
