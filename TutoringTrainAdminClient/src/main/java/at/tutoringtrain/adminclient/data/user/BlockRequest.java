package at.tutoringtrain.adminclient.data.user;

import at.tutoringtrain.adminclient.data.mapper.DataMappingViews;
import at.tutoringtrain.adminclient.user.BlockDuration;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonView;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;

/**
 *
 * @author Marco Wilscher marco.wilscher@edu.htl-villach.at
 */
public final class BlockRequest {
    @JsonView ({
        DataMappingViews.BlockRequest.Out.Limited.class,
        DataMappingViews.BlockRequest.Out.Unlimited.class
    })
    private final String username;
    
    @JsonView ({
        DataMappingViews.BlockRequest.Out.Limited.class,
        DataMappingViews.BlockRequest.Out.Unlimited.class
    })
    private final String reason;
    
    @JsonView ({
        DataMappingViews.BlockRequest.Out.Limited.class
    })
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssZ")
    private final Date duedate;
   
    
    public BlockRequest(String username, String reason, BlockDuration duration) {
        this.username = username;
        this.reason = reason;
        this.duedate = (duration == null ? null : Date.from(ZonedDateTime.now(ZoneId.systemDefault()).plusMinutes(duration.getMinutes()).toInstant()));
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
