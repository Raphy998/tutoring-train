package at.bsd.tutoringtrain.network;

import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;

/**
 *
 * @author Marco Wilscher <marco.wilscher@edu.htl-villach.at>
 */
public class Communicator {
    private static Communicator INSTANCE = null;
    
    public static Communicator getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Communicator();
        }
        return INSTANCE;
    }
    
    private final OkHttpClient client;
    private final String serverURL;
    private String username, sessionkey;
    private final MediaType JSON;
    
    private Communicator() {
        serverURL = "http://192.168.194.147:51246/TutoringTrainWebservice/services/";
        client = new OkHttpClient();
        username = "";
        sessionkey = "";
        JSON = MediaType.parse("application/json; charset=utf-8");
        
    }

    private void setUsername(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }
    
    private void setSessionkey(String sessionkey) {
        this.sessionkey = "Bearer " + sessionkey;
    }

    public String getSessionkey() {
        return sessionkey;
    }
    
    public boolean isAuthenticated() {
        return !StringUtils.isEmpty(getSessionkey());
    }
    
    public void logoff() {
        this.sessionkey = "";
    }
    
    public AuthenticationResult authenticate(String username, String password) throws Exception {  
        Credentials creds;
        HttpUrl.Builder urlBuilder;
        RequestBody body;
        ObjectMapper mapper;
        Request request;
        Response response;
        AuthenticationResult authresult;
        
        if (StringUtils.isEmpty(username) || StringUtils.isEmpty(password)) {
            throw new Exception("authentification failed due to empty credentials");
        }
        authresult = AuthenticationResult.FORBIDDEN;
        creds = new Credentials(username, DigestUtils.md5Hex(password));
        mapper = new ObjectMapper();
        urlBuilder = HttpUrl.parse(serverURL).newBuilder();
        urlBuilder.addPathSegment("authentication");     
        body = RequestBody.create(JSON, mapper.writeValueAsString(creds));
        request = new Request.Builder().url(urlBuilder.build().toString()).post(body).build();
        response = client.newCall(request).execute();
        switch (response.code()) {
            case 200:
                authresult = AuthenticationResult.ALLOWED;
                break;
            case 403:
                authresult = AuthenticationResult.FORBIDDEN;
                break;
            case 401:
                authresult = AuthenticationResult.UNAUTHORIZED;
                break;
            default:
                authresult = null;
                break;
        }
        if (authresult == AuthenticationResult.ALLOWED)
            setSessionkey(response.body().string());
        return authresult;
    }
}
