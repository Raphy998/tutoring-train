package at.bsd.tutoringtrain.data.entities;

import at.bsd.tutoringtrain.data.mapper.views.JsonUserViews;
import com.fasterxml.jackson.annotation.JsonView;
import java.io.Serializable;
import java.time.ZonedDateTime;

public class Blocked implements Serializable {
    private static final long serialVersionUID = 1L;
    @JsonView({
        JsonUserViews.In.Get.class
    })
    private String reason;
    private ZonedDateTime duedate;

    public Blocked() {
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
