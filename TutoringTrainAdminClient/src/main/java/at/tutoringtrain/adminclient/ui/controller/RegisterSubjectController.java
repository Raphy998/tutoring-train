package at.tutoringtrain.adminclient.ui.controller;

import at.tutoringtrain.adminclient.data.subject.Subject;
import at.tutoringtrain.adminclient.internationalization.LocalizedValueProvider;
import at.tutoringtrain.adminclient.io.network.Communicator;
import at.tutoringtrain.adminclient.io.network.RequestResult;
import at.tutoringtrain.adminclient.io.network.listener.subject.RequestRegisterSubjectListener;
import at.tutoringtrain.adminclient.main.ApplicationManager;
import at.tutoringtrain.adminclient.main.DefaultValueProvider;
import at.tutoringtrain.adminclient.main.MessageCodes;
import at.tutoringtrain.adminclient.main.MessageContainer;
import at.tutoringtrain.adminclient.ui.TutoringTrainWindow;
import at.tutoringtrain.adminclient.ui.WindowService;
import at.tutoringtrain.adminclient.ui.validators.TextFieldValidator;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXSnackbar;
import com.jfoenix.controls.JFXSpinner;
import com.jfoenix.controls.JFXTextField;
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
public class RegisterSubjectController implements Initializable, TutoringTrainWindow, RequestRegisterSubjectListener {
    @FXML
    private AnchorPane pane;
    @FXML
    private JFXTextField txtEName;
    @FXML
    private JFXTextField txtDName;
    @FXML
    private JFXButton btnRegister;
    @FXML
    private JFXButton btnClose;
    @FXML
    private JFXSpinner spinner;
    
    private JFXSnackbar snackbar;
    
    private LocalizedValueProvider localizedValueProvider;
    private Logger logger; 
    private Communicator communicator;
    private WindowService windowService;
    private DefaultValueProvider defaultValueProvider;
    
    private TextFieldValidator validatorEName;
    private TextFieldValidator validatorDEame;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        logger = LogManager.getLogger(this);
        communicator = ApplicationManager.getCommunicator();
        windowService = ApplicationManager.getWindowService();
        defaultValueProvider = ApplicationManager.getDefaultValueProvider();
        localizedValueProvider = ApplicationManager.getLocalizedValueProvider();
        initializeControls();
        initializeControlValidators();
        logger.debug("RegisterSubjectController initialized");
    } 
    
    private void initializeControls() {
        snackbar = new JFXSnackbar(pane);       
        spinner.setVisible(false);
    }
    
    private void initializeControlValidators() {
        validatorDEame = new TextFieldValidator(defaultValueProvider.getDefaultValidationPattern("subjectname"));  
        validatorEName = new TextFieldValidator(defaultValueProvider.getDefaultValidationPattern("subjectname"));
        txtDName.getValidators().add(validatorDEame);
        txtEName.getValidators().add(validatorEName);
    }

    private boolean validateInputControls() {
        boolean isValid;
        txtDName.validate();
        txtEName.validate();  
        isValid = !(validatorDEame.getHasErrors() || validatorEName.getHasErrors());
        return isValid;
    }
    
    private void clearInputControls() {
        Platform.runLater(() -> {
            txtEName.setText("");
            txtDName.setText("");
        });
    }
    
    private void disableControls(boolean disable) {
        Platform.runLater(() -> {
            txtDName.setDisable(disable);
            txtEName.setDisable(disable);  
            btnRegister.setDisable(disable);
            btnClose.setDisable(disable);       
            spinner.setVisible(disable);
        });
    }
    
    private String getDEName() {
        return txtDName.getText();
    }
    
    private String getENName() {
        return txtEName.getText();
    }
    
    @Override
    public void displayMessage(MessageContainer container) {
        windowService.displayMessage(snackbar, container);
        logger.debug(container.toString());
    }
    
    @Override
    public void closeWindow() {
        windowService.closeWindow(pane);
    }
    
    @FXML
    void onBtnClose(ActionEvent event) {
        windowService.closeWindow(pane);
    }

    @FXML
    void onBtnRegister(ActionEvent event) {
        try {
            if (validateInputControls()) {
                disableControls(true);
                communicator.requestRegisterSubject(this,  new Subject(getENName(), getDEName()));            
            }    
        } catch (Exception ex) {
            disableControls(false);
            displayMessage(new MessageContainer(MessageCodes.EXCEPTION, localizedValueProvider.getString("messageUnexpectedFailure")));
            logger.error("onBtnRegister: exception occured", ex);
        }
    }

    @Override
    public void requestRegisterSubjectFinished(RequestResult result) {
        disableControls(false);
        try {
            if (result.isSuccessful()) {
                displayMessage(new MessageContainer(MessageCodes.OK, localizedValueProvider.getString("messageSubjectSuccessfullyRegistered")));
                clearInputControls();
            } else {
                displayMessage(result.getMessageContainer());
            }
        } catch (Exception ex) {
            logger.error("requestRegisterSubjectFinished: Exception occurred", ex);
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
