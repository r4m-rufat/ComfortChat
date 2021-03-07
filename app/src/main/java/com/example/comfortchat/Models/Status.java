package com.example.comfortchat.Models;

import android.net.Uri;

public class Status {

    private String id;
    private String userID;
    private String statusDate;
    private String statusImage;
    private String statusText;
    private String viewCount;

    public Status(){
    }

    public Status(String id, String userID, String statusDate, String statusImage, String statusText, String viewCount) {
        this.id = id;
        this.userID = userID;
        this.statusDate = statusDate;
        this.statusImage = statusImage;
        this.statusText = statusText;
        this.viewCount = viewCount;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getStatusDate() {
        return statusDate;
    }

    public void setStatusDate(String statusDate) {
        this.statusDate = statusDate;
    }

    public String getStatusImage() {
        return statusImage;
    }

    public void setStatusImage(String statusImage) {
        this.statusImage = statusImage;
    }

    public String getStatusText() {
        return statusText;
    }

    public void setStatusText(String statusText) {
        this.statusText = statusText;
    }

    public String getViewCount() {
        return viewCount;
    }

    public void setViewCount(String viewCount) {
        this.viewCount = viewCount;
    }


}
