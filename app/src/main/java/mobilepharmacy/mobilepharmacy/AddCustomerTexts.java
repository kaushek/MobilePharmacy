package mobilepharmacy.mobilepharmacy;

/**
 * Created by Kaushek on 26/02/2018.
 */

public class AddCustomerTexts {

    private String to;
    private String from;
    private String subject;
    private String notes;

    AddCustomerTexts(){

    }

    public AddCustomerTexts(String TO, String FROM, String SUBJECT, String NOTES)
    {
        this.to = TO;
        this.from = FROM;
        this.subject = SUBJECT;
        this.notes = NOTES;
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
}
