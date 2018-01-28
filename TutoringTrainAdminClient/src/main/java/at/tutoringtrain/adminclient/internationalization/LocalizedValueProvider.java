package at.tutoringtrain.adminclient.internationalization;

import at.tutoringtrain.adminclient.main.ApplicationManager;
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
    private final ApplicationManager applicationManager;
    private ResourceBundle resourceBundle;
    
    private LocalizedValueProvider() {
        this.logger = LogManager.getLogger(this);
        this.applicationManager = ApplicationManager.getInstance();
        loadResourceBundle();
        this.logger.debug("LocalizedValueProvider initialized");
    }
    
    public void loadResourceBundle() {
        resourceBundle = applicationManager.getLanguageResourceBundle();
        logger.debug("Loaded locale: " + resourceBundle.getLocale().toString());
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
