package at.tutoringtrain.adminclient.internationalization;

import java.util.Locale;

/**
 * Supported language enumeration
 * @author Marco Wilscher marco.wilscher@edu.htl-villach.at
 */
public enum Language {
    EN(Locale.ENGLISH), 
    DE(Locale.GERMAN);
    
    private final Locale locale;

    private Language(Locale locale) {
        this.locale = locale;
    }

    public Locale getLocale() {
        return locale;
    }
}
