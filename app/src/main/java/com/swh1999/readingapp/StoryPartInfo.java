package com.swh1999.readingapp;

public class StoryPartInfo {
    private String partTitle;
    private String partDes;
    private int partView;
    private String privacy;

    public StoryPartInfo(String partTitle, String partDes,int partView,String privacy) {
        this.partTitle = partTitle;
        this.partDes = partDes;
        this.partView=partView;
        this.privacy=privacy;

    }

    public StoryPartInfo() {
    }

    public String getPartTitle() {
        return partTitle;
    }

    public void setPartTitle(String partTitle) {
        this.partTitle = partTitle;
    }

    public String getPartDes() {
        return partDes;
    }

    public void setPartDes(String partDes) {
        this.partDes = partDes;
    }

    public int getPartView() {
        return partView;
    }

    public void setPartView(int partView) {
        this.partView = partView;
    }

    public String getPrivacy() {
        return privacy;
    }

    public void setPrivacy(String privacy) {
        this.privacy = privacy;
    }
}
