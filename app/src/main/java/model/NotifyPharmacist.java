package model;

/**
 * Created by Kaushek on 13/05/2018.
 */

public class NotifyPharmacist {

    public String from;
    public String delStatus;
    public Boolean status;

    public NotifyPharmacist(){}

    public NotifyPharmacist(String from, String deliStatus, Boolean stat) {
        this.from = from;
        this.delStatus = deliStatus;
        this.status = stat;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getDelStatus() {
        return delStatus;
    }

    public void setDelStatus(String delStatus) {
        this.delStatus = delStatus;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }
}
