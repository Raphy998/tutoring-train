package at.tutoringtrain.adminclient.internationalization;

import at.tutoringtrain.adminclient.main.ApplicationManager;
import java.util.Locale;
import java.util.ResourceBundle;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author Marco Wilscher marco.wilscher@edu.htl-villach.at
 */
public final class LocalizedValueProvider {
    private static LocalizedValueProvider INSTANCE;

    public static LocalizedValueProvider getINSTANCE() {
        if (INSTANCE == null) {
            INSTANCE = new LocalizedValueProvider();
        }
        return INSTANCE;
    }
    
    private final Logger logger;
    private Locale locale;
    private ResourceBundle resourceBundle;
    
    private LocalizedValueProvider() {
        this.logger = LogManager.getLogger(this);
        loadLocale();
        this.logger.debug("LocalizedValueProvider initialized");
    }
    
    public void loadLocale() {
        locale = ApplicationManager.getInstance().getApplicationConfiguration().getLanguage().getLocale(); 
        resourceBundle = ResourceBundle.getBundle("bundles.LanguageBundle", locale);
        logger.debug("Loaded locale: " + locale.toString());
    }
    
    public String getString(String key) {
        return resourceBundle.getString(key);
    }
    
    public String getString(String key, StringPlaceholder... placeholders) {
        String string = getString(key);
        for (StringPlaceholder placeholder : placeholders) {
            string = string.replaceAll(placeholder.getKey(), placeholder.getValue());
        }
        return string;
    }
}
