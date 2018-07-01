package com.afrikcode.alccodechallenge.data;

public class Notes {

    private String title;
    private String body;
    private String date;
    private String uid;


    public Notes() {
    }

    public Notes(String title, String body, String date, String uid) {
        this.title = title;
        this.body = body;
        this.date = date;
        this.uid = uid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}

