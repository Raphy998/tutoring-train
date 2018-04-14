package at.tutoringtrain.adminclient.main;

import java.io.IOException;
import java.net.InetAddress;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
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
    private final OkHttpClient httpClient;
    
    private WebserviceHostFallbackService() {
        this.logger = LogManager.getLogger(this);
        this.applicationConfiguration = ApplicationManager.getInstance().getApplicationConfiguration();
        this.lastTimeAvailabilityChecked = LocalDateTime.MIN;
        this.lastAvailableHost = null;
        this.checkRequested = false;
        this.httpClient = new OkHttpClient.Builder().connectTimeout(1000, TimeUnit.MILLISECONDS).readTimeout(1000, TimeUnit.MILLISECONDS).build();
        this.logger.debug("HostFallbackService initialized");
    }
    
    public synchronized WebserviceHostInfo getAvailableHost() {
        WebserviceHostInfo host = lastAvailableHost;
        if (checkRequested || host == null /* || Math.abs(ChronoUnit.SECONDS.between(LocalDateTime.now(), lastTimeAvailabilityChecked)) > 60 */) {
            checkRequested = false;      
            logger.debug("HOST AVAILABILITY CHECK");
            if (isHostReachable(applicationConfiguration.getWebserviceHostInfo())) {               
                logger.debug("MAIN HOST REACHABLE");      
                if (isWebServiceDeployed(applicationConfiguration.getWebserviceHostInfo())) {
                    logger.debug("MAIN HOST WEBSERVICE DEPLOYED");
                    host = applicationConfiguration.getWebserviceHostInfo();
                } else {
                    logger.debug("MAIN HOST WEBSERVICE NOT DEPLOYED");
                    host = getAvailableFallbackHost();   
                }
            } else {
                logger.debug("MAIN HOST UNREACHABLE");
                host = getAvailableFallbackHost();           
            }    
            lastTimeAvailabilityChecked = LocalDateTime.now();
            lastAvailableHost = host;
        }
        return host;
    }
    
    private boolean isHostReachable(WebserviceHostInfo host) {
        boolean reachable;
        try {
            reachable = InetAddress.getByName(host.getHost()).isReachable(1000);
        } catch (IOException ex) {
            reachable = false;
        }
        return reachable;
    }
    
    private boolean isWebServiceDeployed(WebserviceHostInfo host) {
        boolean reachable;     
        try {
            String url = ApplicationManager.getDefaultValueProvider().getDefaultWebServiceProtokoll() + "://" + host.getHost() + ":" + host.getPort() + ApplicationManager.getDefaultValueProvider().getDefaultWebServiceContextPath();
            Request request = new Request.Builder().url(url).build();
            try (Response response = httpClient.newCall(request).execute()) {
                reachable = response.isSuccessful();
            }
        } catch (IOException ex) {
            reachable = false;
        }
        return reachable;
    }
    
    private WebserviceHostInfo getAvailableFallbackHost() {
        WebserviceHostInfo host = null;
        boolean reachable = false;
        for (int i = 0; i < applicationConfiguration.getWebserviceFallbackHostsInfo().size() && reachable == false; i++) {
            if (isHostReachable(applicationConfiguration.getWebserviceFallbackHostsInfo().get(i))) {
                logger.debug("FALLBACK HOST " + i + " REACHABLE");
                if (isWebServiceDeployed(applicationConfiguration.getWebserviceFallbackHostsInfo().get(i))) {
                    logger.debug("FALLBACK HOST " + i + " WEBSERVICE DEPLOYED");
                    host = applicationConfiguration.getWebserviceFallbackHostsInfo().get(i);
                    reachable = true;
                } else {
                    logger.debug("FALLBACK HOST " + i + " WEBSERVICE NOT DEPLOYED");
                    reachable = false;
                }          
            } else {
                reachable = false;
                logger.debug("FALLBACK HOST " + i + " UNREACHABLE");
            }      
        }    
        return host;
    }

    public synchronized void requestCheck() {
        this.checkRequested = true;
    }
}
