package at.bsd.tutoringtrain.data;

import at.bsd.tutoringtrain.controller.MainController;
import at.bsd.tutoringtrain.data.configuration.Configuration;
import at.bsd.tutoringtrain.data.entities.User;
import at.bsd.tutoringtrain.data.enumeration.Gender;
import at.bsd.tutoringtrain.debugging.MessageLogger;
import at.bsd.tutoringtrain.io.file.FileService;
import at.bsd.tutoringtrain.language.Language;
import at.bsd.tutoringtrain.ui.listener.UserDataChangedListner;
import java.io.IOException;
import java.util.ArrayList;

/**
 *
 * @author Marco Wilscher marco.wilscher@edu.htl-villach.at
 */
public class Database {
    private static Database INSTANCE;
    
    public static Database getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Database();
        }
        return INSTANCE;
    } 
    
    private final MessageLogger logger;
    private final FileService fileService;
    
    private Configuration config;
    private User currentUser, backupCurrentUser;
    private ArrayList<Gender> genders;
    private MainController mainController;
    private ArrayList<UserDataChangedListner> userChangedListners;
    
    private Database() {
        logger = MessageLogger.getINSTANCE();
        fileService = FileService.getINSTANCE();
        userChangedListners = new ArrayList<>();
        config = new Configuration();
        genders = new ArrayList();
        currentUser = null;
        genders.add(Gender.MALE);
        genders.add(Gender.FEMALE);
        genders.add(Gender.OTHER);
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
        notifyUserChangedListeners();
    }

    public ArrayList<Gender> getGenders() {
        return genders;
    }

    public void setGenders(ArrayList<Gender> genders) {
        this.genders = genders;
    }

    public Configuration getConfig() {
        return config;
    }

    private void setConfig(Configuration config) {
        this.config = config;
    }
    
    public Language getLanguage() {
        return config.getLanguage();
    }
    
    public void setLanguage(Language language) {
        config.setLanguage(language);
    }
    
    public boolean isTokenFileAvailable() {
        return fileService.isTokenFileAvailable();
    }
    
    public boolean isConfigFileAvailable() {
        return fileService.isConfigFileAvailable();
    }
    
    /**
     * 
     */
    public void readConfigFile() {
        setConfig(fileService.readConfig());
    }
    
    /**
     * 
     * @return
     * @throws IOException 
     */
    public String readTokenFile() throws Exception {
        String value;
        value = null;
        if (isTokenFileAvailable()) {
            value = fileService.readTokenFile();
        }
        return value;
    }
    
    /**
     * 
     * @param token
     * @throws IOException 
     */
    public void writeTokenFile(String token) throws Exception {
        fileService.writeTokenFile(token);
    }
    
    /**
     * 
     * @throws IOException 
     */
    public void writeConfigFile() throws IOException {
        fileService.writeConfig(config);
    }
    
    /**
     * 
     * @return 
     */
    public boolean deleteConfigFile() {
        return fileService.deleteConfigFile();
    }
    
    /**
     * 
     * @return 
     */
    public boolean deleteTokenFile() {
        return fileService.deleteTokenFile();
    }
    
    public void backupCurrentUser() {
        backupCurrentUser = new User(currentUser.getUsername(), currentUser.getName(), currentUser.getGender(), currentUser.getPassword(), currentUser.getEmail(), currentUser.getEducation());
    }
    
    public void restoreCurrentUser() {
        setCurrentUser(backupCurrentUser);
    }
    
    public void addUserChangedListener(UserDataChangedListner listener) {
        userChangedListners.add(listener);
    }
    
    private void notifyUserChangedListeners() {
        userChangedListners.forEach((l) -> l.userDataChanged(currentUser));
    }

    public MainController getMainController() {
        return mainController;
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }
}
