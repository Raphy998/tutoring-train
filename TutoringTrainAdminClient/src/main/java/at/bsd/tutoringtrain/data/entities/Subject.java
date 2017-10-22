package at.bsd.tutoringtrain.data.entities;

import at.bsd.tutoringtrain.data.mapper.views.JsonSubjectViews;
import com.fasterxml.jackson.annotation.JsonView;
import java.math.BigDecimal;

/**
 * 
 * @author Marco Wilscher marco.wilscher@edu.htl-villach.at
 */
public class Subject {
    @JsonView({
        JsonSubjectViews.In.Register.class,
        JsonSubjectViews.In.Get.class,
        JsonSubjectViews.Out.Update.class 
    })
    private BigDecimal id;
    @JsonView({
       JsonSubjectViews.Out.Register.class,
        JsonSubjectViews.Out.Update.class
    })
    private String dename;
    @JsonView({
        JsonSubjectViews.Out.Register.class,
        JsonSubjectViews.Out.Update.class
    })
    private String enname;
    @JsonView({
        JsonSubjectViews.In.Get.class,
    })
    private String name;

    public Subject() {
    }

    public Subject(BigDecimal id) {
        this.id = id;
    }

    public BigDecimal getId() {
        return id;
    }

    public void setId(BigDecimal id) {
        this.id = id;
    }

    public String getDename() {
        return dename;
    }

    public void setDename(String dename) {
        this.dename = dename;
    }

    public String getEnname() {
        return enname;
    }

    public void setEnname(String enname) {
        this.enname = enname;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
