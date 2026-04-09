package com.zagavideodown.app.models;

public class TutorialModel {
    private int resourceId;

    private int tvTitle;
    private int tvDes;

    public TutorialModel(int resourceId, int tvTitle, int tvDes) {
        this.resourceId = resourceId;
        this.tvTitle = tvTitle;
        this.tvDes = tvDes;
    }

    public int getResourceId() {
        return resourceId;
    }

    public void setResourceId(int resourceId) {
        this.resourceId = resourceId;
    }

    public int getTvTitle() {
        return tvTitle;
    }

    public void setTvTitle(int tvTitle) {
        this.tvTitle = tvTitle;
    }

    public int getTvDes() {
        return tvDes;
    }

    public void setTvDes(int tvDes) {
        this.tvDes = tvDes;
    }
}

