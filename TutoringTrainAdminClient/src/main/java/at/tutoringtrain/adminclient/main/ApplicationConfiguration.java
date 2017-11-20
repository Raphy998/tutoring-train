package at.tutoringtrain.adminclient.main;

import at.tutoringtrain.adminclient.internationalization.Language;
import org.apache.commons.lang.StringUtils;
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
    private String serverIp;
    private int serverPort;

    public ApplicationConfiguration() {
        this.logger = LogManager.getLogger(this);
        this.defaultValueProvider = DefaultValueProvider.getINSTANCE();      
        this.language = defaultValueProvider.getDefaultLanguage();
        this.serverIp = defaultValueProvider.getDefaultWebServiceIpAddress();
        this.serverPort = defaultValueProvider.getDefaultWebServicePort();
        this.logger.debug("ApplicationConfiguration initialized");
    }

    public ApplicationConfiguration(Language language) {
        this.logger = LogManager.getLogger(this);
        this.defaultValueProvider = DefaultValueProvider.getINSTANCE();      
        this.language = language;
        this.serverIp = defaultValueProvider.getDefaultWebServiceIpAddress();
        this.serverPort = defaultValueProvider.getDefaultWebServicePort();
        this.logger.debug("ApplicationConfiguration initialized");    
    }

    public Language getLanguage() {
        return language;
    }

    public void setLanguage(Language language) {
        this.language = language;
    }

    public String getServerIp() {
        return serverIp;
    }

    public void setServerIp(String serverIp) {
        if (StringUtils.isBlank(serverIp)) {
            this.serverIp = defaultValueProvider.getDefaultWebServiceIpAddress();
        } else {
            this.serverIp = serverIp;
        }
    }
    
    public int getServerPort() {
        return serverPort;
    }

    public void setServerPort(int serverPort) {
        if (serverPort >= 0) {
            this.serverPort = serverPort;
        }
    }
    
    public boolean apply(ApplicationConfiguration config) {
        boolean success = false;
        if (config != null) {
            setLanguage(config.getLanguage());
            setServerPort(config.getServerPort());
            setServerIp(config.getServerIp());
            success = true;
        }
        return success;
    }
}