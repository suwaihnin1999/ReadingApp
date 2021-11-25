package com.swh1999.readingapp;

public class StoryViewerInfo {
    private String storyTitle;
    private String authorId;
    private int totalView;
    private int totalLike;

    public StoryViewerInfo(String storyTitle, String authorId,int totalView, int totalLike) {
        this.storyTitle = storyTitle;
        this.authorId=authorId;
        this.totalView = totalView;
        this.totalLike = totalLike;
    }

    public StoryViewerInfo() {
    }

    public String getStoryTitle() {
        return storyTitle;
    }

    public void setStoryTitle(String storyTitle) {
        this.storyTitle = storyTitle;
    }

    public int getTotalView() {
        return totalView;
    }

    public void setTotalView(int totalView) {
        this.totalView = totalView;
    }

    public int getTotalLike() {
        return totalLike;
    }

    public void setTotalLike(int totalLike) {
        this.totalLike = totalLike;
    }

    public String getAuthorId() {
        return authorId;
    }

    public void setAuthorId(String authorId) {
        this.authorId = authorId;
    }
}
