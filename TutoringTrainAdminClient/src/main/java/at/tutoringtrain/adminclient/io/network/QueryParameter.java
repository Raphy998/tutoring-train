package at.tutoringtrain.adminclient.io.network;

/**
 *
 * @author Marco Wilscher marco.wilscher@edu.htl-villach.at
 */
public class QueryParameter {
    private final String key;
    private final String value;
    
    public QueryParameter(String key, String value) {
        if (key == null) {
            this.key = "";
        } else {
            this.key = key;
        }
        if (value == null) {
            this.value = "";
        } else {
            this.value = value;
        }
    }

    public QueryParameter(String key, Object value) {
        this(key, value.toString());
    }
    
    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }
}
