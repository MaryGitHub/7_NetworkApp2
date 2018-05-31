package com.example.android.vaccineguardiannews;

public class Article {

    private String mAuthorArt;
    private String mSectionArt;
    private String mTitleArt;
    private String mTimeArt;
    private String mUrl;

    public Article(String sectionArt, String authorArt, String titleArt, String timeArt, String url) {
        mAuthorArt = authorArt;
        mSectionArt = sectionArt;
        mTitleArt = titleArt;
        mTimeArt = timeArt;
        mUrl = url;
    }

    public String getAuthor() {
        return mAuthorArt;
    }

    public String getSection() {
        return mSectionArt;
    }

    public String getTitle() {
        return mTitleArt;
    }

    public String getTime() {
        return mTimeArt;
    }

    public String getUrl() {
        return mUrl;
    }

}