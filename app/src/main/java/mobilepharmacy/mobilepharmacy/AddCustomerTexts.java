package mobilepharmacy.mobilepharmacy;

import java.io.Serializable;

/**
 * Created by Kaushek on 26/02/2018.
 */

public class AddCustomerTexts implements Serializable{

    private String to;
    private String from;
    private String subject;
    private String notes;
    private String ImgKey;
    private String url;

    AddCustomerTexts(){

    }

    public AddCustomerTexts(String TO, String FROM, String SUBJECT, String NOTES, String key, String imgUrl)
    {
        this.to = TO;
        this.from = FROM;
        this.subject = SUBJECT;
        this.notes = NOTES;
        this.ImgKey = key;
        this.url = imgUrl;
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
}
