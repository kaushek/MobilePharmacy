package model;

/**
 * Created by Kaushek on 07/05/2018.
 */

public class ReplyMessages {

    private String To;
    private String frm;
    private String sub;
    private String msg;
    private String date;
    private Boolean status;

    public ReplyMessages()
    {}

    public ReplyMessages(String to, String from, String subject, String message, String currentDate, Boolean stats)
    {
        this.To = to;
        this.frm = from;
        this.sub = subject;
        this.msg = message;
        this.date = currentDate;
        this.status = stats;
    }

    public String getTo() {
        return To;
    }

    public void setTo(String to) {
        To = to;
    }

    public String getFrm() {
        return frm;
    }

    public void setFrm(String frm) {
        this.frm = frm;
    }

    public String getSub() {
        return sub;
    }

    public void setSub(String sub) {
        this.sub = sub;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }
}


