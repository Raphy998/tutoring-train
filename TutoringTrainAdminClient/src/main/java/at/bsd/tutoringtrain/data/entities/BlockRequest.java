package at.bsd.tutoringtrain.data.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.ZonedDateTime;

/**
 *
 * @author Marco Wilscher marco.wilscher@edu.htl-villach.at
 */
public class BlockRequest {
    private String username;
    private String reason;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'hh:mm:ssZ")
    private ZonedDateTime duedate;
    
    public BlockRequest() {
        this(null, null, null);
    }
    
    public BlockRequest(String username, String reason, ZonedDateTime duedate) {
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

    public ZonedDateTime getDuedate() {
        return duedate;
    }

    public void setDuedate(ZonedDateTime duedate) {
        this.duedate = duedate;
    }
}
