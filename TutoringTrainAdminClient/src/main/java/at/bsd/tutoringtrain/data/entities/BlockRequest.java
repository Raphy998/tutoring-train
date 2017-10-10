package at.bsd.tutoringtrain.data.entities;

import java.util.Date;

/**
 *
 * @author Marco Wilscher <marco.wilscher@edu.htl-villach.at>
 */
public class BlockRequest {
    private String username;
    private String reason;
    private Date duedate;
    
    public BlockRequest() {
        this(null, null, null);
    }
    
    public BlockRequest(String username, String reason, Date duedate) {
        this.username = username;
        this.reason = reason;
        this.duedate = duedate;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Date getDuedate() {
        return duedate;
    }

    public void setDuedate(Date duedate) {
        this.duedate = duedate;
    }
}
