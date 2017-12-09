package at.tutoringtrain.adminclient.ui.controller;

import at.tutoringtrain.adminclient.data.user.Gender;
import at.tutoringtrain.adminclient.data.mapper.DataMapper;
import at.tutoringtrain.adminclient.data.mapper.DataMappingViews;
import at.tutoringtrain.adminclient.internationalization.LocalizedValueProvider;
import at.tutoringtrain.adminclient.io.network.Communicator;
import at.tutoringtrain.adminclient.io.network.Credentials;
import at.tutoringtrain.adminclient.io.network.RequestResult;
import at.tutoringtrain.adminclient.io.network.listener.user.RequestAuthenticateListner;
import at.tutoringtrain.adminclient.io.network.listener.user.RequestGetGendersListener;
import at.tutoringtrain.adminclient.io.network.listener.user.RequestOwnUserListener;
import at.tutoringtrain.adminclient.io.network.listener.user.RequestTokenValidationListener;
import at.tutoringtrain.adminclient.main.ApplicationManager;
import at.tutoringtrain.adminclient.main.DataStorage;
import at.tutoringtrain.adminclient.main.DefaultValueProvider;
import at.tutoringtrain.adminclient.main.MessageCodes;
import at.tutoringtrain.adminclient.main.MessageContainer;
import at.tutoringtrain.adminclient.security.PasswordHashGenerator;
import at.tutoringtrain.adminclient.ui.TutoringTrainWindow;
import at.tutoringtrain.adminclient.ui.WindowService;
import at.tutoringtrain.adminclient.ui.validators.TextFieldValidator;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXSnackbar;
import com.jfoenix.controls.JFXSpinner;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXToggleButton;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * FXML Controller class
 *
 * @author Marco Wilscher marco.wilscher@edu.htl-villach.at
 */
public class AuthenticationController implements Initializable, TutoringTrainWindow, RequestAuthenticateListner, RequestTokenValidationListener, RequestOwnUserListener, RequestGetGendersListener {
    @FXML
    private AnchorPane pane;
    @FXML
    private JFXTextField txtUsername;
    @FXML
    private JFXPasswordField txtPassword;
    @FXML
    private JFXButton btnLogin;
    @FXML
    private JFXSpinner spinner;
    @FXML
    private JFXToggleButton remember;
    
    private JFXSnackbar snackbar; 
    
    private ApplicationManager applicationManager;
    private Logger logger; 
    private Communicator communicator;
    private WindowService windowService;
    private LocalizedValueProvider localizedValueProvider;
    private DataMapper dataMapper;
    private DataStorage dataStorage;
    private DefaultValueProvider defaultValueProvider;
       
    private TextFieldValidator validatorUsernameField;
    private TextFieldValidator validatorPasswordField;
    private boolean loadedOwnUser;
    private boolean loadedGenders;
   
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        logger = LogManager.getLogger(this);
        applicationManager = ApplicationManager.getInstance();      
        communicator = ApplicationManager.getCommunicator();
        windowService = ApplicationManager.getWindowService();
        localizedValueProvider = ApplicationManager.getLocalizedValueProvider();
        dataStorage = ApplicationManager.getDataStorage();
        dataMapper = ApplicationManager.getDataMapper();
        defaultValueProvider = ApplicationManager.getDefaultValueProvider();
        initializeControls();
        initializeControlValidators();
        logger.debug("AuthenticationController initialized");
        tryLoginWithStoredToken();
    }  
    
    private void initializeControls() {
        snackbar = new JFXSnackbar(pane);       
        spinner.setVisible(false);       
    }
    
    private void initializeControlValidators() {
        validatorUsernameField =  new TextFieldValidator(defaultValueProvider.getDefaultValidationPattern("username"));
        validatorPasswordField =  new TextFieldValidator(defaultValueProvider.getDefaultValidationPattern("password"));
        txtUsername.getValidators().add(validatorUsernameField);
        txtPassword.getValidators().add(validatorPasswordField);
    }

    private void tryLoginWithStoredToken() {
        disableControls(true);
        if (applicationManager.isTokenFileAvailable()) {
            try {
                communicator.requestTokenValidation(this, applicationManager.readTokenFile());
            } catch (Exception ex) {
                logger.error("Token validation failed", ex);
                disableControls(false);
            }
        } else {
            disableControls(false);
        }
    }
    
    private boolean validateInputControls() {
        boolean isValid;
        txtUsername.validate();
        txtPassword.validate();
        isValid = !(validatorUsernameField.getHasErrors() || validatorPasswordField.getHasErrors());
        return isValid;
    }
    
    private String getHashedPassword() {
        return PasswordHashGenerator.generate(txtPassword.getText());
    }
    
    private String getUsername() {
        return txtUsername.getText();
    }
    
    private void disableControls(boolean disable) {
        Platform.runLater(() -> {
            txtUsername.setDisable(disable);
            txtPassword.setDisable(disable);   
            btnLogin.setDisable(disable);
            remember.setDisable(disable);         
            spinner.setVisible(disable);
        });
    }

    @Override
    public void displayMessage(MessageContainer container) {
        Platform.runLater(() -> {
            snackbar.enqueue(new JFXSnackbar.SnackbarEvent(container.toString()));
            logger.debug(container.toString());
        });
    }
    
    @Override
    public void closeWindow() {
        windowService.closeWindow(pane);
    }
    
    private void loadGender() throws Exception {
        communicator.requestGetGenders(this);
        loadedGenders = false;
    }
    
    private void loadOwnUser() throws Exception {
        communicator.requestOwnUser(this);
        loadedOwnUser = false;
    }
    
    private void openMainWindow() {
        Platform.runLater(() -> {
            try {
                windowService.openMainWindow(this);
            } catch (IOException ioex) {
                disableControls(false);
                logger.error("Opening main window failed", ioex);
            }
        });
    }
    
    @FXML
    void onBtnLogin(ActionEvent event) {
        try {
            if (validateInputControls()) {
                disableControls(true);
                communicator.requestAuthenticate(this, new Credentials(getUsername(), getHashedPassword()), remember.isSelected());
            }
        } catch (Exception ex) {
            disableControls(false);
            logger.error("Login failed", ex);
        }
    }
    
    @FXML
    void onBtnDebugSettings(ActionEvent event) {
        try {
            windowService.openSettingsWindow(true);
        } catch (Exception ex) {
            logger.error("Login failed", ex);
        }
    }
    
    @Override
    public void requestAuthenticateFinished(RequestResult result) {
        if (result.isSuccessful()) {
            try {
                loadGender();
                loadOwnUser();
            } catch (Exception ex) {
                logger.error("Loading user or loading genders failed", ex);
                disableControls(false);
                displayMessage(new MessageContainer(MessageCodes.LOADING_FAILED, localizedValueProvider.getString("messageLoginFailedTryAgainLater")));
            }   
        } else {
            disableControls(false);
            displayMessage(result.getMessageContainer());           
        }
    }

    @Override
    public void requestTokenValidationFinished(RequestResult result) {
        if (result.isSuccessful()) {
            try {
                loadGender();
                loadOwnUser();
            } catch (Exception ex) {
                logger.error("Loading user or loading genders failed", ex);
                disableControls(false);
                displayMessage(new MessageContainer(MessageCodes.LOADING_FAILED, localizedValueProvider.getString("messageLoginFailedTryAgainLater")));
            }  
        } else {
            disableControls(false);
            displayMessage(result.getMessageContainer());           
        }
    }
    
    @Override
    public void requestGetGendersFinished(RequestResult result) {
        if (result.isSuccessful()) {
            dataStorage.clearGenders();
            try {
                for (Gender gender : dataMapper.toGenderArrayList(result.getData())) {
                    dataStorage.addGender(gender);
                }
            } catch (IOException ex) {
                logger.debug("Mapping genders failed! Using default genders");
                for (Gender gender: defaultValueProvider.getDefaultGenders().values()) {
                    dataStorage.addGender(gender);
                }
            }
            loadedGenders = true;
            if (loadedOwnUser && loadedGenders) {
                openMainWindow();
            }
        } else {
            disableControls(false);
            logger.debug("get genders failed");
            logger.error(result.getMessageContainer());
        }
    }

    @Override
    public void requestOwnUserFinished(RequestResult result) {
        if (result.isSuccessful()) {
            try {
                applicationManager.setCurrentUser(ApplicationManager.getDataMapper().toUser(result.getData(), DataMappingViews.User.In.Get.class));
                loadedOwnUser = true;
            } catch (IOException ex) {
                logger.error("Mapping own user failed");
            }
            if (loadedOwnUser && loadedGenders) {
                openMainWindow();
            }
        } else {
            disableControls(false);
            logger.debug("get own user failed");
            logger.error(result.getMessageContainer());
        }
    }
    
    @Override
    public void requestFailed(RequestResult result) {
        disableControls(false);
        displayMessage(new MessageContainer(MessageCodes.REQUEST_FAILED, localizedValueProvider.getString("messageUnexpectedFailure")));
        logger.error("Request failed with status code:" + result.getStatusCode());
        logger.error(result.getMessageContainer().toString());
    }
}
