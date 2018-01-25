package at.tutoringtrain.adminclient.data.user;

/**
 *
 * @author Marco Wilscher marco.wilscher@edu.htl-villach.at
 */
public class Gender {
    private String code;
    private String name;

    public Gender() {
    }

    public Gender(String code, String name) {
        this.code = code;
        this.name = name;
    }
    
    public Character getCode() {
        return code != null ? code.charAt(0) : ' ';
    }

    public void setCode(String code) {
        this.code = code;
    }
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return getName();
    }
}
