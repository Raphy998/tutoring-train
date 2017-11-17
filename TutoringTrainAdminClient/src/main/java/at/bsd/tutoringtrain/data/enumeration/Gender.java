package at.bsd.tutoringtrain.data.enumeration;

import org.apache.commons.lang.StringUtils;

/**
 *
 * @author Marco Wilscher marco.wilscher@edu.htl-villach.at
 */
public enum Gender {
    MALE('M', "Male"),
    FEMALE('F', "Female"),
    OTHER('N', "Other");
    
    private final Character id;
    private String name;
    
    private Gender(Character id, String defaultName) {
        this.id = id;
        this.name = defaultName;
    }
    
    public Character getId() {
        return id;
    }

    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public static Gender get(Character id) {
        Gender gender;
        if (null == id) {
            gender = OTHER;
        } else {
            switch (id) {
                case 'M':
                    gender = MALE;
                    break;
                case 'F':
                    gender = FEMALE;
                    break;
                default:
                    gender = OTHER;
                    break;
            }
        }
        return gender;
    }

    @Override
    public String toString() {
        return StringUtils.isBlank(name) ? id.toString() : name;
    }  
}
