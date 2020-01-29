package com.example.booklistingapp_v2.models;

public class Book {

    private String mTitle, mAuthor, mDescription, mImageUrl, mURL, mCategories, mPublishedDate;
    private double mRating;
    private int mRatingCount, mPageCount;

    public Book(String mTitle, String mAuthor, String mDescription, String mImageUrl, String mURL, String mCategories, String mPublishedDate, double mRating, int mRatingCount, int mPageCount) {
        this.mTitle = mTitle;
        this.mAuthor = mAuthor;
        this.mDescription = mDescription;
        this.mImageUrl = mImageUrl;
        this.mURL = mURL;
        this.mCategories = mCategories;
        this.mPublishedDate = mPublishedDate;
        this.mRating = mRating;
        this.mRatingCount = mRatingCount;
        this.mPageCount = mPageCount;
    }

    public String getTitle() { return mTitle; }
    public String getAuthor() { return mAuthor; }
    public String getDescription() { return mDescription; }
    public double getRating() { return mRating; }
    public String getImageUrl() { return mImageUrl; }
    public String getUrl() { return mURL; }
    public String getCategories() { return mCategories; }
    public String getPublishedDate() { return mPublishedDate; }
    public int getRatingCount() { return mRatingCount; }
    public int getPageCount() { return mPageCount; }

}
