package mobilepharmacy.mobilepharmacy;

/**
 * Created by Kaushek on 07/05/2018.
 */

class ReplyMessages {

    private String To;
    private String frm;
    private String sub;
    private String msg;

    ReplyMessages()
    {}

    public ReplyMessages(String to, String from, String subject, String message)
    {
        this.To = to;
        this.frm = from;
        this.sub = subject;
        this.msg = message;
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

}


