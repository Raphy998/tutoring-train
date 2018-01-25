package at.tutoringtrain.adminclient.ui.controller;

import at.tutoringtrain.adminclient.io.network.Communicator;
import at.tutoringtrain.adminclient.io.network.Credentials;
import at.tutoringtrain.adminclient.io.network.RequestResult;
import at.tutoringtrain.adminclient.io.network.WebserviceOperation;
import at.tutoringtrain.adminclient.io.network.listener.user.RequestReauthenticateListner;
import at.tutoringtrain.adminclient.main.ApplicationManager;
import at.tutoringtrain.adminclient.main.DefaultValueProvider;
import at.tutoringtrain.adminclient.main.MessageCodes;
import at.tutoringtrain.adminclient.main.MessageContainer;
import at.tutoringtrain.adminclient.security.PasswordHashGenerator;
import at.tutoringtrain.adminclient.ui.TutoringTrainWindow;
import at.tutoringtrain.adminclient.ui.WindowService;
import at.tutoringtrain.adminclient.ui.listener.ReauthenticationListener;
import at.tutoringtrain.adminclient.ui.validators.TextFieldValidator;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXSnackbar;
import com.jfoenix.controls.JFXSpinner;
import com.jfoenix.controls.JFXTextField;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * FXML Controller class
 *
 * @author Marco Wilscher marco.wilscher@edu.htl-villach.at
 */
public class ReauthenticationController implements Initializable, TutoringTrainWindow, RequestReauthenticateListner {
    @FXML
    private AnchorPane pane;
    @FXML
    private JFXTextField txtUsername;
    @FXML
    private JFXPasswordField txtPassword;
    @FXML
    private JFXButton btnAuthenticate;
    @FXML
    private JFXButton btnExit;
    @FXML
    private JFXSpinner spinner;
    @FXML
    private Label lblTitle;

    private JFXSnackbar snackbar; 
    private Communicator communicator;
    private Logger logger;
    private ApplicationManager database;
    private WindowService windowService;
    
    private TextFieldValidator validatorPasswordField;
    
    private ReauthenticationListener reauthenticationListener;
    private WebserviceOperation webserviceOperation;
            
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            logger = LogManager.getLogger(this);
            logger.debug("initialization started");  
            database = ApplicationManager.getInstance();
            communicator = Communicator.getInstance();
            windowService = WindowService.getInstance();
            
            initializeControls();
            setCurrentUsername();
            logger.debug("initialization finished");
        } catch (Exception ex) {
            disableControls(false);
            logger.error("initialization failed", ex);
        }
    }    
    
    private void initializeControls() {
        snackbar = new JFXSnackbar(pane);       
        spinner.setVisible(false);
        logger.debug("ui controls initialized");
        initializeControlValidators();
    }
    
    private void initializeControlValidators() {
        validatorPasswordField =  new TextFieldValidator(DefaultValueProvider.getINSTANCE().getDefaultValidationPattern("name"));
        txtPassword.getValidators().add(validatorPasswordField);
        logger.debug("input validators initialized");
    }
    
    private boolean validateInputControls() {
        boolean isValid;
        txtPassword.validate();
        isValid = !(validatorPasswordField.getHasErrors());
        logger.debug("input validation result: " + (isValid ? "valid" : "invalid"));
        return isValid;
    }
    
    private void setCurrentUsername() {
        Platform.runLater(() -> txtUsername.setText(database.getCurrentUser().getUsername()));
        logger.debug("set current username");
    }
    
    private String getHashedPassword() {
        return PasswordHashGenerator.generate(txtPassword.getText());
    }
    
    private String getUsername() {
        return txtUsername.getText();
    }

    public void setReauthenticationListener(ReauthenticationListener reauthenticationListener) {
        this.reauthenticationListener = reauthenticationListener;
    }

    public void setWebserviceOperation(WebserviceOperation webserviceOperation) {
        this.webserviceOperation = webserviceOperation;
    }

    private void disableControls(boolean disable) {
        Platform.runLater(() -> {
            txtUsername.setDisable(disable);
            txtPassword.setDisable(disable);   
            btnAuthenticate.setDisable(disable);
            btnExit.setDisable(disable);         
            spinner.setVisible(disable);
        });
    }
    
    @Override
    public void displayMessage(MessageContainer container) {
        Platform.runLater(() -> {
            logger.debug(container.toString());
            snackbar.enqueue(new JFXSnackbar.SnackbarEvent(container.toString()));
        });
    }
    
    @Override
    public void closeWindow() {
        logger.debug("closing reauthentification window");
        windowService.closeWindow(pane);
    }
    
    @FXML
    void onBtnAuthenticate(ActionEvent event) {
         try {
            logger.debug("authentification using credentials started");
            if (validateInputControls()) {
                disableControls(true);
                communicator.requestReauthenticate(this, new Credentials(getUsername(), getHashedPassword()));
            }
        } catch (Exception ex) {
            disableControls(false);
            logger.error("login failed", ex);
        }
    }

    @FXML
    void onBtnExit(ActionEvent event) {
        if (reauthenticationListener != null) {
            reauthenticationListener.reauthenticationCanceled();
        }
    }

    @Override
    public void requestReauthenticateFinished(RequestResult result) {
        if (result.isSuccessful()) {
            logger.debug("authentification successful");
            Platform.runLater(() -> {
                closeWindow();
                reauthenticationListener.reauthenticationSuccessful(webserviceOperation);
            });
        } else {
            logger.debug("authentification failed");
            disableControls(false);
            displayMessage(result.getMessageContainer());           
        }
    }
    
    @Override
    public void requestFailed(RequestResult result) {
        disableControls(false);
        displayMessage(new MessageContainer(MessageCodes.REQUEST_FAILED, "Something unexpected happended! (see log for further information)"));
        logger.error("request failed (" + result.getStatusCode() + ")");
        logger.debug(result.getMessageContainer().toString());
    }
}
