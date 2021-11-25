package com.swh1999.readingapp;

public class StoryInfo {
    private String storyTitle;
    private String storyTitleNew;
    private String uid;
    private String storyDes;
    private String storyImg;
    private String tag;

    public StoryInfo(String uid,String storyTitle, String storyDes, String storyImg,String storyTitleNew,String tag) {
        this.uid=uid;
        this.storyTitle = storyTitle;
        this.storyDes = storyDes;
        this.storyImg = storyImg;
        this.storyTitleNew=storyTitleNew;
        this.tag=tag;
    }

    public StoryInfo() {
    }

    public String getStoryTitle() {
        return storyTitle;
    }

    public void setStoryTitle(String storyTitle) {
        this.storyTitle = storyTitle;
    }

    public String getStoryDes() {
        return storyDes;
    }

    public void setStoryDes(String storyDes) {
        this.storyDes = storyDes;
    }

    public String getStoryImg() {
        return storyImg;
    }

    public void setStoryImg(String storyImg) {
        this.storyImg = storyImg;
    }

    public String getStoryTitleNew() {
        return storyTitleNew;
    }

    public void setStoryTitleNew(String storyTitleNew) {
        this.storyTitleNew = storyTitleNew;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
