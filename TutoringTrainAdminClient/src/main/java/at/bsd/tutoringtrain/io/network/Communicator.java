package at.bsd.tutoringtrain.io.network;

import at.bsd.tutoringtrain.data.Database;
import at.bsd.tutoringtrain.data.entities.BlockRequest;
import at.bsd.tutoringtrain.data.entities.User;
import at.bsd.tutoringtrain.data.mapper.DataMapper;
import at.bsd.tutoringtrain.data.mapper.views.JsonUserViews;
import at.bsd.tutoringtrain.debugging.MessageLogger;
import at.bsd.tutoringtrain.io.network.enumeration.HttpMethod;
import at.bsd.tutoringtrain.io.network.listener.user.RequestAllUsersListener;
import at.bsd.tutoringtrain.io.network.listener.user.RequestAuthenticateListner;
import at.bsd.tutoringtrain.io.network.listener.user.RequestBlockUserListener;
import at.bsd.tutoringtrain.io.network.listener.user.RequestOwnUserListener;
import at.bsd.tutoringtrain.io.network.listener.user.RequestRegisterUserListener;
import at.bsd.tutoringtrain.io.network.listener.user.RequestTokenValidationListener;
import at.bsd.tutoringtrain.io.network.listener.user.RequestUnblockUserListener;
import at.bsd.tutoringtrain.io.network.listener.user.RequestUpdateOwnUserListener;
import at.bsd.tutoringtrain.io.network.listener.user.RequestUpdateUserListener;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import okhttp3.Call;
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
 * @author Marco Wilscher marco.wilscher@edu.htl-villach.at
 */
public class Communicator {
    private static Communicator INSTANCE = null; 
    public static Communicator getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Communicator();
        }
        return INSTANCE;
    }
  
    private final MessageLogger logger;
    private final Database db;
    private final OkHttpClient client;
    private final String serverUrl;
    private String username, sessionkey;
    private final MediaType JSON;
    private final Character minimumRequiredRole;
    
    /**
     * 
     */
    private Communicator() {
        //serverUrl = "http://localhost:47546/TutoringTrainWebservice/services/";
        serverUrl = "http://192.168.192.232:51246/TutoringTrainWebservice/services/";   //SANTNER
        minimumRequiredRole = 'A';
        client = new OkHttpClient();
        db = Database.getInstance();
        logger = MessageLogger.getINSTANCE();
        username = "";
        sessionkey = "";
        JSON = MediaType.parse("application/json; charset=utf-8");
        
    }

    /**
     * 
     * @param username 
     */
    private void setUsername(String username) {
        this.username = username;
    }

    /**
     * 
     * @return 
     */
    public String getUsername() {
        return username;
    }
    
    /**
     * 
     */
    public void clearUsername() {
        this.username = "";
    }
    
    /**
     * 
     * @param sessionkey 
     */
    private void setSessionkey(String sessionkey) {
        this.sessionkey = "Bearer " + sessionkey;
    }
    
    /**
     * 
     */
    public void clearSessionkey() {
        this.sessionkey = "";
        db.deleteTokenFile();
    }

    /**
     * 
     * @return 
     */
    public String getSessionkey() {
        return sessionkey;
    }
    
    /**
     * 
     * @return 
     */
    public boolean isSessionkeyAvailable() {
        return !StringUtils.isBlank(getSessionkey());
    }   
    
    /**
     * 
     */
    public void closeSession() {
        clearSessionkey();
        clearUsername();
    }
    
    /***************************************************************************
    * Authentification method
    ***************************************************************************/
    
    /**
     * 
     * @param username
     * @param password
     * @return
     * @throws Exception 
     */
    public RequestResult authenticate(String username, String password) throws Exception {  
        Credentials creds;
        HttpUrl.Builder urlBuilder;
        RequestBody body;
        ObjectMapper mapper;
        Request request;
        Response response;
        RequestResult result;
        
        if (StringUtils.isEmpty(username) || StringUtils.isEmpty(password)) {
            throw new Exception("empty credentials");
        }
        creds = new Credentials(username, DigestUtils.md5Hex(password));
        creds.setRequiredRole(minimumRequiredRole);
        mapper = new ObjectMapper();
        urlBuilder = HttpUrl.parse(serverUrl).newBuilder();
        urlBuilder.addPathSegment("authentication");     
        body = RequestBody.create(JSON, mapper.writeValueAsString(creds));
        request = new Request.Builder().header("Accept-Language", db.getLanguage().getStringValue()).url(urlBuilder.build().toString()).post(body).build();
        response = client.newCall(request).execute();
        
        if (response.code() == 200) {
            setUsername(username);
            setSessionkey(response.body().string());
            result = new RequestResult(200, getSessionkey());
        } else {
            result = new RequestResult(response.code(), response.body().string()); 
        }
        return result;
    }
    
    /**
     * 
     * @param raListener
     * @param credentials
     * @param saveSession
     * @return
     * @throws Exception 
     */
    public boolean requestAuthenticate(RequestAuthenticateListner raListener, Credentials credentials, boolean saveSession) throws Exception {  
         RequestCallback reqCall;  
        if (raListener == null) {
            throw new Exception("RequestAuthenticateListener must not be null");
        }
        if (credentials == null) {
            throw new Exception("Credentials must not be null");
        }
        credentials.setRequiredRole(minimumRequiredRole);
        reqCall = new RequestCallback<RequestAuthenticateListner>(raListener) {  
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.code() == 200) {
                    setUsername(username);
                    setSessionkey(response.body().string());
                    if (saveSession) {
                        try {
                            db.writeTokenFile(getSessionkey());
                        } catch (Exception ex) {
                            logger.exception(ex, getClass());
                        }
                    } else {
                        db.deleteTokenFile();
                    }
                    getListener().requestAuthenticateFinished(new RequestResult(200, getSessionkey()));
                } else {
                    getListener().requestAuthenticateFinished(new RequestResult(response.code(), response.body().string())); 
                }
            }
        };
        return enqueueRequest(HttpMethod.POST, false, "authentication", reqCall, JSON, DataMapper.toJSON(credentials));
    }
    
    public boolean requestTokenValidation(RequestTokenValidationListener rtvListener, String token) throws Exception {  
         RequestCallback reqCall;  
        if (rtvListener == null) {
            throw new Exception("RequestTokenValidationListener must not be null");
        }
        if (StringUtils.isBlank(token)) {
            throw new Exception("Token must not be blank");
        }
        reqCall = new RequestCallback<RequestTokenValidationListener>(rtvListener) {  
            @Override
            public void onResponse(Call call, Response response) throws IOException {           
                if (response.code() == 200) {
                    setSessionkey(StringUtils.remove(token, "Bearer "));
                    getListener().requestTokenValidationFinished(new RequestResult(response.code(), "VALID"));
                } else {
                    getListener().requestTokenValidationFinished(new RequestResult(response.code(), response.body().string())); 
                }
            }
        };
        return enqueueRequest(HttpMethod.POST, true, "authentication/check", reqCall, null, null, token);
    }
    
    /***************************************************************************
    * Request
    ***************************************************************************/
    
    /**
     * 
     * @param method
     * @param authorizationRequired
     * @param pathSegments
     * @param callback
     * @param mediaType
     * @param data
     * @param queryparameters
     * @return 
     * @throws java.lang.Exception 
     */
    private boolean enqueueRequest(HttpMethod method, boolean authorizationRequired, String pathSegments, RequestCallback callback, MediaType mediaType, String data, QueryParameter... queryparameters) throws Exception {
        return enqueueRequest(method, authorizationRequired, pathSegments, callback, mediaType, data, getSessionkey(), queryparameters);
    }
    
    /**
     * 
     * @param method
     * @param authorizationRequired
     * @param pathSegments
     * @param callback
     * @param mediaType
     * @param data
     * @param token
     * @param queryparameters
     * @return
     * @throws Exception 
     */
    private boolean enqueueRequest(HttpMethod method, boolean authorizationRequired, String pathSegments, RequestCallback callback, MediaType mediaType, String data, String token, QueryParameter... queryparameters) throws Exception {
        HttpUrl.Builder urlBuilder;
        Request.Builder requestBuilder;
        Request request;
        boolean isSessionKeyAvailable;
        isSessionKeyAvailable = !StringUtils.isBlank(token);
        if (!authorizationRequired || isSessionKeyAvailable) {    
            urlBuilder = HttpUrl.parse(serverUrl).newBuilder();
            urlBuilder.addPathSegments(pathSegments);
            for (QueryParameter queryparameter : queryparameters) {
                urlBuilder.addQueryParameter(queryparameter.getKey(), queryparameter.getValue());
            }
            requestBuilder = new Request.Builder();
            if (authorizationRequired) {
                requestBuilder.header("Authorization", token);
            }
            requestBuilder.header("Accept-Language", db.getLanguage().getStringValue());
            requestBuilder.url(urlBuilder.build().toString());
            switch (method) {
                case GET:
                    requestBuilder.get();
                    break;
                case POST:
                    requestBuilder.post(RequestBody.create(mediaType, data == null ? "" : data));
                    break;
                case PUT:
                    requestBuilder.put(RequestBody.create(mediaType, data == null ? "" : data));
                    break;
                case DELTE:
                    requestBuilder.delete();
                    break;
            }
            request = requestBuilder.build();
            client.newCall(request).enqueue(callback);
        }
        return authorizationRequired ? isSessionKeyAvailable : true;
    }
    
    /***************************************************************************
    * User methods
    ***************************************************************************/
    
    /**
     * 
     * @param rruListener
     * @param user 
     * @return  
     * @throws java.lang.Exception  
     */
    public boolean requestRegisterUser(RequestRegisterUserListener rruListener, User user) throws Exception {
        RequestCallback reqCall;  
        if (rruListener == null) {
            throw new Exception("RequestRegisterUserListener must not be null");
        }
        if (user == null) {
            throw new Exception("User must not be null");
        }
        reqCall = new RequestCallback<RequestRegisterUserListener>(rruListener) {  
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                getListener().requestRegisterUserFinished(new RequestResult(response.code(), response.body().string()));
            }
        };
        return enqueueRequest(HttpMethod.POST, true, "user/register", reqCall, JSON, DataMapper.toJSON(user, JsonUserViews.Out.Register.class));
    }
    
    /**
     * 
     * @param rouListener
     * @return
     * @throws Exception 
     */
    public boolean requestOwnUser(RequestOwnUserListener rouListener) throws Exception {     
        RequestCallback reqCall;  
        if (rouListener == null) {
            throw new Exception("RequestOwnUserListener must not be null");
        }
        reqCall = new RequestCallback<RequestOwnUserListener>(rouListener) {  
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                getListener().requestOwnUserFinished(new RequestResult(response.code(), response.body().string()));
            }
        };
        return enqueueRequest(HttpMethod.GET, true, "user", reqCall, null, null);
    }
    
    /**
     * 
     * @param rauListener
     * @return 
     * @throws java.lang.Exception 
     */
    public boolean requestAllUsers(RequestAllUsersListener rauListener) throws Exception {
        RequestCallback reqCall;  
        if (rauListener == null) {
            throw new Exception("RequestAllUsersListener must not be null");
        }
        reqCall = new RequestCallback<RequestAllUsersListener>(rauListener) {  
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                getListener().requestAllUsersFinished(new RequestResult(response.code(), response.body().string()));
            }
        };
        return enqueueRequest(HttpMethod.GET, true, "user/all", reqCall, null, null);
    }
    
    /**
     * 
     * @param rauListener
     * @param startIndex
     * @param maxCount 
     * @return  
     * @throws java.lang.Exception 
     */
    public boolean requestAllUsers(RequestAllUsersListener rauListener, int startIndex, int maxCount) throws Exception {
        RequestCallback reqCall;  
        if (rauListener == null) {
            throw new Exception("RequestAllUsersListener must not be null");
        }
        reqCall = new RequestCallback<RequestAllUsersListener>(rauListener) {  
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                getListener().requestAllUsersFinished(new RequestResult(response.code(), response.body().string()));
            }
        };
        return enqueueRequest(HttpMethod.GET, true, "user/all", reqCall, null, null, new QueryParameter("start", Integer.toString(startIndex)), new QueryParameter("pageSize", Integer.toString(maxCount)));
    }

    /**
     * 
     * @param ruuListener
     * @param user 
     * @return  
     * @throws java.lang.Exception 
     */
    public boolean requestUpdateUser(RequestUpdateUserListener ruuListener, User user) throws Exception {
        RequestCallback reqCall;  
        if (ruuListener == null) {
            throw new Exception("RequestUpdateUserListener must not be null");
        }
        if (user == null) {
            throw new Exception("User must not be null");
        }
        reqCall = new RequestCallback<RequestUpdateUserListener>(ruuListener) {  
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                getListener().requestUpdateUserFinished(new RequestResult(response.code(), response.body().string()));
            }
        };
        return enqueueRequest(HttpMethod.PUT, true, "user/update", reqCall, JSON, DataMapper.toJSON(user, JsonUserViews.Out.Update.class));
    }
    
    /**
     * 
     * @param ruouListener
     * @return 
     * @throws java.lang.Exception
     */
    public boolean requestUpdateOwnUser(RequestUpdateOwnUserListener ruouListener) throws Exception {
        RequestCallback reqCall;  
        if (ruouListener == null) {
            throw new Exception("RequestUpdateOwnUserListener must not be null");
        }
        reqCall = new RequestCallback<RequestUpdateOwnUserListener>(ruouListener) {  
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                getListener().requestUpdateOwnUserFinished(new RequestResult(response.code(), response.body().string()));
            }
        };
        return enqueueRequest(HttpMethod.PUT, true, "user/update/own", reqCall, JSON, DataMapper.toJSON(db.getCurrentUser(), JsonUserViews.Out.UpdateOwn.class));
    }
    
    /**
     * 
     * @param rbuListener
     * @param blockRequest 
     * @return  
     * @throws java.lang.Exception 
     */
    public boolean requestBlockUser(RequestBlockUserListener rbuListener, BlockRequest blockRequest) throws Exception {
        RequestCallback reqCall;  
        if (rbuListener == null) {
            throw new Exception("RequestBlockUserListener must not be null");
        }
        if (blockRequest == null) {
            throw new Exception("BlockRequest must not be null");
        }
        reqCall = new RequestCallback<RequestBlockUserListener>(rbuListener) {  
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                getListener().requestBlockUserFinished(new RequestResult(response.code(), response.body().string()));
            }
        };
        return enqueueRequest(HttpMethod.POST, true, "user/block", reqCall, JSON, DataMapper.toJSON(blockRequest));
    }
    
    /**
     * 
     * @param ruuListener
     * @param username 
     * @return  
     * @throws java.lang.Exception  
     */
    public boolean requestUnblockUser(RequestUnblockUserListener ruuListener, String username) throws Exception {
        RequestCallback reqCall;  
        if (ruuListener == null) {
            throw new Exception("RequestUnblockUserListener must not be null");
        }
        if (StringUtils.isEmpty(username)) {
            throw new Exception("Username must not be empty");
        }
        reqCall = new RequestCallback<RequestUnblockUserListener>(ruuListener) {  
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                getListener().requestUnblockUserFinished(new RequestResult(response.code(), response.body().string()));
            }
        };
        return enqueueRequest(HttpMethod.GET, true, "user/unblock/" + username, reqCall, null, null);
    }
    
    /***************************************************************************
     * Offer methods 
     **************************************************************************/
    
    /*
    public void createOffer(Offer offer) {
        
    } 
    public void updateOffer(Offer offer) {
        
    }
    public void getNewestOffers(int startIndex, int maxCount) {
        
    }
    public void getNewestOffers(User user, int startIndex, int maxCount) {
        
    }
    */
    
    /***************************************************************************
     * Subject methods
     **************************************************************************/
    
    /*
    public void createSubject(Subject subject) {
        
    }
    public void updateSubject(Subject subject) {
        
    }
    public void deleteSubject(Subject subject) {
        
    }
    public void getSubjects() {
        
    }
    */
} 
