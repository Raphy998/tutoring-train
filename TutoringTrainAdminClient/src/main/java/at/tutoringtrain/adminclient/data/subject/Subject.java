package at.tutoringtrain.adminclient.data.subject;

import at.tutoringtrain.adminclient.data.mapper.DataMappingViews;
import com.fasterxml.jackson.annotation.JsonView;
import java.math.BigDecimal;

/**
 * 
 * @author Marco Wilscher marco.wilscher@edu.htl-villach.at
 */
public class Subject {
    @JsonView({
        DataMappingViews.Subject.In.Register.class,
        DataMappingViews.Subject.In.Get.class,
        DataMappingViews.Subject.Out.Update.class,
        DataMappingViews.Subject.Out.UpdateState.class,
        DataMappingViews.Entry.In.Get.class
    })
    private BigDecimal id;
    @JsonView({
        DataMappingViews.Subject.In.Get.class,
        DataMappingViews.Subject.Out.Register.class,
        DataMappingViews.Subject.Out.Update.class
    })
    private String dename;
    @JsonView({
        DataMappingViews.Subject.In.Get.class,
        DataMappingViews.Subject.Out.Register.class,
        DataMappingViews.Subject.Out.Update.class
    })
    private String enname;
    @JsonView({
        DataMappingViews.Subject.In.Get.class,
        DataMappingViews.Entry.In.Get.class
    })
    private String name;
    @JsonView({
        DataMappingViews.Subject.In.Get.class,
        DataMappingViews.Subject.Out.Update.class,
        DataMappingViews.Subject.Out.UpdateState.class,
        DataMappingViews.Entry.In.Get.class
    })
    private boolean isactive;

    public Subject() {
    }

    public Subject(BigDecimal id) {
        this.id = id;
    }

    public Subject(String enName, String deName) {
        this.enname = enName;
        this.dename = deName;
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

    public boolean isIsactive() {
        return isactive;
    }

    public void setIsactive(boolean isactive) {
        this.isactive = isactive;
    }

    @Override
    public String toString() {
        return "Subject{" + "id=" + id + ", dename=" + dename + ", enname=" + enname + ", name=" + name + ", isactive=" + isactive + '}';
    }
}
