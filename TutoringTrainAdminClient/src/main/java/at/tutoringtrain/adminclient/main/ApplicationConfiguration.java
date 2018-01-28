package at.tutoringtrain.adminclient.main;

import at.tutoringtrain.adminclient.internationalization.Language;
import java.util.ArrayList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Application configuration
 * @author Marco Wilscher marco.wilscher@edu.htl-villach.at
 */
public final class ApplicationConfiguration {
    private final Logger logger;
    private final DefaultValueProvider defaultValueProvider;
    
    private Language language;
    private WebserviceHostInfo webserviceHostInfo;
    private ArrayList<WebserviceHostInfo> webserviceFallbackHostsInfo;

    public ApplicationConfiguration() {
        this.logger = LogManager.getLogger(this);
        this.defaultValueProvider = DefaultValueProvider.getINSTANCE();      
        this.language = defaultValueProvider.getDefaultLanguage();
        this.webserviceHostInfo = defaultValueProvider.getDefaultWebserviceHostInfo();
        this.webserviceFallbackHostsInfo = defaultValueProvider.getDefaultWebserviceHostFallbacks();
        this.logger.debug("ApplicationConfiguration initialized");
    }

    public ApplicationConfiguration(Language language) {
        this.logger = LogManager.getLogger(this);
        this.defaultValueProvider = DefaultValueProvider.getINSTANCE();      
        this.language = language;
        this.webserviceHostInfo = defaultValueProvider.getDefaultWebserviceHostInfo();
        this.webserviceFallbackHostsInfo = defaultValueProvider.getDefaultWebserviceHostFallbacks();
        this.logger.debug("ApplicationConfiguration initialized");    
    }

    public Language getLanguage() {
        return language;
    }

    public void setLanguage(Language language) {
        this.language = language;
        logger.debug("language set to " + this.language.getLocale());
    }

    public WebserviceHostInfo getWebserviceHostInfo() {
        return webserviceHostInfo;
    }

    public ArrayList<WebserviceHostInfo> getWebserviceFallbackHostsInfo() {
        return webserviceFallbackHostsInfo;
    }

    public void setWebserviceHostInfo(WebserviceHostInfo webserviceHostInfo) {
        this.webserviceHostInfo = webserviceHostInfo;
    }

    private void setWebserviceFallbackHostsInfo(ArrayList<WebserviceHostInfo> webserviceFallbackHostsInfo) {
        this.webserviceFallbackHostsInfo = webserviceFallbackHostsInfo;
    }
    
    public void addWebserviceFallbackHost(WebserviceHostInfo fallbackHostInfo) {
        webserviceFallbackHostsInfo.add(fallbackHostInfo);
    }
    
    public boolean apply(ApplicationConfiguration config) {
        boolean success = false;
        if (config != null) {
            setLanguage(config.getLanguage());
            setWebserviceHostInfo(config.getWebserviceHostInfo());
            setWebserviceFallbackHostsInfo(config.getWebserviceFallbackHostsInfo());
            success = true;
        }
        return success;
    }

    @Override
    public String toString() {
        return "ApplicationConfiguration{" + "language=" + language + ", webserviceHostInfo=" + webserviceHostInfo + ", webserviceFallbackHostsInfo=" + webserviceFallbackHostsInfo + '}';
    }
}