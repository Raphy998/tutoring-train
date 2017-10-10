package at.bsd.tutoringtrain.network;

import at.bsd.tutoringtrain.data.Database;
import at.bsd.tutoringtrain.data.entities.BlockRequest;
import at.bsd.tutoringtrain.data.entities.User;
import at.bsd.tutoringtrain.data.mapper.EntityMapper;
import at.bsd.tutoringtrain.debugging.MessageLogger;
import at.bsd.tutoringtrain.network.enumeration.HttpMethod;
import at.bsd.tutoringtrain.network.enumeration.ResultType;
import at.bsd.tutoringtrain.network.listener.user.RequestAllUsersListener;
import at.bsd.tutoringtrain.network.listener.user.RequestAuthenticateListner;
import at.bsd.tutoringtrain.network.listener.user.RequestBlockUserListener;
import at.bsd.tutoringtrain.network.listener.user.RequestOwnUserListener;
import at.bsd.tutoringtrain.network.listener.user.RequestRegisterUserListener;
import at.bsd.tutoringtrain.network.listener.user.RequestUnblockUserListener;
import at.bsd.tutoringtrain.network.listener.user.RequestUpdateOwnUserListener;
import at.bsd.tutoringtrain.network.listener.user.RequestUpdateUserListener;
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
        serverUrl = "http://localhost:47546/TutoringTrainWebservice/services/";
        //serverURL = "http://192.168.194.147:51246/TutoringTrainWebservice/services/";   //SANTNER
        minimumRequiredRole = 'A';
        client = new OkHttpClient();
        db = Database.getInstance();
        logger = MessageLogger.getINSTANCE();
        username = "";
        sessionkey = "";
        JSON = MediaType.parse("application/json; charset=utf-8");
        
        logger.objectInitialized(this, getClass());  
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
        return !StringUtils.isEmpty(getSessionkey());
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
    public Result authenticate(String username, String password) throws Exception {  
        Credentials creds;
        HttpUrl.Builder urlBuilder;
        RequestBody body;
        ObjectMapper mapper;
        Request request;
        Response response;
        Result result;
        
        if (StringUtils.isEmpty(username) || StringUtils.isEmpty(password)) {
            throw new Exception("empty credentials");
        }
        creds = new Credentials(username, DigestUtils.md5Hex(password));
        creds.setRequiredRole(minimumRequiredRole);
        mapper = new ObjectMapper();
        urlBuilder = HttpUrl.parse(serverUrl).newBuilder();
        urlBuilder.addPathSegment("authentication");     
        body = RequestBody.create(JSON, mapper.writeValueAsString(creds));
        request = new Request.Builder().url(urlBuilder.build().toString()).post(body).build();
        response = client.newCall(request).execute();
        
        switch(response.code()) {
            case 200:
                setUsername(username);
                setSessionkey(response.body().string());
                result = new Result(200, ResultType.OK, getSessionkey());
                break;
             case 401:
                result = new Result(401, ResultType.INVALID_CREDENTIALS, null, "username/password invalid");
                break;
            case 403:    
                result = new Result(403, ResultType.INSUFFICIENT_PRIVILEGES, null, "higher privileges required");
                break;
            case 450:    
                result = new Result(450, ResultType.USER_BLOCKED, null, response.body().string());
                break;
            default:
                result = new Result(response.code(), ResultType.DEFAULT, null, "response code not handled", response.body().string());
                break;
        }
        return result;
    }
    
    /**
     * 
     * @param raListener
     * @param credentials
     * @return
     * @throws Exception 
     */
    public boolean requestAuthenticate(RequestAuthenticateListner raListener, Credentials credentials) throws Exception {  
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
                switch(response.code()) {
                    case 200:
                        setUsername(username);
                        setSessionkey(response.body().string());
                        getListener().requestAuthenticateFinished(new Result(200, ResultType.OK, getSessionkey()));
                        break;
                    case 401:
                       getListener().requestAuthenticateFinished(new Result(401, ResultType.INVALID_CREDENTIALS, null, "username/password invalid"));
                       break;
                   case 403:    
                       getListener().requestAuthenticateFinished(new Result(403, ResultType.INSUFFICIENT_PRIVILEGES, null, "higher privileges required"));
                       break;
                   case 450:    
                       getListener().requestAuthenticateFinished(new Result(450, ResultType.USER_BLOCKED, null, response.body().string()));
                       break;
                   default:
                       getListener().requestAuthenticateFinished(new Result(response.code(), ResultType.DEFAULT, null, "response code not handled", response.body().string()));
                       break;
               }
            }
        };
        return enqueueRequest(HttpMethod.POST, false, "authentication", reqCall, JSON, EntityMapper.toJSON(credentials));
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
    public boolean enqueueRequest(HttpMethod method, boolean authorizationRequired, String pathSegments, RequestCallback callback, MediaType mediaType, String data, QueryParameter... queryparameters) throws Exception {
        HttpUrl.Builder urlBuilder;
        Request.Builder requestBuilder;
        Request request;
        boolean isSessionKeyAvailable;
        isSessionKeyAvailable = !StringUtils.isEmpty(getSessionkey());
        if (!authorizationRequired || isSessionKeyAvailable) {    
            urlBuilder = HttpUrl.parse(serverUrl).newBuilder();
            urlBuilder.addPathSegments(pathSegments);
            for (QueryParameter queryparameter : queryparameters) {
                urlBuilder.addQueryParameter(queryparameter.getKey(), queryparameter.getValue());
            }
            requestBuilder = new Request.Builder();
            if (authorizationRequired) {
                requestBuilder.header("Authorization", getSessionkey());
            }
            requestBuilder.url(urlBuilder.build().toString());
            switch (method) {
                case GET:
                    requestBuilder.get();
                    break;
                case POST:
                    requestBuilder.post(RequestBody.create(mediaType, data));
                    break;
                case PUT:
                    requestBuilder.put(RequestBody.create(mediaType, data));
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
                switch(response.code()) {
                    case 200:
                        getListener().requestRegisterUserFinished(new Result(200 ,ResultType.OK, response.body().string()));
                        break;
                    case 401:
                        getListener().requestRegisterUserFinished(new Result(401, ResultType.SESSION_KEY_INVALID_OR_EXPIRED, null, "session key invalid/expired"));
                        break;
                    case 403:
                        getListener().requestRegisterUserFinished(new Result(403, ResultType.INSUFFICIENT_PRIVILEGES, null, "higher privileges required"));
                        break;
                    case 409:
                        getListener().requestRegisterUserFinished(new Result(409, ResultType.USERNAME_ALREADY_EXISTS, null, "username already exists"));
                        break;
                    case 450:
                        getListener().requestRegisterUserFinished(new Result(450, ResultType.USER_BLOCKED, null, response.body().string()));
                        break;
                    case 451:
                        getListener().requestRegisterUserFinished(new Result(451, ResultType.INVALID_JSON, null, response.body().string()));
                        break;
                    case 500:
                        getListener().requestRegisterUserFinished(new Result(500, ResultType.SERVER_ERROR, null, response.body().string()));
                        break;
                    default:
                        getListener().requestRegisterUserFinished(new Result(response.code(), ResultType.DEFAULT, null, "response code not handled", response.body().string()));
                        break;
                }
            }
        };
        return enqueueRequest(HttpMethod.POST, true, "user/register", reqCall, JSON, EntityMapper.toJSON(user));
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
                try {
                    switch(response.code()) {
                        case 200:
                            getListener().requestOwnUserFinished(new Result(200 ,ResultType.OK, response.body().string()));
                            break;
                        case 401:
                            getListener().requestOwnUserFinished(new Result(401, ResultType.SESSION_KEY_INVALID_OR_EXPIRED, null, "session key invalid/expired"));
                            break;
                        case 403:
                            getListener().requestOwnUserFinished(new Result(403, ResultType.INSUFFICIENT_PRIVILEGES, null, "higher privileges required"));
                            break;
                        case 450:
                            getListener().requestOwnUserFinished(new Result(450, ResultType.USER_BLOCKED, null, response.body().string()));
                            break;
                        case 451:
                            getListener().requestOwnUserFinished(new Result(451, ResultType.INVALID_JSON, null, response.body().string()));
                            break;
                        case 500:
                            getListener().requestOwnUserFinished(new Result(500, ResultType.SERVER_ERROR, null, response.body().string()));
                            break;
                        default:
                            getListener().requestOwnUserFinished(new Result(response.code(), ResultType.DEFAULT, null, "response code not handled", response.body().string()));
                            break;
                    }
                } catch (IOException ioex) {
                    throw ioex;
                } catch (Exception ex) {
                    getListener().requestFailed(new Result(0, ResultType.ERROR, null, ex.getMessage()));
                }
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
                switch(response.code()) {
                    case 200:
                        getListener().requestAllUsersFinished(new Result(200 ,ResultType.OK, response.body().string()));
                        break;
                    case 401:
                        getListener().requestAllUsersFinished(new Result(401, ResultType.SESSION_KEY_INVALID_OR_EXPIRED, null, "session key invalid/expired"));
                        break;
                    case 403:
                        getListener().requestAllUsersFinished(new Result(403, ResultType.INSUFFICIENT_PRIVILEGES, null, "higher privileges required"));
                        break;
                    case 450:
                        getListener().requestAllUsersFinished(new Result(450, ResultType.USER_BLOCKED, null, response.body().string()));
                        break;
                    case 451:
                        getListener().requestAllUsersFinished(new Result(451, ResultType.INVALID_JSON, null, response.body().string()));
                        break;
                    case 500:
                        getListener().requestAllUsersFinished(new Result(500, ResultType.SERVER_ERROR, null, response.body().string()));
                        break;
                    default:
                        getListener().requestAllUsersFinished(new Result(response.code(), ResultType.DEFAULT, null, "response code not handled", response.body().string()));
                        break;
                }
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
                switch(response.code()) {
                    case 200:
                        getListener().requestAllUsersFinished(new Result(200 ,ResultType.OK, response.body().string()));
                        break;
                    case 401:
                        getListener().requestAllUsersFinished(new Result(401, ResultType.SESSION_KEY_INVALID_OR_EXPIRED, null, "session key invalid/expired"));
                        break;
                    case 403:
                        getListener().requestAllUsersFinished(new Result(403, ResultType.INSUFFICIENT_PRIVILEGES, null, "higher privileges required"));
                        break;
                    case 450:
                        getListener().requestAllUsersFinished(new Result(450, ResultType.USER_BLOCKED, null, response.body().string()));
                        break;
                    case 451:
                        getListener().requestAllUsersFinished(new Result(451, ResultType.INVALID_JSON, null, response.body().string()));
                        break;
                    case 500:
                        getListener().requestAllUsersFinished(new Result(500, ResultType.SERVER_ERROR, null, response.body().string()));
                        break;
                    default:
                        getListener().requestAllUsersFinished(new Result(response.code(), ResultType.DEFAULT, null, "response code not handled", response.body().string()));
                        break;
                }
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
                switch(response.code()) {
                    case 200:
                        getListener().requestUpdateUserFinished(new Result(200 ,ResultType.OK, response.body().string()));
                        break;
                    case 401:
                        getListener().requestUpdateUserFinished(new Result(401, ResultType.SESSION_KEY_INVALID_OR_EXPIRED, null, "session key invalid/expired"));
                        break;
                    case 403:
                        getListener().requestUpdateUserFinished(new Result(403, ResultType.INSUFFICIENT_PRIVILEGES, null, "higher privileges required"));
                        break;
                    case 450:
                        getListener().requestUpdateUserFinished(new Result(450, ResultType.USER_BLOCKED, null, response.body().string()));
                        break;
                    case 451:
                        getListener().requestUpdateUserFinished(new Result(451, ResultType.INVALID_JSON, null, response.body().string()));
                        break;
                    case 453:
                        getListener().requestUpdateUserFinished(new Result(453, ResultType.USER_NOT_FOUND, null, "user not found"));
                        break;
                    case 500:
                        getListener().requestUpdateUserFinished(new Result(500, ResultType.SERVER_ERROR, null, response.body().string()));
                        break;
                    default:
                        getListener().requestUpdateUserFinished(new Result(response.code(), ResultType.DEFAULT, null, "response code not handled", response.body().string()));
                        break;
                }
            }
        };
        return enqueueRequest(HttpMethod.PUT, true, "user/update", reqCall, JSON, EntityMapper.toJSON(user));
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
                switch(response.code()) {
                    case 200:
                        getListener().requestUpdateOwnUserFinished(new Result(200 ,ResultType.OK, response.body().string()));
                        break;
                    case 401:
                        getListener().requestUpdateOwnUserFinished(new Result(401, ResultType.SESSION_KEY_INVALID_OR_EXPIRED, null, "session key invalid/expired"));
                        break;
                    case 403:
                        getListener().requestUpdateOwnUserFinished(new Result(403, ResultType.INSUFFICIENT_PRIVILEGES, null, "higher privileges required"));
                        break;
                    case 450:
                        getListener().requestUpdateOwnUserFinished(new Result(450, ResultType.USER_BLOCKED, null, response.body().string()));
                        break;
                    case 451:
                        getListener().requestUpdateOwnUserFinished(new Result(451, ResultType.INVALID_JSON, null, response.body().string()));
                        break;
                    case 500:
                        getListener().requestUpdateOwnUserFinished(new Result(500, ResultType.SERVER_ERROR, null, response.body().string()));
                        break;
                    default:
                        getListener().requestUpdateOwnUserFinished(new Result(response.code(), ResultType.DEFAULT, null, "response code not handled", response.body().string()));
                        break;
                }
            }
        };
        return enqueueRequest(HttpMethod.PUT, true, "user/update/own", reqCall, JSON, EntityMapper.toJSON(db.getUser()));
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
                switch(response.code()) {
                    case 200:
                        getListener().requestBlockUserFinished(new Result(200 ,ResultType.OK, response.body().string()));
                        break;
                    case 401:
                        getListener().requestBlockUserFinished(new Result(401, ResultType.SESSION_KEY_INVALID_OR_EXPIRED, null, "session key invalid/expired"));
                        break;
                    case 403:
                        getListener().requestBlockUserFinished(new Result(403, ResultType.INSUFFICIENT_PRIVILEGES, null, "higher privileges required"));
                        break;
                    case 450:
                        getListener().requestBlockUserFinished(new Result(450, ResultType.USER_BLOCKED, null, response.body().string()));
                        break;
                    case 451:
                        getListener().requestBlockUserFinished(new Result(451, ResultType.INVALID_JSON, null, response.body().string()));
                        break;
                    case 456:
                        getListener().requestBlockUserFinished(new Result(456, ResultType.BLOCKING_FAILED, null, response.body().string()));
                        break;
                    case 500:
                        getListener().requestBlockUserFinished(new Result(500, ResultType.SERVER_ERROR, null, response.body().string()));
                        break;
                    default:
                        getListener().requestBlockUserFinished(new Result(response.code(), ResultType.DEFAULT, null, "response code not handled", response.body().string()));
                        break;
                }
            }
        };
        return enqueueRequest(HttpMethod.POST, true, "user/block", reqCall, JSON, EntityMapper.toJSON(blockRequest));
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
                switch(response.code()) {
                    case 200:
                        getListener().requestUnblockUserFinished(new Result(200 ,ResultType.OK, response.body().string()));
                        break;
                    case 401:
                        getListener().requestUnblockUserFinished(new Result(401, ResultType.SESSION_KEY_INVALID_OR_EXPIRED, null, "session key invalid/expired"));
                        break;
                    case 403:
                        getListener().requestUnblockUserFinished(new Result(403, ResultType.INSUFFICIENT_PRIVILEGES, null, "higher privileges required"));
                        break;
                    case 450:
                        getListener().requestUnblockUserFinished(new Result(450, ResultType.USER_BLOCKED, null, response.body().string()));
                        break;
                    case 451:
                        getListener().requestUnblockUserFinished(new Result(451, ResultType.INVALID_JSON, null, response.body().string()));
                        break;
                    case 500:
                        getListener().requestUnblockUserFinished(new Result(500, ResultType.SERVER_ERROR, null, response.body().string()));
                        break;
                    default:
                        getListener().requestUnblockUserFinished(new Result(response.code(), ResultType.DEFAULT, null, "response code not handled", response.body().string()));
                        break;
                }
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
