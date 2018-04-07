package at.tutoringtrain.adminclient.io.network;

import at.tutoringtrain.adminclient.data.entry.EntryType;
import at.tutoringtrain.adminclient.data.entry.Location;
import at.tutoringtrain.adminclient.data.mapper.DataMapper;
import at.tutoringtrain.adminclient.data.mapper.DataMappingViews;
import at.tutoringtrain.adminclient.data.subject.Subject;
import at.tutoringtrain.adminclient.data.user.BlockRequest;
import at.tutoringtrain.adminclient.data.user.User;
import at.tutoringtrain.adminclient.exception.ParameterValueException;
import at.tutoringtrain.adminclient.exception.RequiredParameterException;
import at.tutoringtrain.adminclient.io.network.listener.entry.comment.RequestCommentsOfEntryListener;
import at.tutoringtrain.adminclient.io.network.listener.entry.comment.RequestDeleteCommentListener;
import at.tutoringtrain.adminclient.io.network.listener.entry.offer.RequestDeleteOfferListener;
import at.tutoringtrain.adminclient.io.network.listener.entry.offer.RequestNewestOffersListener;
import at.tutoringtrain.adminclient.io.network.listener.entry.offer.RequestNewestOffersOfUserListener;
import at.tutoringtrain.adminclient.io.network.listener.entry.offer.RequestOfferCountListener;
import at.tutoringtrain.adminclient.io.network.listener.entry.offer.RequestOfferSearchListener;
import at.tutoringtrain.adminclient.io.network.listener.entry.offer.RequestOfferSearchLocationListener;
import at.tutoringtrain.adminclient.io.network.listener.entry.offer.RequestOfferSimpleSearchListener;
import at.tutoringtrain.adminclient.io.network.listener.entry.offer.RequestResetPropertyOfOfferListener;
import at.tutoringtrain.adminclient.io.network.listener.entry.offer.RequestUpdateOfferListener;
import at.tutoringtrain.adminclient.io.network.listener.entry.request.RequestDeleteRequestListener;
import at.tutoringtrain.adminclient.io.network.listener.entry.request.RequestNewestRequestsListener;
import at.tutoringtrain.adminclient.io.network.listener.entry.request.RequestNewestRequestsOfUserListener;
import at.tutoringtrain.adminclient.io.network.listener.entry.request.RequestRequestCountListener;
import at.tutoringtrain.adminclient.io.network.listener.entry.request.RequestRequestSearchListener;
import at.tutoringtrain.adminclient.io.network.listener.entry.request.RequestRequestSearchLocationListener;
import at.tutoringtrain.adminclient.io.network.listener.entry.request.RequestRequestSimpleSearchListener;
import at.tutoringtrain.adminclient.io.network.listener.entry.request.RequestResetPropertyOfRequestListener;
import at.tutoringtrain.adminclient.io.network.listener.entry.request.RequestUpdateRequestListener;
import at.tutoringtrain.adminclient.io.network.listener.subject.RequestAllSubjectsListener;
import at.tutoringtrain.adminclient.io.network.listener.subject.RequestDeleteSubjectListener;
import at.tutoringtrain.adminclient.io.network.listener.subject.RequestRegisterSubjectListener;
import at.tutoringtrain.adminclient.io.network.listener.subject.RequestSubjectCountListener;
import at.tutoringtrain.adminclient.io.network.listener.subject.RequestUpdateSubjectListener;
import at.tutoringtrain.adminclient.io.network.listener.user.RequestAllUsersListener;
import at.tutoringtrain.adminclient.io.network.listener.user.RequestAuthenticateListner;
import at.tutoringtrain.adminclient.io.network.listener.user.RequestBlockUserListener;
import at.tutoringtrain.adminclient.io.network.listener.user.RequestDeleteUserListener;
import at.tutoringtrain.adminclient.io.network.listener.user.RequestGetAvatarListener;
import at.tutoringtrain.adminclient.io.network.listener.user.RequestGetGendersListener;
import at.tutoringtrain.adminclient.io.network.listener.user.RequestOwnUserListener;
import at.tutoringtrain.adminclient.io.network.listener.user.RequestReauthenticateListner;
import at.tutoringtrain.adminclient.io.network.listener.user.RequestRegisterUserListener;
import at.tutoringtrain.adminclient.io.network.listener.user.RequestResetAvatarListener;
import at.tutoringtrain.adminclient.io.network.listener.user.RequestResetPasswordListener;
import at.tutoringtrain.adminclient.io.network.listener.user.RequestSetUserRoleListener;
import at.tutoringtrain.adminclient.io.network.listener.user.RequestTokenValidationListener;
import at.tutoringtrain.adminclient.io.network.listener.user.RequestUnblockUserListener;
import at.tutoringtrain.adminclient.io.network.listener.user.RequestUpdateAvatarListener;
import at.tutoringtrain.adminclient.io.network.listener.user.RequestUpdateOwnUserListener;
import at.tutoringtrain.adminclient.io.network.listener.user.RequestUpdateUserListener;
import at.tutoringtrain.adminclient.io.network.listener.user.RequestUserCountListener;
import at.tutoringtrain.adminclient.io.network.listener.user.RequestUserSearchListener;
import at.tutoringtrain.adminclient.main.ApplicationManager;
import at.tutoringtrain.adminclient.main.DefaultValueProvider;
import at.tutoringtrain.adminclient.ui.search.OrderElement;
import at.tutoringtrain.adminclient.ui.search.SearchCriteria;
import at.tutoringtrain.adminclient.ui.search.SpatialOperation;
import at.tutoringtrain.adminclient.ui.search.SpatialSearchCriteria;
import at.tutoringtrain.adminclient.ui.search.entry.EntryProp;
import at.tutoringtrain.adminclient.ui.search.entry.EntrySearch;
import at.tutoringtrain.adminclient.ui.search.user.UserSearch;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import javax.imageio.ImageIO;
import okhttp3.Call;
import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
    
    private final ApplicationManager applicationManager;
    private final OkHttpClient okHttpClient;
    private final Logger logger;
    private final DataMapper dataMapper;
    private final DefaultValueProvider defaultValueProvider;
    private final MediaType json;
    private final MediaType png;
    private final MediaType jpg;
    private final String no_data;
    private final MediaType no_mediatype;
    private final File no_file;
    
    private String currentUsername; 
    private String sessionToken;
    
    private Communicator() {
        this.logger = LogManager.getLogger(this);
        this.applicationManager = ApplicationManager.getInstance();
        this.dataMapper = ApplicationManager.getDataMapper();
        this.defaultValueProvider = ApplicationManager.getDefaultValueProvider();
        this.okHttpClient = new OkHttpClient.Builder().connectTimeout(15, TimeUnit.SECONDS).readTimeout(15, TimeUnit.SECONDS).writeTimeout(15, TimeUnit.SECONDS).build();
        this.currentUsername = "";
        this.sessionToken = "";    
        this.json = defaultValueProvider.getJsonMediaType();
        this.png = defaultValueProvider.getPngImageMediaType();
        this.jpg = defaultValueProvider.getJpgImageMediaType();
        this.no_data = null;
        this.no_file = null;
        this.no_mediatype = null;
        this.logger.debug("Communicator initialized");
    }

    private void setCurrentUsername(String username) {
        currentUsername = username;
    }

    public String getCurrentUsername() {
        return currentUsername;
    }
    
    public void clearCurrentUsername() {
        currentUsername = "";
    }

    private void setSessionToken(String token) {
        sessionToken = "Bearer " + token;
    }
    
    public String getSessionToken() {
        return sessionToken;
    }
    
    public void clearSessionToken() {
        sessionToken = "";
        applicationManager.deleteTokenFile();
    }

    public boolean isSessionTokenAvailable() {
        return !StringUtils.isBlank(getSessionToken());
    }   
    
    public void closeSession() {
        clearSessionToken();
        clearCurrentUsername();
    }
    
    private boolean enqueueRequest(HttpMethod method, boolean authorizationRequired, String pathSegments, RequestCallback callback, QueryParameter... queryparameters) throws Exception {
        return enqueueRequest(method, authorizationRequired, pathSegments, callback, no_mediatype, no_data, no_file, getSessionToken(), queryparameters);
    }
    
    private boolean enqueueRequest(HttpMethod method, boolean authorizationRequired, String pathSegments, RequestCallback callback, MediaType mediaType, File file, QueryParameter... queryparameters) throws Exception {
        return enqueueRequest(method, authorizationRequired, pathSegments, callback, mediaType, no_data, file, getSessionToken(), queryparameters);
    }

    private boolean enqueueRequest(HttpMethod method, boolean authorizationRequired, String pathSegments, RequestCallback callback, MediaType mediaType, String data, QueryParameter... queryparameters) throws Exception {
        return enqueueRequest(method, authorizationRequired, pathSegments, callback, mediaType, data, no_file, getSessionToken(), queryparameters);
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
    private boolean enqueueRequest(HttpMethod method, boolean authorizationRequired, String pathSegments, RequestCallback callback, MediaType mediaType, String data, File file, String token, QueryParameter... queryparameters) throws Exception {
        HttpUrl.Builder urlBuilder;
        Request.Builder requestBuilder;
        Request request;
        RequestBody requestBody;
        boolean isSessionKeyAvailable;
        isSessionKeyAvailable = !StringUtils.isBlank(token);
        if (!authorizationRequired || isSessionKeyAvailable) {    
            urlBuilder = HttpUrl.parse(applicationManager.getWebServiceUrl()).newBuilder();
            urlBuilder.addPathSegments(pathSegments);
            for (QueryParameter queryparameter : queryparameters) {
                urlBuilder.addQueryParameter(queryparameter.getKey(), queryparameter.getValue());
            }
            requestBuilder = new Request.Builder();
            if (authorizationRequired) {
                requestBuilder.header("Authorization", token);
            }
            requestBuilder.header("Accept-Language", applicationManager.getLanguage().getLocale().getLanguage());
            requestBuilder.url(urlBuilder.build().toString());
            if (method == HttpMethod.POST || method == HttpMethod.PUT) {
                if (mediaType == jpg || mediaType == png) {
                    requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM).addPart(Headers.of("Content-Disposition", "form-data; name=\"name\""), RequestBody.create(null, file.getName())).addPart(Headers.of("Content-Disposition", "form-data; name=\"file\""),RequestBody.create(mediaType, file)).build();
                } else {
                    requestBody = RequestBody.create(mediaType, data == null ? "" : data);
                }
            } else {
                requestBody = null;
            }
            switch (method) {
                case GET:
                    requestBuilder.get();
                    break;
                case POST:
                    requestBuilder.post(requestBody);
                    break;
                case PUT:
                    requestBuilder.put(requestBody);
                    break;
                case DELTE:
                    requestBuilder.delete();
                    break;
            }
            request = requestBuilder.build();
            okHttpClient.newCall(request).enqueue(callback);
        }
        return authorizationRequired ? isSessionKeyAvailable : true;
    }
    
    /**
     * Authenticates the user using a credential object
     * @param listener result listener
     * @param credentials user credentials
     * @param saveSession save session locally 
     * @throws Exception 
     */
    public void requestAuthenticate(RequestAuthenticateListner listener, Credentials credentials, boolean saveSession) throws Exception {  
        RequestCallback requestCallback;  
        if (listener == null) {
             throw new RequiredParameterException(listener, "must not be null");
        }
        if (credentials == null) {
             throw new RequiredParameterException(credentials, "must not be null");
        }
        credentials.setRequiredRole(applicationManager.getMinimumRequiredUserRole().getValue());
        requestCallback = new RequestCallback<RequestAuthenticateListner>(listener) {  
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.code() == 200) {
                    setCurrentUsername(currentUsername);
                    setSessionToken(response.body().string());
                    if (saveSession) {
                        try {
                            applicationManager.writeTokenFile(getSessionToken());
                        } catch (Exception exception) {
                            logger.warn("Saving session failed", exception);
                        }
                    } else {
                        applicationManager.deleteTokenFile();
                    }
                    listener.requestAuthenticateFinished(new RequestResult(response.code(), "OK"));
                } else {
                    listener.requestAuthenticateFinished(new RequestResult(response.code(), response.body().string())); 
                }
            }
        };
        enqueueRequest(HttpMethod.POST, false, "authentication", requestCallback, json, dataMapper.toJSON(credentials));
    }
    
    public void requestReauthenticate(RequestReauthenticateListner listener, Credentials credentials) throws Exception {  
        RequestCallback requestCallback;  
        if (listener == null) {
             throw new RequiredParameterException(listener, "must not be null");
        }
        if (credentials == null) {
             throw new RequiredParameterException(credentials, "must not be null");
        }
        credentials.setRequiredRole(applicationManager.getMinimumRequiredUserRole().getValue());
        clearSessionToken();
        requestCallback = new RequestCallback<RequestReauthenticateListner>(listener) {  
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.code() == 200) {
                    setSessionToken(response.body().string());
                    listener.requestReauthenticateFinished(new RequestResult(response.code(), "OK"));
                } else {
                    listener.requestReauthenticateFinished(new RequestResult(response.code(), response.body().string())); 
                }
            }
        };
        enqueueRequest(HttpMethod.POST, false, "authentication", requestCallback, json, dataMapper.toJSON(credentials));
    }
    
    /**
     * Validates an existing token
     * @param listener result listner
     * @param token token to validate 
     * @throws Exception 
     */
    public void requestTokenValidation(RequestTokenValidationListener listener, String token) throws Exception {  
         RequestCallback requestCallback;  
        if (listener == null) {
            throw new RequiredParameterException(listener, "must not be null");
        }
        if (StringUtils.isBlank(token)) {
            throw new RequiredParameterException(token, "Token must not be blank");
        }
        requestCallback = new RequestCallback<RequestTokenValidationListener>(listener) {  
            @Override
            public void onResponse(Call call, Response response) throws IOException {           
                if (response.code() == 200) {
                    setCurrentUsername("");
                    setSessionToken(StringUtils.remove(token, "Bearer "));
                    listener.requestTokenValidationFinished(new RequestResult(response.code(), "VALID"));
                } else {
                    listener.requestTokenValidationFinished(new RequestResult(response.code(), response.body().string())); 
                }
            }
        };
        enqueueRequest(HttpMethod.POST, true, "authentication/check", requestCallback, no_mediatype, no_data, no_file, token);
    }

    /**
     * Register user
     * @param listener result listener
     * @param user user to register
     * @return true if request was successfully enqueued
     * @throws java.lang.Exception  
     */
    public boolean requestRegisterUser(RequestRegisterUserListener listener, User user) throws Exception {
        RequestCallback requestCallback;  
        if (listener == null) {
             throw new RequiredParameterException(listener, "must not be null");
        }
        if (user == null) {
             throw new RequiredParameterException(user, "must not be null");
        }
        requestCallback = new RequestCallback<RequestRegisterUserListener>(listener) {  
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                listener.requestRegisterUserFinished(new RequestResult(response.code(), response.body().string()));
            }
        };
        return enqueueRequest(HttpMethod.POST, true, "user/register", requestCallback, json, dataMapper.toJSON(user, DataMappingViews.User.Out.Register.class));
    }
    
    /**
     * Get own user
     * @param listener result listener
     * @return true if request was successfully enqueued
     * @throws Exception 
     */
    public boolean requestOwnUser(RequestOwnUserListener listener) throws Exception {     
        RequestCallback requestCallback;  
        if (listener == null) {
             throw new RequiredParameterException(listener, "must not be null");
        }
        requestCallback = new RequestCallback<RequestOwnUserListener>(listener) {  
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                listener.requestOwnUserFinished(new RequestResult(response.code(), response.body().string()));
            }
        };
        return enqueueRequest(HttpMethod.GET, true, "user", requestCallback);
    }
    
    /**
     * Get all users
     * @param listener result listner
     * @return true if request was successfully enqueued
     * @throws java.lang.Exception 
     */
    public boolean requestAllUsers(RequestAllUsersListener listener) throws Exception {
        RequestCallback requestCallback;  
        if (listener == null) {
            throw new RequiredParameterException(listener, "must not be null");
        }
        requestCallback = new RequestCallback<RequestAllUsersListener>(listener) {  
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                listener.requestAllUsersFinished(new RequestResult(response.code(), response.body().string()));
            }
        };
        return enqueueRequest(HttpMethod.GET, true, "user/all", requestCallback);
    }
    
    /**
     * Get all users within a specified border
     * @param listener result listener
     * @param startIndex start index
     * @param maxCount maximum count
     * @return true if request was successfully enqueued
     * @throws java.lang.Exception 
     */
    public boolean requestAllUsers(RequestAllUsersListener listener, int startIndex, int maxCount) throws Exception {
        RequestCallback requestCallback;  
        if (listener == null) {
             throw new RequiredParameterException(listener, "must not be null");
        }
        requestCallback = new RequestCallback<RequestAllUsersListener>(listener) {  
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                listener.requestAllUsersFinished(new RequestResult(response.code(), response.body().string()));
            }
        };
        return enqueueRequest(HttpMethod.GET, true, "user/all", requestCallback, new QueryParameter("start", Integer.toString(startIndex)), new QueryParameter("pageSize", Integer.toString(maxCount)));
    }
    
    /**
     * 
     * @param listener
     * @param userSearch
     * @return
     * @throws Exception 
     */
    public boolean requestUserSearch(RequestUserSearchListener listener, UserSearch userSearch) throws Exception {
        RequestCallback requestCallback;  
        if (listener == null) {
             throw new RequiredParameterException(listener, "must not be null");
        }
        if (userSearch == null) {
             throw new RequiredParameterException(userSearch, "must not be null");
        }
        requestCallback = new RequestCallback<RequestUserSearchListener>(listener) {  
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                listener.requestUserSearchFinished(new RequestResult(response.code(), response.body().string()));
            }
        };
        return enqueueRequest(HttpMethod.POST, true, "user/search", requestCallback, json, dataMapper.toJSON(userSearch));
    }

    /**
     * Get user count
     * @param listener result listener
     * @return true if request was successfully enqueued
     * @throws Exception 
     */
    public boolean requestUserCount(RequestUserCountListener listener) throws Exception {
        RequestCallback requestCallback;  
        if (listener == null) {
             throw new RequiredParameterException(listener, "must not be null");
        }
        requestCallback = new RequestCallback<RequestUserCountListener>(listener) {  
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                listener.requestUserCountFinished(new RequestResult(response.code(), response.body().string()));
            }
        };
        return enqueueRequest(HttpMethod.GET, true, "user/count/", requestCallback);
    }
    
    /**
     * Update user
     * @param listener result listener
     * @param user user to update
     * @return true if request was successfully enqueued
     * @throws java.lang.Exception 
     */
    public boolean requestUpdateUser(RequestUpdateUserListener listener, User user) throws Exception {
        RequestCallback requestCallback;  
        if (listener == null) {
            throw new RequiredParameterException(listener, "must not be null");
        }
        if (user == null) {
            throw new RequiredParameterException(user, "must not be null");
        }
        requestCallback = new RequestCallback<RequestUpdateUserListener>(listener) {  
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                listener.requestUpdateUserFinished(new RequestResult(response.code(), response.body().string()));
            }
        };
        return enqueueRequest(HttpMethod.PUT, true, "user/update", requestCallback, json, dataMapper.toJSON(user, DataMappingViews.User.Out.Update.class));
    }
    
    /**
     * Update own user
     * @param listener result listener
     * @return true if request was successfully enqueued
     * @throws java.lang.Exception
     */
    public boolean requestUpdateOwnUser(RequestUpdateOwnUserListener listener) throws Exception {
        RequestCallback requestCallback;  
        if (listener == null) {
            throw new RequiredParameterException(listener, "must not be null");
        }
        requestCallback = new RequestCallback<RequestUpdateOwnUserListener>(listener) {  
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                listener.requestUpdateOwnUserFinished(new RequestResult(response.code(), response.body().string()));
            }
        };
        return enqueueRequest(HttpMethod.PUT, true, "user/update/own", requestCallback, json, dataMapper.toJSON(applicationManager.getCurrentUser(), DataMappingViews.User.Out.UpdateOwn.class));
    }
    
    public boolean requestUserAvatar(RequestGetAvatarListener listener, String username) throws Exception {
        RequestCallback requestCallback;  
        if (listener == null) {
            throw new RequiredParameterException(listener, "must not be null");
        }
        requestCallback = new RequestCallback<RequestGetAvatarListener>(listener) {  
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                listener.requestAvatarFinished(new RequestResult(response.code(), ""), response.body().byteStream());
            }
        };
        return enqueueRequest(HttpMethod.GET, true, "user/avatar" + (StringUtils.isBlank(username) ? "" : "/" + username), requestCallback, null, "");
    }
    
    public boolean requestUpdateUserAvatar(RequestUpdateAvatarListener listener, User user, File imageFile) throws Exception {
        RequestCallback requestCallback;  
        if (listener == null) {
            throw new RequiredParameterException(listener, "must not be null");
        }
        requestCallback = new RequestCallback<RequestUpdateAvatarListener>(listener) {  
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                listener.requestUpdateAvatarFinished(new RequestResult(response.code(), response.body().string()), ImageIO.read(imageFile));
            }
        };
        return enqueueRequest(HttpMethod.POST, true, "user/avatar", requestCallback, imageFile.getAbsolutePath().endsWith("png") ? png : jpg, imageFile);
    }
    
    /**
     * 
     * @param listener
     * @param user
     * @return
     * @throws Exception 
     */
    public boolean requestResetUserAvatar(RequestResetAvatarListener listener, User user) throws Exception {
        RequestCallback requestCallback;  
        if (listener == null) {
            throw new RequiredParameterException(listener, "must not be null");
        }
        if (user == null) {
            throw new RequiredParameterException(user, "must not be null");
        }
        requestCallback = new RequestCallback<RequestResetAvatarListener>(listener) {  
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                listener.requestResetAvatarFinished(new RequestResult(response.code(), response.body().string()));
            }
        };
        return enqueueRequest(HttpMethod.DELTE, true, "user/avatar/" + user.getUsername(), requestCallback);
    }
    
    /**
     * Block user
     * @param listener result listener
     * @param blockRequest block request
     * @return true if request was successfully enqueued
     * @throws java.lang.Exception 
     */
    public boolean requestBlockUser(RequestBlockUserListener listener, BlockRequest blockRequest, boolean unlimited) throws Exception {
        RequestCallback requestCallback;  
        if (listener == null) {
            throw new Exception("RequestBlockUserListener must not be null");
        }
        if (blockRequest == null) {
            throw new Exception("BlockRequest must not be null");
        }
        requestCallback = new RequestCallback<RequestBlockUserListener>(listener) {  
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                listener.requestBlockUserFinished(new RequestResult(response.code(), response.body().string()));
            }
        };
        return enqueueRequest(HttpMethod.POST, true, "user/block", requestCallback, json, dataMapper.toJSON(blockRequest, (unlimited ? DataMappingViews.BlockRequest.Out.Unlimited.class : DataMappingViews.BlockRequest.Out.Limited.class)));
    }
    
    /**
     * Unblock user
     * @param listener result listener
     * @param username username of the user to unblock
     * @return true if request was successfully enqueued
     * @throws java.lang.Exception  
     */
    public boolean requestUnblockUser(RequestUnblockUserListener listener, String username) throws Exception {
        RequestCallback requestCallback;  
        if (listener == null) {
            throw new Exception("RequestUnblockUserListener must not be null");
        }
        if (StringUtils.isEmpty(username)) {
            throw new Exception("Username must not be empty");
        }
        requestCallback = new RequestCallback<RequestUnblockUserListener>(listener) {  
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                listener.requestUnblockUserFinished(new RequestResult(response.code(), response.body().string()));
            }
        };
        return enqueueRequest(HttpMethod.GET, true, "user/unblock/" + username, requestCallback);
    }
    
    public boolean requestResetPassword(RequestResetPasswordListener listener, String username) throws Exception {
        RequestCallback requestCallback;  
        if (listener == null) {
            throw new Exception("RequestResetPasswordListener must not be null");
        }
        if (StringUtils.isEmpty(username)) {
            throw new Exception("Username must not be empty");
        }
        requestCallback = new RequestCallback<RequestResetPasswordListener>(listener) {  
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                listener.requestResetPasswordFinished(new RequestResult(response.code(), response.body().string()));
            }
        };
        return enqueueRequest(HttpMethod.PUT, true, "user/resetPw/" + username, requestCallback);
    }
    
    public boolean requestSetUserRole(RequestSetUserRoleListener listener, User user) throws Exception {
        RequestCallback requestCallback;  
        if (listener == null) {
            throw new RequiredParameterException(listener, "must not be null");
        }
        if (user == null) {
            throw new RequiredParameterException(user, "must not be null");
        }
        requestCallback = new RequestCallback<RequestSetUserRoleListener>(listener) {  
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                listener.requestSetUserRoleFinished(new RequestResult(response.code(), response.body().string()));
            }
        };
        return enqueueRequest(HttpMethod.PUT, true, "user/role/" + user.getUsername(), requestCallback, json, dataMapper.toJSON(user, DataMappingViews.User.Out.UpdateRole.class));
    }
    
    public boolean requestDeleteUser(RequestDeleteUserListener listener, User user, boolean force) throws Exception {
        RequestCallback requestCallback;  
        if (listener == null) {
            throw new RequiredParameterException(listener, "must not be null");
        }
        if (user == null) {
            throw new RequiredParameterException(user, "must not be null");
        }
        requestCallback = new RequestCallback<RequestDeleteUserListener>(listener) {  
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                listener.requestDeleteUserFinished(new RequestResult(response.code(), response.body().string()));
            }
        };
        return enqueueRequest(HttpMethod.DELTE, true, "user/" + user.getUsername(), requestCallback, new QueryParameter("force", Boolean.toString(force)));
    }
    
    /**
     * Get all genders
     * @param listener result listener
     * @return true if request was successfully enqueued
     * @throws Exception 
     */
    public boolean requestGetGenders(RequestGetGendersListener listener) throws Exception {
        RequestCallback requestCallback;  
        if (listener == null) {
            throw new Exception("RequestGetGendersListener must not be null");
        }
        requestCallback = new RequestCallback<RequestGetGendersListener>(listener) {  
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                listener.requestGetGendersFinished(new RequestResult(response.code(), response.body().string()));
            }
        };
        return enqueueRequest(HttpMethod.GET, true, "user/gender/", requestCallback);
    }
    
    /**
     * Register subject
     * @param listener result listener
     * @param subject subject to register
     * @return true if request was successfully enqueued
     * @throws Exception 
     */
    public boolean requestRegisterSubject(RequestRegisterSubjectListener listener, Subject subject) throws Exception {
        RequestCallback requestCallback;  
        if (listener == null) {
            throw new Exception("RequestRegisterSubjectListener must not be null");
        }
        if (subject == null) {
            throw new Exception("Subject must not be null");
        }
        requestCallback = new RequestCallback<RequestRegisterSubjectListener>(listener) {  
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                listener.requestRegisterSubjectFinished(new RequestResult(response.code(), response.body().string()));
            }
        };
        return enqueueRequest(HttpMethod.POST, true, "subject/", requestCallback, json, dataMapper.toJSON(subject, DataMappingViews.Subject.Out.Register.class));
    }
    
    /**
     * Update subject
     * @param listener result listener
     * @param subject subject to update
     * @return true if request was successfully enqueued
     * @throws Exception 
     */
    public boolean requestUpdateSubject(RequestUpdateSubjectListener listener, Subject subject) throws Exception {
        RequestCallback requestCallback;  
        if (listener == null) {
            throw new Exception("RequestUpdateSubjectListener must not be null");
        }
        if (subject == null) {
            throw new Exception("Subject must not be null");
        }
        requestCallback = new RequestCallback<RequestUpdateSubjectListener>(listener) {  
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                listener.requestUpdateSubjectFinished(new RequestResult(response.code(), response.body().string()));
            }
        };
        return enqueueRequest(HttpMethod.PUT, true, "subject/", requestCallback, json, dataMapper.toJSON(subject, DataMappingViews.Subject.Out.Update.class));
    }
    
    public boolean requestUpdateSubjectState(RequestUpdateSubjectListener listener, Subject subject) throws Exception {
        RequestCallback requestCallback;  
        if (listener == null) {
            throw new Exception("RequestUpdateSubjectListener must not be null");
        }
        if (subject == null) {
            throw new Exception("Subject must not be null");
        }
        requestCallback = new RequestCallback<RequestUpdateSubjectListener>(listener) {  
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                listener.requestUpdateSubjectFinished(new RequestResult(response.code(), response.body().string()));
            }
        };
        return enqueueRequest(HttpMethod.PUT, true, "subject/", requestCallback, json, dataMapper.toJSON(subject, DataMappingViews.Subject.Out.UpdateState.class));
    }
    
    /**
     * Delete subject
     * @param listener result listener
     * @param subject subject to delete
     * @return true if request was successfully enqueued
     * @throws Exception 
     */
    public boolean requestDeleteSubject(RequestDeleteSubjectListener listener, Subject subject) throws Exception {
        RequestCallback requestCallback;  
        if (listener == null) {
            throw new Exception("RequestDeleteSubjectListener must not be null");
        }
        if (subject == null) {
            throw new Exception("Subject must not be null");
        }
        requestCallback = new RequestCallback<RequestDeleteSubjectListener>(listener) {  
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                listener.requestDeleteSubjectFinished(new RequestResult(response.code(), response.body().string()));
            }
        };
        return enqueueRequest(HttpMethod.DELTE, true, "subject/" + subject.getId(), requestCallback);
    }
    
    /**
     * Get all subjects
     * @param listener result listener
     * @return true if request was successfully enqueued
     * @throws Exception 
     */
    public boolean requestAllSubjects(RequestAllSubjectsListener listener) throws Exception {
        RequestCallback requestCallback;  
        if (listener == null) {
            throw new Exception("RequestGetAllSubjectsListener must not be null");
        }
        requestCallback = new RequestCallback<RequestAllSubjectsListener>(listener) {  
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                listener.requestGetAllSubjectsFinished(new RequestResult(response.code(), response.body().string()));
            }
        };
        return enqueueRequest(HttpMethod.GET, true, "subject/all/", requestCallback);
    }
    
    /**
     * Get subject count
     * @param listener result listener
     * @return true if request was successfully enqueued
     * @throws Exception 
     */
    public boolean requestSubjectCount(RequestSubjectCountListener listener) throws Exception {
        RequestCallback requestCallback;  
        if (listener == null) {
            throw new Exception("RequestSubjectCountListener must not be null");
        }
        requestCallback = new RequestCallback<RequestSubjectCountListener>(listener) {  
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                listener.requestSubjectCountFinished(new RequestResult(response.code(), response.body().string()));
            }
        };
        return enqueueRequest(HttpMethod.GET, true, "subject/count/", requestCallback);
    }
    
    //OFFERS
    
    public boolean requestNewestOffers(RequestNewestOffersListener listener, int startIndex, int maxCount) throws Exception {
        RequestCallback requestCallback;  
        if (listener == null) {
            throw new RequiredParameterException(listener, "must not be null");
        }
        if (startIndex < 0) {
            throw new ParameterValueException(startIndex, "startIndex must be positiv");
        }
        if (maxCount < 1) {
            throw new ParameterValueException(maxCount, "maxCount must be at least one");
        }
        requestCallback = new RequestCallback<RequestNewestOffersListener>(listener) {  
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                listener.requestGetNewestOffersFinished(new RequestResult(response.code(), response.body().string()));
            }
        };
        return enqueueRequest(HttpMethod.GET, true, "offer/new", requestCallback, new QueryParameter("start", startIndex), new QueryParameter("pageSize", maxCount));
    }
    
    public boolean requestNewestOffers(RequestNewestOffersOfUserListener listener, User user, int startIndex, int maxCount) throws Exception {
        RequestCallback requestCallback;  
        if (listener == null) {
            throw new RequiredParameterException(listener, "must not be null");
        }
        if (user == null) {
            throw new RequiredParameterException(user, "must not be null");
        }
        if (StringUtils.isBlank(user.getUsername())) {
            throw new RequiredParameterException(user.getUsername(), "must not be blank");
        }
        if (startIndex < 0) {
            throw new ParameterValueException(startIndex, "startIndex must be positiv");
        }
        if (maxCount < 1) {
            throw new ParameterValueException(maxCount, "maxCount must be at least one");
        }
        requestCallback = new RequestCallback<RequestNewestOffersOfUserListener>(listener) {  
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                listener.requestGetNewestOffersOfUserFinished(new RequestResult(response.code(), response.body().string()));
            }
        };
        return enqueueRequest(HttpMethod.GET, true, "offer/new/" + user.getUsername(), requestCallback, new QueryParameter("start", startIndex), new QueryParameter("pageSize", maxCount));
    }
    
    public boolean requestOfferCount(RequestOfferCountListener listener) throws Exception {
        RequestCallback requestCallback;  
        if (listener == null) {
            throw new RequiredParameterException(listener, "must not be null");
        }
        requestCallback = new RequestCallback<RequestOfferCountListener>(listener) {  
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                listener.requestOfferCountFinished(new RequestResult(response.code(), response.body().string()));
            }
        };
        return enqueueRequest(HttpMethod.GET, true, "offer/count/", requestCallback);
    }
    
    public boolean requestResetPropertyOfOffer(RequestResetPropertyOfOfferListener listener, int offerId, String... propertynames) throws Exception {
        RequestCallback requestCallback;  
        if (listener == null) {
            throw new RequiredParameterException(listener, "must not be null");
        }
        if (propertynames == null || propertynames.length < 1) {
            throw new RequiredParameterException(propertynames, "must not be empty");
        } 
        requestCallback = new RequestCallback<RequestResetPropertyOfOfferListener>(listener) {  
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                listener.requestResetPropertyOfOfferFinished(new RequestResult(response.code(), response.body().string()));
            }
        };
        return enqueueRequest(HttpMethod.POST, true, "offer/reset/" + offerId, requestCallback, json, dataMapper.toJSON(propertynames));
    }
    
    public boolean requestOfferSearch(RequestOfferSearchListener listener, EntrySearch entrySearch) throws Exception {
        RequestCallback requestCallback;  
        if (listener == null) {
             throw new RequiredParameterException(listener, "must not be null");
        }
        if (entrySearch == null) {
             throw new RequiredParameterException(entrySearch, "must not be null");
        }
        requestCallback = new RequestCallback<RequestOfferSearchListener>(listener) {  
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                listener.requestOfferSearchFinished(new RequestResult(response.code(), response.body().string()));
            }
        };
        return enqueueRequest(HttpMethod.POST, true, "offer/search", requestCallback, json, dataMapper.toJSON(entrySearch));
    }
    
    public boolean requestOfferSearchLocation(RequestOfferSearchLocationListener listener, Location location, double radius) throws Exception {
        RequestCallback requestCallback;  
        if (listener == null) {
             throw new RequiredParameterException(listener, "must not be null");
        }
        if (location == null) {
             throw new RequiredParameterException(location, "must not be null");
        }
        ArrayList<SearchCriteria<EntryProp>> criteria = new ArrayList<>();
        ArrayList<OrderElement<EntryProp>> order = new ArrayList<>();
        criteria.add(new SpatialSearchCriteria<>(EntryProp.LOCATION, SpatialOperation.INSIDE, location, radius));
        EntrySearch search = new EntrySearch(criteria, order);
        requestCallback = new RequestCallback<RequestOfferSearchLocationListener>(listener) {  
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                listener.requestOfferSearchLocationFinished(new RequestResult(response.code(), response.body().string()));
            }
        };
        return enqueueRequest(HttpMethod.POST, true, "offer/search", requestCallback, json, dataMapper.toJSON(search));
    }
    
    public boolean requestUpdateOffer(RequestUpdateOfferListener listener, at.tutoringtrain.adminclient.data.entry.Offer offer) throws Exception {
        RequestCallback requestCallback;  
        if (listener == null) {
            throw new RequiredParameterException(listener, "must not be null");
        }
        if (offer == null) {
            throw new RequiredParameterException(offer, "must not be null");
        }
        requestCallback = new RequestCallback<RequestUpdateOfferListener>(listener) {  
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                listener.requestUpdateOfferFinished(new RequestResult(response.code(), response.body().string()));
            }
        };
        return enqueueRequest(HttpMethod.PUT, true, "offer", requestCallback, json, dataMapper.toJSON(offer, DataMappingViews.Entry.Out.Update.class));
    }
    
    public boolean requestDeleteOffer(RequestDeleteOfferListener listener, at.tutoringtrain.adminclient.data.entry.Offer offer) throws Exception {
        RequestCallback requestCallback;  
        if (listener == null) {
            throw new RequiredParameterException(listener, "must not be null");
        }
        if (offer == null) {
            throw new RequiredParameterException(offer, "must not be null");
        }
        requestCallback = new RequestCallback<RequestDeleteOfferListener>(listener) {  
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                listener.requestDeleteOfferFinished(new RequestResult(response.code(), response.body().string()));
            }
        };
        return enqueueRequest(HttpMethod.DELTE, true, "offer/" + offer.getId(), requestCallback);
    }
    
    public boolean requestOfferSimpleSearch(RequestOfferSimpleSearchListener listener, String query) throws Exception {
        RequestCallback requestCallback;  
        if (listener == null) {
             throw new RequiredParameterException(listener, "must not be null");
        }
        requestCallback = new RequestCallback<RequestOfferSimpleSearchListener>(listener) {  
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                listener.requestOfferSimpleSearchFinished(new RequestResult(response.code(), response.body().string()));
            }
        };
        return enqueueRequest(HttpMethod.POST, true, "offer/simpleSearch", requestCallback, json, "", new QueryParameter("searchStr", query));
    }
    
    //REQUESTS
    
    public boolean requestNewestRequests(RequestNewestRequestsListener listener, int startIndex, int maxCount) throws Exception {
        RequestCallback requestCallback;  
        if (listener == null) {
            throw new RequiredParameterException(listener, "must not be null");
        }
        if (startIndex < 0) {
            throw new ParameterValueException(startIndex, "startIndex must be positiv");
        }
        if (maxCount < 1) {
            throw new ParameterValueException(maxCount, "maxCount must be at least one");
        }
        requestCallback = new RequestCallback<RequestNewestRequestsListener>(listener) {  
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                listener.requestGetNewestRequestsFinished(new RequestResult(response.code(), response.body().string()));
            }
        };
        return enqueueRequest(HttpMethod.GET, true, "request/new", requestCallback, new QueryParameter("start", startIndex), new QueryParameter("pageSize", maxCount));
    }
    
    public boolean requestNewestRequests(RequestNewestRequestsOfUserListener listener, User user, int startIndex, int maxCount) throws Exception {
        RequestCallback requestCallback;  
        if (listener == null) {
            throw new RequiredParameterException(listener, "must not be null");
        }
        if (user == null) {
            throw new RequiredParameterException(user, "must not be null");
        }
        if (StringUtils.isBlank(user.getUsername())) {
            throw new RequiredParameterException(user.getUsername(), "must not be blank");
        }
        if (startIndex < 0) {
            throw new ParameterValueException(startIndex, "startIndex must be positiv");
        }
        if (maxCount < 1) {
            throw new ParameterValueException(maxCount, "maxCount must be at least one");
        }
        requestCallback = new RequestCallback<RequestNewestRequestsOfUserListener>(listener) {  
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                listener.requestGetNewestRequestsOfUserFinished(new RequestResult(response.code(), response.body().string()));
            }
        };
        return enqueueRequest(HttpMethod.GET, true, "request/new/" + user.getUsername(), requestCallback, new QueryParameter("start", startIndex), new QueryParameter("pageSize", maxCount));
    }
    
    public boolean requestRequestCount(RequestRequestCountListener listener) throws Exception {
        RequestCallback requestCallback;  
        if (listener == null) {
            throw new RequiredParameterException(listener, "must not be null");
        }
        requestCallback = new RequestCallback<RequestRequestCountListener>(listener) {  
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                listener.requestRequestCountFinished(new RequestResult(response.code(), response.body().string()));
            }
        };
        
        return enqueueRequest(HttpMethod.GET, true, "request/count/", requestCallback);
    }
    
    public boolean requestResetPropertyOfRequest(RequestResetPropertyOfRequestListener listener, int requestId, String... propertynames) throws Exception {
        RequestCallback requestCallback;  
        if (listener == null) {
            throw new RequiredParameterException(listener, "must not be null");
        }
        if (propertynames == null || propertynames.length < 1) {
            throw new RequiredParameterException(propertynames, "must not be empty");
        } 
        requestCallback = new RequestCallback<RequestResetPropertyOfRequestListener>(listener) {  
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                listener.requestResetPropertyOfRequestFinished(new RequestResult(response.code(), response.body().string()));
            }
        };
        return enqueueRequest(HttpMethod.POST, true, "request/reset/" + requestId, requestCallback, json, dataMapper.toJSON(propertynames));
    }
    
    public boolean requestRequestSearch(RequestRequestSearchListener listener, EntrySearch entrySearch) throws Exception {
        RequestCallback requestCallback;  
        if (listener == null) {
             throw new RequiredParameterException(listener, "must not be null");
        }
        if (entrySearch == null) {
             throw new RequiredParameterException(entrySearch, "must not be null");
        }
        requestCallback = new RequestCallback<RequestRequestSearchListener>(listener) {  
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                listener.requestRequestSearchFinished(new RequestResult(response.code(), response.body().string()));
            }
        };
        return enqueueRequest(HttpMethod.POST, true, "request/search", requestCallback, json, dataMapper.toJSON(entrySearch));
    }
    
    public boolean requestRequestSearchLocation(RequestRequestSearchLocationListener listener, Location location, double radius) throws Exception {
        RequestCallback requestCallback;  
        if (listener == null) {
             throw new RequiredParameterException(listener, "must not be null");
        }
        if (location == null) {
             throw new RequiredParameterException(location, "must not be null");
        }
        ArrayList<SearchCriteria<EntryProp>> criteria = new ArrayList<>();
        ArrayList<OrderElement<EntryProp>> order = new ArrayList<>();
        criteria.add(new SpatialSearchCriteria<>(EntryProp.LOCATION, SpatialOperation.INSIDE, location, radius));
        EntrySearch search = new EntrySearch(criteria, order);
        requestCallback = new RequestCallback<RequestRequestSearchLocationListener>(listener) {  
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                listener.requestRequestSearchLocationFinished(new RequestResult(response.code(), response.body().string()));
            }
        };
        return enqueueRequest(HttpMethod.POST, true, "request/search", requestCallback, json, dataMapper.toJSON(search));
    }
    
    public boolean requestUpdateRequest(RequestUpdateRequestListener listener, at.tutoringtrain.adminclient.data.entry.Request request) throws Exception {
        RequestCallback requestCallback;  
        if (listener == null) {
            throw new RequiredParameterException(listener, "must not be null");
        }
        if (request == null) {
            throw new RequiredParameterException(request, "must not be null");
        }
        requestCallback = new RequestCallback<RequestUpdateRequestListener>(listener) {  
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                listener.requestUpdateRequestFinished(new RequestResult(response.code(), response.body().string()));
            }
        };
        return enqueueRequest(HttpMethod.PUT, true, "request", requestCallback, json, dataMapper.toJSON(request, DataMappingViews.Entry.Out.Update.class));
    }
    
    public boolean requestDeleteRequest(RequestDeleteRequestListener listener, at.tutoringtrain.adminclient.data.entry.Request request) throws Exception {
        RequestCallback requestCallback;  
        if (listener == null) {
            throw new RequiredParameterException(listener, "must not be null");
        }
        if (request == null) {
            throw new RequiredParameterException(request, "must not be null");
        }
        requestCallback = new RequestCallback<RequestDeleteRequestListener>(listener) {  
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                listener.requestDeleteRequestFinished(new RequestResult(response.code(), response.body().string()));
            }
        };
        return enqueueRequest(HttpMethod.DELTE, true, "request/" + request.getId(), requestCallback);
    }
    
    public boolean requestRequestSimpleSearch(RequestRequestSimpleSearchListener listener, String query) throws Exception {
        RequestCallback requestCallback;  
        if (listener == null) {
             throw new RequiredParameterException(listener, "must not be null");
        }
        requestCallback = new RequestCallback<RequestRequestSimpleSearchListener>(listener) {  
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                listener.requestRequestSimpleSearchFinished(new RequestResult(response.code(), response.body().string()));
            }
        };
        return enqueueRequest(HttpMethod.POST, true, "request/simpleSearch", requestCallback, json, "", new QueryParameter("searchStr", query));
    }
    
    //COMMENTS
    
    public boolean requestCommentsOfEntry(RequestCommentsOfEntryListener listener, BigDecimal entryId, EntryType entryType) throws Exception {
        RequestCallback requestCallback;  
        if (listener == null) {
             throw new RequiredParameterException(listener, "must not be null");
        }
        requestCallback = new RequestCallback<RequestCommentsOfEntryListener>(listener) {  
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                listener.requestGetCommentsOfEntryFinished(new RequestResult(response.code(), response.body().string()));
            }
        };
        return enqueueRequest(HttpMethod.GET, true, entryType.toString() + "/" + entryId + "/comments", requestCallback);
    }
    
    public boolean requestDeleteComment(RequestDeleteCommentListener listener, BigDecimal entryId, EntryType entryType, BigDecimal commentId) throws Exception {
        RequestCallback requestCallback;  
        if (listener == null) {
             throw new RequiredParameterException(listener, "must not be null");
        }
        requestCallback = new RequestCallback<RequestDeleteCommentListener>(listener) {  
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                listener.requestDeleteCommentFinished(new RequestResult(response.code(), response.body().string()));
            }
        };
        return enqueueRequest(HttpMethod.DELTE, true, entryType.toString() + "/" + entryId + "/comments/" + commentId, requestCallback);
    }
} 
