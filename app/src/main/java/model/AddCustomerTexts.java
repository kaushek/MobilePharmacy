package model;

import java.io.Serializable;

/**
 * Created by Kaushek on 26/02/2018.
 */

public class AddCustomerTexts implements Serializable{

    private String id;
    private String to;
    private String from;
    private String subject;
    private String notes;
    private String ImgKey;
    private String url;
    private String date;
    private String order;
    private Boolean status;



    public AddCustomerTexts(){

    }
    public AddCustomerTexts(String to, String from, String subject, String notes, String imgKey, String url,String CurrentDate,String ord, Boolean status) {
        this.to = to;
        this.from = from;
        this.subject = subject;
        this.notes = notes;
        ImgKey = imgKey;
        this.url = url;
        this.date = CurrentDate;
        this.order = ord;
        this.status = status;

    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getImgKey() {
        return ImgKey;
    }

    public void setImgKey(String imgKey) {
        ImgKey = imgKey;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
