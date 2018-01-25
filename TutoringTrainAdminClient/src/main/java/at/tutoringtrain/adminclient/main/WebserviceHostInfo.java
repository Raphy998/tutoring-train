package at.tutoringtrain.adminclient.main;

/**
 *
 * @author Marco Wilscher marco.wilscher@edu.htl-villach.at
 */
public class WebserviceHostInfo  {
    private final String host;
    private final int port;

    public WebserviceHostInfo(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    @Override
    public String toString() {
        return "WebserviceHostInfo {" + "host=" + host + ", port=" + port + '}';
    }
}
