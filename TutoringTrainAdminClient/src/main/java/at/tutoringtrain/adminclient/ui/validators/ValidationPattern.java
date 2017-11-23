package at.tutoringtrain.adminclient.ui.validators;

/**
 *
 * @author Marco Wilscher marco.wilscher@edu.htl-villach.at
 */
public class ValidationPattern {
    private final String pattern;
    private final String messageKey;

    public ValidationPattern(String pattern, String messageKey) {
        this.pattern = pattern;
        this.messageKey = messageKey;
    }

    public String getMessageKey() {
        return messageKey;
    }

    public String getPattern() {
        return pattern;
    }
}
