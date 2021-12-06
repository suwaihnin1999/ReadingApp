package com.swh1999.readingapp;

public class LibraryBookInfo {
    private String authorId;
    private String title;

    public LibraryBookInfo(String authorId, String title) {
        this.authorId = authorId;
        this.title = title;
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
}
