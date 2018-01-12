package at.tutoringtrain.adminclient.main;

import java.io.IOException;
import java.net.InetAddress;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author Marco Wilscher marco.wilscher@edu.htl-villach.at
 */
public class WebserviceHostFallbackService {
    private static WebserviceHostFallbackService INSTANCE;

    public static WebserviceHostFallbackService getINSTANCE() {
        if (INSTANCE == null) {
            INSTANCE = new WebserviceHostFallbackService();
        }
        return INSTANCE;
    }
    
    private final Logger logger;
    private final ApplicationConfiguration applicationConfiguration;
    private WebserviceHostInfo lastAvailableHost;
    private LocalDateTime lastTimeAvailabilityChecked;
    private boolean checkRequested;
    
    private WebserviceHostFallbackService() {
        this.logger = LogManager.getLogger(this);
        this.applicationConfiguration = ApplicationManager.getInstance().getApplicationConfiguration();
        this.lastTimeAvailabilityChecked = LocalDateTime.MIN;
        this.lastAvailableHost = null;
        this.checkRequested = false;
        this.logger.debug("HostFallbackService initialized");
    }
    
    public synchronized WebserviceHostInfo getAvailableHost() {
        WebserviceHostInfo host = lastAvailableHost;
        boolean reachable;
        if (checkRequested || host == null || Math.abs(ChronoUnit.SECONDS.between(LocalDateTime.now(), lastTimeAvailabilityChecked)) > 15) {
            checkRequested = false;
            logger.debug("HOST AVAILABILITY CHECK");
            if (isHostReachable(applicationConfiguration.getWebserviceHostInfo())) {
                host = applicationConfiguration.getWebserviceHostInfo();
                logger.debug("MAIN HOST REACHABLE");
            } else {
                logger.debug("MAIN HOST UNREACHABLE");
                reachable = false;
                for (int i = 0; i < applicationConfiguration.getWebserviceFallbackHostsInfo().size() && reachable == false; i++) {
                    if (isHostReachable(applicationConfiguration.getWebserviceFallbackHostsInfo().get(i))) {
                        host = applicationConfiguration.getWebserviceFallbackHostsInfo().get(i);
                        reachable = true;
                        logger.debug("FALLBACK HOST " + i + " REACHABLE");
                    } else {
                        reachable = false;
                        logger.debug("FALLBACK HOST " + i + " UNREACHABLE");
                    }      
                }            
            }    
            lastTimeAvailabilityChecked = LocalDateTime.now();
            lastAvailableHost = host;
        }
        return host;
    }
    
    private boolean isHostReachable(WebserviceHostInfo hostInfo) {
        boolean reachable;
        try {
            reachable = InetAddress.getByName(hostInfo.getHost()).isReachable(1000);
        } catch (IOException ex) {
            reachable = false;
        }
        return reachable;
    }

    public synchronized void setCheckRequested(boolean checkRequested) {
        this.checkRequested = checkRequested;
    }
}
