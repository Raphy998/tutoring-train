package edu.tutoringtrain.data.geo;

import edu.tutoringtrain.data.geo.data.LocationObject;
import java.io.IOException;
import java.security.cert.CertificateException;
import java.util.Locale;
import java.util.stream.Stream;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import sun.security.ssl.SSLSocketFactoryImpl;

/**
 *
 * @author Marco Wilscher marco.wilscher@edu.htl-villach.at
 */
public class LocationQuery {
    
    public static void query(LocationQueryParameter param) throws Exception {
        query(param, null);
    }
    
    public static LocationObject query(LocationQueryParameter param, Locale locale) throws Exception {
        OkHttpClient httpClient = configureToIgnoreCertificate(new OkHttpClient.Builder()).build();
        HttpUrl url = HttpUrl.parse("https://nominatim.openstreetmap.org/reverse?format=jsonv2&" + param.toString() + "&addressdetails=1&zoom=17" + (locale != null ? ("&accept-language=" + locale.getLanguage()) : ""));
        Request request = new Request.Builder().url(url).header("User-Agent", "TutoringTrain").build();
        Response r = httpClient.newCall(request).execute();
        
        return DataMapper.fromJson(r.body().string());
    }
    
    private static OkHttpClient.Builder configureToIgnoreCertificate(OkHttpClient.Builder builder) {
        try {

            // Create a trust manager that does not validate certificate chains
            final TrustManager[] trustAllCerts = new TrustManager[] {
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType)
                                throws CertificateException {
                        }

                        @Override
                        public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType)
                                throws CertificateException {
                        }

                        @Override
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return new java.security.cert.X509Certificate[]{};
                        }
                    }
            };

            // Install the all-trusting trust manager
            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            // Create an ssl socket factory with our all-trusting manager
            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

            builder.sslSocketFactory(sslSocketFactory, (X509TrustManager)trustAllCerts[0]);
            builder.hostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return builder;
    }
}
