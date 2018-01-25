package at.tutoringtrain.adminclient.data;

import at.tutoringtrain.adminclient.datamapper.JsonUserViews;
import com.fasterxml.jackson.annotation.JsonView;
import java.io.Serializable;
import java.util.Date;

public class Blocked implements Serializable {
    private static final long serialVersionUID = 1L;
    @JsonView({
        JsonUserViews.In.Get.class
    })
    private String reason;
    @JsonView({
        JsonUserViews.In.Get.class
    })
    private Date duedate;

    public Blocked() {
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
