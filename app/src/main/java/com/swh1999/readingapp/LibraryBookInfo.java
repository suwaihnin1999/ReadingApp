package com.swh1999.readingapp;

public class LibraryBookInfo {
    private String authorId;
    private String title;
    private String partTitle;
    private int scrollPos;
    private int progressMax;
    public LibraryBookInfo(String authorId, String title,String partTitle,int scrollPos,int progressMax) {
        this.authorId = authorId;
        this.title = title;
        this.partTitle=partTitle;
        this.scrollPos=scrollPos;
        this.progressMax=progressMax;
    }

    public LibraryBookInfo() {
    }

    public String getAuthorId() {
        return authorId;
    }

    public void setAuthorId(String authorId) {
        this.authorId = authorId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPartTitle() {
        return partTitle;
    }

    public void setPartTitle(String partTitle) {
        this.partTitle = partTitle;
    }

    public int getScrollPos() {
        return scrollPos;
    }

    public void setScrollPos(int scrollPos) {
        this.scrollPos = scrollPos;
    }

    public int getProgressMax() {
        return progressMax;
    }

    public void setProgressMax(int progressMax) {
        this.progressMax = progressMax;
    }
}
