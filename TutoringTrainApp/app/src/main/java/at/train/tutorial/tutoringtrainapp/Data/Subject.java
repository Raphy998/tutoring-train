package at.train.tutorial.tutoringtrainapp.Data;

import com.fasterxml.jackson.annotation.JsonView;

/**
 * Created by Moe on 12.01.2018.
 */

public class Subject {
    @JsonView({Views.Entry.In.loadNewest.class})
    private int id;
    @JsonView({Views.Entry.In.loadNewest.class})
    private String name;
    @JsonView({Views.Entry.In.loadNewest.class})
    private boolean isActive;
    @JsonView({Views.Entry.In.loadNewest.class})
    private String dename;
    @JsonView({Views.Entry.In.loadNewest.class})
    private String enname;

    @Override
    public String toString() {
        return "++++++++++++++++++++++++++++++++++++++++++++++++ " + id + " " + dename;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public boolean isActive() {
        return isActive;
    }

    public String getDename() {
        return dename;
    }

    public String getEnname() {
        return enname;
    }
}
