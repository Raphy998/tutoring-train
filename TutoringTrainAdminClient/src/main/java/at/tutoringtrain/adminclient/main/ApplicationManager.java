package at.tutoringtrain.adminclient.main;

import at.tutoringtrain.adminclient.data.User;
import at.tutoringtrain.adminclient.datamapper.DataMapper;
import at.tutoringtrain.adminclient.internationalization.Language;
import at.tutoringtrain.adminclient.internationalization.LocalizedValueProvider;
import at.tutoringtrain.adminclient.io.file.FileService;
import at.tutoringtrain.adminclient.io.network.Communicator;
import at.tutoringtrain.adminclient.io.network.UserRole;
import at.tutoringtrain.adminclient.ui.WindowService;
import at.tutoringtrain.adminclient.ui.listener.ApplicationExitListener;
import at.tutoringtrain.adminclient.ui.listener.UserDataChangedListner;
import java.io.IOException;
import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.ResourceBundle;
import java.util.Stack;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author Marco Wilscher marco.wilscher@edu.htl-villach.at
 */
public final class ApplicationManager {
    private static ApplicationManager INSTANCE;

    public static ApplicationManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ApplicationManager();
        }
        return INSTANCE;
    } 

    public static void shutdown() {
        //TODO
        INSTANCE = null;
    }
    
    private final Logger logger;
    private final ApplicationConfiguration applicationConfiguration;
    private final ArrayList<UserDataChangedListner> currentUserDataChangedListeners;
    private final Stack<User> currentUser;
    
    private ApplicationConfiguration temporaryApplicationConfiguration;
    private ApplicationExitListener mainApplicationExitListener;
    
    private ApplicationManager() {
        this.logger = LogManager.getLogger(this);
        this.applicationConfiguration = new ApplicationConfiguration();
        this.currentUserDataChangedListeners = new ArrayList<>();
        this.currentUser = new Stack<>();
        this.mainApplicationExitListener = null;
        this.logger.debug("ApplicationManager initialized");
    }

    public static Communicator getCommunicator() {
        return Communicator.getInstance();
    }

    public static DefaultValueProvider getDefaultValueProvider() {
        return DefaultValueProvider.getINSTANCE();
    }

    public static DataStorage getDataStorage() {
        return DataStorage.getINSTANCE();
    }

    public static FileService getFileService() {
        return FileService.getINSTANCE();
    }

    public ApplicationConfiguration getApplicationConfiguration() {
        return applicationConfiguration;
    }

    public static LocalizedValueProvider getLocalizedValueProvider() {
        return LocalizedValueProvider.getINSTANCE();
    }

    public static DataMapper getDataMapper() {
        return DataMapper.getINSTANCE();
    }

    public static WindowService getWindowService() {
        return WindowService.getInstance();
    }
    
    public static ListItemFactory getListItemFactory() {
        return ListItemFactory.getINSTANCE();
    }

    public String getWebServiceUrl() {
        return getDefaultValueProvider().getDefaultWebServiceProtokoll() + "://" + applicationConfiguration.getServerIp() + ":" + applicationConfiguration.getServerPort() + getDefaultValueProvider().getDefaultWebServiceRootPath();
    }

    public UserRole getMinimumRequiredUserRole() {
        return getDefaultValueProvider().getMinimumRequiredUserRoleForAdminClient();
    }
    
    public ResourceBundle getLanguageResourceBundle() {
        return ResourceBundle.getBundle("bundles.LanguageBundle", applicationConfiguration.getLanguage().getLocale());
    }
    
    public boolean registerMainApplicationExitListener(ApplicationExitListener listener) {
        boolean success = false;
        if (mainApplicationExitListener == null) {
            mainApplicationExitListener = listener;
            success = true;
        }
        return success;
    }
    
    public User getCurrentUser() {
        return currentUser.peek();
    }

    public void setCurrentUser(User user) {
        currentUser.push(user);
        notifyCurrentUserDataChangedListeners();
    }
    
    public User restoreToPreviousCurrentUser() {
        User tmpCurrentUser;
        try {
            tmpCurrentUser = currentUser.pop();
            notifyCurrentUserDataChangedListeners();
        } catch (EmptyStackException esex) {
            tmpCurrentUser = null;
        }
        return tmpCurrentUser;
    }
    
    public boolean isCurrentUser(String username) {
        return getCurrentUser().getUsername().equals(username);
    }
    
    public boolean addCurrentUserDataChangedListener(UserDataChangedListner listener) {
        return currentUserDataChangedListeners.add(listener);
    }
    
    public boolean removeCurrentUserDataChangedListener(UserDataChangedListner listener) {
        return currentUserDataChangedListeners.remove(listener);
    }
    
    private void notifyCurrentUserDataChangedListeners() {
        currentUserDataChangedListeners.forEach((listener) -> listener.userDataChanged(currentUser.peek()));
    }
    
    public Language getLanguage() {
        return applicationConfiguration.getLanguage();
    }
    
    public void setLanguage(Language language) {
        applicationConfiguration.setLanguage(language);
    }

    public void setTemporaryApplicationConfiguration(ApplicationConfiguration temporaryApplicationConfiguration) {
        this.temporaryApplicationConfiguration = temporaryApplicationConfiguration;
    }
    
    public boolean isTokenFileAvailable() {
        return getFileService().isTokenFileAvailable();
    }

    public boolean isConfigFileAvailable() {
        return getFileService().isConfigFileAvailable();
    }
    
    public boolean readConfigFile() {
        boolean success = false; 
        if (isConfigFileAvailable()) {
            success = applicationConfiguration.apply(getFileService().readConfig());
        }
        return success;
    }
    
    public String readTokenFile() throws Exception {
        String value;
        value = null;
        if (isTokenFileAvailable()) {
            value = getFileService().readTokenFile();
        }
        return value;
    }

    public void writeTokenFile(String token) throws Exception {
        getFileService().writeTokenFile(token);
    }
    
    public void writeConfigFile() throws IOException {
        getFileService().writeConfig(applicationConfiguration);
    }
    
    public void writeTemporaryConfigFile() throws IOException {
        if (temporaryApplicationConfiguration != null) {
            getFileService().writeConfig(temporaryApplicationConfiguration);
        }
    }
    
    public boolean deleteConfigFile() {
        return getFileService().deleteConfigFile();
    }
    
    public boolean deleteTokenFile() {
        return getFileService().deleteTokenFile();
    }    
}
