package at.tutoringtrain.adminclient.internationalization;

/**
 *
 * @author Marco Wilscher marco.wilscher@edu.htl-villach.at
 */
public final class StringPlaceholder {
    private final String key;
    private final String value;

    public StringPlaceholder(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return "@" + key;
    }

    public String getValue() {
        return value;
    }
}
