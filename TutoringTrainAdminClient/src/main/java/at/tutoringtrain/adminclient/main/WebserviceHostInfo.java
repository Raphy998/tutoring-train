package at.tutoringtrain.adminclient.main;

/**
 *
 * @author Marco Wilscher marco.wilscher@edu.htl-villach.at
 */
public class WebserviceHostInfo  {
    private String host;
    private int port;

    public WebserviceHostInfo() {
    }

    public WebserviceHostInfo(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getHost() {
        return host;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getPort() {
        return port;
    }

    @Override
    public String toString() {
        return host + ":" + port;
    }
}
