package at.tutoringtrain.adminclient.data;

import at.tutoringtrain.adminclient.user.BlockDuration;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

/**
 *
 * @author Marco Wilscher marco.wilscher@edu.htl-villach.at
 */
public final class BlockRequest {
    private final String username;
    private final String reason;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'hh:mm:ssZ")
    private final Date duedate;
   
    
    public BlockRequest(String username, String reason, BlockDuration duration) {
        this.username = username;
        this.reason = reason;
        this.duedate = Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).plusMinutes(duration.getMinutes()).toInstant());
    }

    public String getUsername() {
        return username;
    }

    public String getReason() {
        return reason;
    }

    public Date getDuedate() {
        return duedate;
    }
}
