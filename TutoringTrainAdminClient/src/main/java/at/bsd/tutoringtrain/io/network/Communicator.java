package at.bsd.tutoringtrain.io.network;

import at.bsd.tutoringtrain.data.Database;
import at.bsd.tutoringtrain.data.entities.BlockRequest;
import at.bsd.tutoringtrain.data.entities.Subject;
import at.bsd.tutoringtrain.data.entities.User;
import at.bsd.tutoringtrain.data.mapper.DataMapper;
import at.bsd.tutoringtrain.data.mapper.views.JsonSubjectViews;
import at.bsd.tutoringtrain.data.mapper.views.JsonUserViews;
import at.bsd.tutoringtrain.debugging.MessageLogger;
import at.bsd.tutoringtrain.io.network.enumeration.HttpMethod;
import at.bsd.tutoringtrain.io.network.enumeration.UserRole;
import at.bsd.tutoringtrain.io.network.listener.subject.RequestDeleteSubjectListener;
import at.bsd.tutoringtrain.io.network.listener.subject.RequestGetAllSubjectsListener;
import at.bsd.tutoringtrain.io.network.listener.subject.RequestRegisterSubjectListener;
import at.bsd.tutoringtrain.io.network.listener.subject.RequestSubjectCountListener;
import at.bsd.tutoringtrain.io.network.listener.subject.RequestUpdateSubjectListener;
import at.bsd.tutoringtrain.io.network.listener.user.RequestAllUsersListener;
import at.bsd.tutoringtrain.io.network.listener.user.RequestAuthenticateListner;
import at.bsd.tutoringtrain.io.network.listener.user.RequestBlockUserListener;
import at.bsd.tutoringtrain.io.network.listener.user.RequestGetGendersListener;
import at.bsd.tutoringtrain.io.network.listener.user.RequestOwnUserListener;
import at.bsd.tutoringtrain.io.network.listener.user.RequestRegisterUserListener;
import at.bsd.tutoringtrain.io.network.listener.user.RequestTokenValidationListener;
import at.bsd.tutoringtrain.io.network.listener.user.RequestUnblockUserListener;
import at.bsd.tutoringtrain.io.network.listener.user.RequestUpdateOwnUserListener;
import at.bsd.tutoringtrain.io.network.listener.user.RequestUpdateUserListener;
import at.bsd.tutoringtrain.io.network.listener.user.RequestUserCountListener;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
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
  
    private final static String WEBSERVICE_URL = "http://localhost:47546/TutoringTrainWebservice/services/";
    //private final static String WEBSERVICE_URL = "http://192.168.192.232:51246/TutoringTrainWebservice/services/";   //SANTNER
    private final static UserRole MINIMUM_REQUIRED_ROLL = UserRole.ADMIN;
    private final static MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    
    private final OkHttpClient client;
    private final MessageLogger logger;
    private final Database db;
    private String currentUsername, sessionkey;
    
    private Communicator() {
        OkHttpClient.Builder clientBuilder;
        clientBuilder = new OkHttpClient.Builder();
        clientBuilder.connectTimeout(10, TimeUnit.SECONDS);
        clientBuilder.readTimeout(10, TimeUnit.SECONDS);
        clientBuilder.writeTimeout(10, TimeUnit.SECONDS);
        client = clientBuilder.build();
        db = Database.getInstance();
        logger = MessageLogger.getINSTANCE();
        currentUsername = "";
        sessionkey = "";    
    }

    /**
     * Sets the username of the currently logged in user
     * @param username username of the currently logged in user
     */
    private void setCurrentUsername(String username) {
        this.currentUsername = username;
    }

    /**
     * Returns the username of the currently logged in user
     * @return username
     */
    public String getCurrentUsername() {
        return currentUsername;
    }
    
    /**
     * Removes the currently logged in username
     */
    public void clearUsername() {
        this.currentUsername = "";
    }
    
    /**
     * Sets the Bearer token
     * @param sessionkey token hash
     */
    private void setSessionkey(String sessionkey) {
        this.sessionkey = "Bearer " + sessionkey;
    }
    
    /**
     *  Deletes the session key and the session key file on the disk
     */
    public void clearSessionkey() {
        this.sessionkey = "";
        db.deleteTokenFile();
    }

    /**
     * Returns the entire Bearer token
     * @return Bearer token
     */
    public String getSessionkey() {
        return sessionkey;
    }
    
    /**
     * Checks if session key is set
     * @return state
     */
    public boolean isSessionkeyAvailable() {
        return !StringUtils.isBlank(getSessionkey());
    }   
    
    /**
     * Closes the current session
     */
    public void closeSession() {
        clearSessionkey();
        clearUsername();
    }
     
    /**
     * Authenticates the user using the username and password (synchronous)
     * @param username username
     * @param password plain password
     * @return request result
     * @throws Exception 
     */
    public RequestResult authenticate(String username, String password) throws Exception {  
        Credentials credentials;
        HttpUrl.Builder urlBuilder;
        RequestBody body;
        Request request;
        Response response;
        RequestResult result;
        if (StringUtils.isEmpty(username) || StringUtils.isEmpty(password)) {
            throw new Exception("empty credentials");
        }
        credentials = new Credentials(username, DigestUtils.md5Hex(password));
        credentials.setRequiredRole(MINIMUM_REQUIRED_ROLL.getValue());
        urlBuilder = HttpUrl.parse(WEBSERVICE_URL).newBuilder();
        urlBuilder.addPathSegment("authentication");     
        body = RequestBody.create(JSON, DataMapper.toJSON(credentials));
        request = new Request.Builder().header("Accept-Language", db.getLanguage().getStringValue()).url(urlBuilder.build().toString()).post(body).build();
        response = client.newCall(request).execute();
        if (response.code() == 200) {
            setCurrentUsername(username);
            setSessionkey(response.body().string());
            result = new RequestResult(200, getSessionkey());
        } else {
            result = new RequestResult(response.code(), response.body().string()); 
        }
        return result;
    }
    
    /**
     * Authenticates the user using a credential object
     * @param raListener result listener
     * @param credentials user credentials
     * @param saveSession save session locally 
     * @throws Exception 
     */
    public void requestAuthenticate(RequestAuthenticateListner raListener, Credentials credentials, boolean saveSession) throws Exception {  
        RequestCallback requestCallback;  
        if (raListener == null) {
            throw new Exception("RequestAuthenticateListener must not be null");
        }
        if (credentials == null) {
            throw new Exception("Credentials must not be null");
        }
        credentials.setRequiredRole(MINIMUM_REQUIRED_ROLL.getValue());
        requestCallback = new RequestCallback<RequestAuthenticateListner>(raListener) {  
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.code() == 200) {
                    setCurrentUsername(currentUsername);
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
                    getListener().requestAuthenticateFinished(new RequestResult(response.code(), "OK"));
                } else {
                    getListener().requestAuthenticateFinished(new RequestResult(response.code(), response.body().string())); 
                }
            }
        };
        enqueueRequest(HttpMethod.POST, false, "authentication", requestCallback, JSON, DataMapper.toJSON(credentials));
    }
    
    /**
     * Validates an existing token
     * @param rtvListener result listner
     * @param token token to validate 
     * @throws Exception 
     */
    public void requestTokenValidation(RequestTokenValidationListener rtvListener, String token) throws Exception {  
         RequestCallback requestCallback;  
        if (rtvListener == null) {
            throw new Exception("RequestTokenValidationListener must not be null");
        }
        if (StringUtils.isBlank(token)) {
            throw new Exception("Token must not be blank");
        }
        requestCallback = new RequestCallback<RequestTokenValidationListener>(rtvListener) {  
            @Override
            public void onResponse(Call call, Response response) throws IOException {           
                if (response.code() == 200) {
                    setCurrentUsername("");
                    setSessionkey(StringUtils.remove(token, "Bearer "));
                    getListener().requestTokenValidationFinished(new RequestResult(response.code(), "VALID"));
                } else {
                    getListener().requestTokenValidationFinished(new RequestResult(response.code(), response.body().string())); 
                }
            }
        };
        enqueueRequest(HttpMethod.POST, true, "authentication/check", requestCallback, null, null, token);
    }
       
    /**
     * Enqueues webservice request
     * @param method http method
     * @param authorizationRequired is authorization header required
     * @param pathSegments url path segments
     * @param callback request callback
     * @param mediaType data media type
     * @param data data
     * @param queryparameters query parameters
     * @return true if requets was successfully enqueued
     * @throws java.lang.Exception 
     */
    private boolean enqueueRequest(HttpMethod method, boolean authorizationRequired, String pathSegments, RequestCallback callback, MediaType mediaType, String data, QueryParameter... queryparameters) throws Exception {
        return enqueueRequest(method, authorizationRequired, pathSegments, callback, mediaType, data, getSessionkey(), queryparameters);
    }
    
    /**
     * Enqueues webservice request
     * @param method http method
     * @param authorizationRequired is authorization header required
     * @param pathSegments url path segments
     * @param callback request callback
     * @param mediaType data media type
     * @param data data
     * @param token bearer token
     * @param queryparameters query parameters
     * @return true if requets was successfully enqueued
     * @throws Exception 
     */
    private boolean enqueueRequest(HttpMethod method, boolean authorizationRequired, String pathSegments, RequestCallback callback, MediaType mediaType, String data, String token, QueryParameter... queryparameters) throws Exception {
        HttpUrl.Builder urlBuilder;
        Request.Builder requestBuilder;
        Request request;
        boolean isSessionKeyAvailable;
        isSessionKeyAvailable = !StringUtils.isBlank(token);
        if (!authorizationRequired || isSessionKeyAvailable) {    
            urlBuilder = HttpUrl.parse(WEBSERVICE_URL).newBuilder();
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
    
    /**
     * Register user
     * @param rruListener result listener
     * @param user user to register
     * @return true if request was successfully enqueued
     * @throws java.lang.Exception  
     */
    public boolean requestRegisterUser(RequestRegisterUserListener rruListener, User user) throws Exception {
        RequestCallback requestCallback;  
        if (rruListener == null) {
            throw new Exception("RequestRegisterUserListener must not be null");
        }
        if (user == null) {
            throw new Exception("User must not be null");
        }
        requestCallback = new RequestCallback<RequestRegisterUserListener>(rruListener) {  
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                getListener().requestRegisterUserFinished(new RequestResult(response.code(), response.body().string()));
            }
        };
        return enqueueRequest(HttpMethod.POST, true, "user/register", requestCallback, JSON, DataMapper.toJSON(user, JsonUserViews.Out.Register.class));
    }
    
    /**
     * Get own user
     * @param rouListener result listener
     * @return true if request was successfully enqueued
     * @throws Exception 
     */
    public boolean requestOwnUser(RequestOwnUserListener rouListener) throws Exception {     
        RequestCallback requestCallback;  
        if (rouListener == null) {
            throw new Exception("RequestOwnUserListener must not be null");
        }
        requestCallback = new RequestCallback<RequestOwnUserListener>(rouListener) {  
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                getListener().requestOwnUserFinished(new RequestResult(response.code(), response.body().string()));
            }
        };
        return enqueueRequest(HttpMethod.GET, true, "user", requestCallback, null, null);
    }
    
    /**
     * Get all users
     * @param rauListener result listner
     * @return true if request was successfully enqueued
     * @throws java.lang.Exception 
     */
    public boolean requestAllUsers(RequestAllUsersListener rauListener) throws Exception {
        RequestCallback requestCallback;  
        if (rauListener == null) {
            throw new Exception("RequestAllUsersListener must not be null");
        }
        requestCallback = new RequestCallback<RequestAllUsersListener>(rauListener) {  
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                getListener().requestAllUsersFinished(new RequestResult(response.code(), response.body().string()));
            }
        };
        return enqueueRequest(HttpMethod.GET, true, "user/all", requestCallback, null, null);
    }
    
    /**
     * Get all users within a specified border
     * @param rauListener result listener
     * @param startIndex start index
     * @param maxCount maximum count
     * @return true if request was successfully enqueued
     * @throws java.lang.Exception 
     */
    public boolean requestAllUsers(RequestAllUsersListener rauListener, int startIndex, int maxCount) throws Exception {
        RequestCallback requestCallback;  
        if (rauListener == null) {
            throw new Exception("RequestAllUsersListener must not be null");
        }
        requestCallback = new RequestCallback<RequestAllUsersListener>(rauListener) {  
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                getListener().requestAllUsersFinished(new RequestResult(response.code(), response.body().string()));
            }
        };
        return enqueueRequest(HttpMethod.GET, true, "user/all", requestCallback, null, null, new QueryParameter("start", Integer.toString(startIndex)), new QueryParameter("pageSize", Integer.toString(maxCount)));
    }

    /**
     * Get user count
     * @param rucListener result listener
     * @return true if request was successfully enqueued
     * @throws Exception 
     */
    public boolean requestUserCount(RequestUserCountListener rucListener) throws Exception {
        RequestCallback requestCallback;  
        if (rucListener == null) {
            throw new Exception("RequestUserCountListener must not be null");
        }
        requestCallback = new RequestCallback<RequestUserCountListener>(rucListener) {  
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                getListener().requestUserCountFinished(new RequestResult(response.code(), response.body().string()));
            }
        };
        return enqueueRequest(HttpMethod.GET, true, "user/count/", requestCallback, null, null);
    }
    
    /**
     * Update user
     * @param ruuListener result listener
     * @param user user to update
     * @return true if request was successfully enqueued
     * @throws java.lang.Exception 
     */
    public boolean requestUpdateUser(RequestUpdateUserListener ruuListener, User user) throws Exception {
        RequestCallback requestCallback;  
        if (ruuListener == null) {
            throw new Exception("RequestUpdateUserListener must not be null");
        }
        if (user == null) {
            throw new Exception("User must not be null");
        }
        requestCallback = new RequestCallback<RequestUpdateUserListener>(ruuListener) {  
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                getListener().requestUpdateUserFinished(new RequestResult(response.code(), response.body().string()));
            }
        };
        return enqueueRequest(HttpMethod.PUT, true, "user/update", requestCallback, JSON, DataMapper.toJSON(user, JsonUserViews.Out.Update.class));
    }
    
    /**
     * Update own user
     * @param ruouListener result listener
     * @return true if request was successfully enqueued
     * @throws java.lang.Exception
     */
    public boolean requestUpdateOwnUser(RequestUpdateOwnUserListener ruouListener) throws Exception {
        RequestCallback requestCallback;  
        if (ruouListener == null) {
            throw new Exception("RequestUpdateOwnUserListener must not be null");
        }
        requestCallback = new RequestCallback<RequestUpdateOwnUserListener>(ruouListener) {  
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                getListener().requestUpdateOwnUserFinished(new RequestResult(response.code(), response.body().string()));
            }
        };
        return enqueueRequest(HttpMethod.PUT, true, "user/update/own", requestCallback, JSON, DataMapper.toJSON(db.getCurrentUser(), JsonUserViews.Out.UpdateOwn.class));
    }
    
    /**
     * Block user
     * @param rbuListener result listener
     * @param blockRequest block request
     * @return true if request was successfully enqueued
     * @throws java.lang.Exception 
     */
    public boolean requestBlockUser(RequestBlockUserListener rbuListener, BlockRequest blockRequest) throws Exception {
        RequestCallback requestCallback;  
        if (rbuListener == null) {
            throw new Exception("RequestBlockUserListener must not be null");
        }
        if (blockRequest == null) {
            throw new Exception("BlockRequest must not be null");
        }
        requestCallback = new RequestCallback<RequestBlockUserListener>(rbuListener) {  
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                getListener().requestBlockUserFinished(new RequestResult(response.code(), response.body().string()));
            }
        };
        return enqueueRequest(HttpMethod.POST, true, "user/block", requestCallback, JSON, DataMapper.toJSON(blockRequest));
    }
    
    /**
     * Unblock user
     * @param ruuListener result listener
     * @param username username of the user to unblock
     * @return true if request was successfully enqueued
     * @throws java.lang.Exception  
     */
    public boolean requestUnblockUser(RequestUnblockUserListener ruuListener, String username) throws Exception {
        RequestCallback requestCallback;  
        if (ruuListener == null) {
            throw new Exception("RequestUnblockUserListener must not be null");
        }
        if (StringUtils.isEmpty(username)) {
            throw new Exception("Username must not be empty");
        }
        requestCallback = new RequestCallback<RequestUnblockUserListener>(ruuListener) {  
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                getListener().requestUnblockUserFinished(new RequestResult(response.code(), response.body().string()));
            }
        };
        return enqueueRequest(HttpMethod.GET, true, "user/unblock/" + username, requestCallback, null, null);
    }
    
    /**
     * Get all genders
     * @param rggListener result listener
     * @return true if request was successfully enqueued
     * @throws Exception 
     */
    public boolean requestGetGenders(RequestGetGendersListener rggListener) throws Exception {
        RequestCallback requestCallback;  
        if (rggListener == null) {
            throw new Exception("RequestGetGendersListener must not be null");
        }
        requestCallback = new RequestCallback<RequestGetGendersListener>(rggListener) {  
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                getListener().requestGetGendersFinished(new RequestResult(response.code(), response.body().string()));
            }
        };
        return enqueueRequest(HttpMethod.GET, true, "user/gender/", requestCallback, null, null);
    }
    
    /**
     * Register subject
     * @param rrsListener result listener
     * @param subject subject to register
     * @return true if request was successfully enqueued
     * @throws Exception 
     */
    public boolean requestRegisterSubject(RequestRegisterSubjectListener rrsListener, Subject subject) throws Exception {
        RequestCallback requestCallback;  
        if (rrsListener == null) {
            throw new Exception("RequestRegisterSubjectListener must not be null");
        }
        if (subject == null) {
            throw new Exception("Subject must not be null");
        }
        requestCallback = new RequestCallback<RequestRegisterSubjectListener>(rrsListener) {  
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                getListener().requestRegisterSubjectFinished(new RequestResult(response.code(), response.body().string()));
            }
        };
        return enqueueRequest(HttpMethod.POST, true, "subject/", requestCallback, JSON, DataMapper.toJSON(subject, JsonSubjectViews.Out.Register.class));
    }
    
    /**
     * Update subject
     * @param rusListener result listener
     * @param subject subject to update
     * @return true if request was successfully enqueued
     * @throws Exception 
     */
    public boolean requestUpdateSubject(RequestUpdateSubjectListener rusListener, Subject subject) throws Exception {
        RequestCallback requestCallback;  
        if (rusListener == null) {
            throw new Exception("RequestUpdateSubjectListener must not be null");
        }
        if (subject == null) {
            throw new Exception("Subject must not be null");
        }
        requestCallback = new RequestCallback<RequestUpdateSubjectListener>(rusListener) {  
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                getListener().requestUpdateSubjectFinished(new RequestResult(response.code(), response.body().string()));
            }
        };
        return enqueueRequest(HttpMethod.PUT, true, "subject/", requestCallback, JSON, DataMapper.toJSON(subject, JsonSubjectViews.Out.Update.class));
    }
    
    /**
     * Delete subject
     * @param rdsListener result listener
     * @param subject subject to delete
     * @return true if request was successfully enqueued
     * @throws Exception 
     */
    public boolean requestDeleteSubject(RequestDeleteSubjectListener rdsListener, Subject subject) throws Exception {
        RequestCallback requestCallback;  
        if (rdsListener == null) {
            throw new Exception("RequestDeleteSubjectListener must not be null");
        }
        if (subject == null) {
            throw new Exception("Subject must not be null");
        }
        requestCallback = new RequestCallback<RequestDeleteSubjectListener>(rdsListener) {  
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                getListener().requestDeleteSubjectFinished(new RequestResult(response.code(), response.body().string()));
            }
        };
        return enqueueRequest(HttpMethod.DELTE, true, "subject/" + subject.getId(), requestCallback, null, null);
    }
    
    /**
     * Get all subjects
     * @param rgasListener result listener
     * @return true if request was successfully enqueued
     * @throws Exception 
     */
    public boolean requestGetAllSubjects(RequestGetAllSubjectsListener rgasListener) throws Exception {
        RequestCallback requestCallback;  
        if (rgasListener == null) {
            throw new Exception("RequestGetAllSubjectsListener must not be null");
        }
        requestCallback = new RequestCallback<RequestGetAllSubjectsListener>(rgasListener) {  
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                getListener().requestGetAllSubjectsFinished(new RequestResult(response.code(), response.body().string()));
            }
        };
        return enqueueRequest(HttpMethod.GET, true, "subject/", requestCallback, null, null);
    }
    
    /**
     * Get subject count
     * @param rscListener result listener
     * @return true if request was successfully enqueued
     * @throws Exception 
     */
    public boolean requestSubjectCount(RequestSubjectCountListener rscListener) throws Exception {
        RequestCallback requestCallback;  
        if (rscListener == null) {
            throw new Exception("RequestSubjectCountListener must not be null");
        }
        requestCallback = new RequestCallback<RequestSubjectCountListener>(rscListener) {  
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                getListener().requestSubjectCountFinished(new RequestResult(response.code(), response.body().string()));
            }
        };
        return enqueueRequest(HttpMethod.GET, true, "subject/count/", requestCallback, null, null);
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
} 
