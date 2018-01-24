package at.tutoringtrain.adminclient.ui.controller;

import at.tutoringtrain.adminclient.data.user.Gender;
import at.tutoringtrain.adminclient.data.user.User;
import at.tutoringtrain.adminclient.internationalization.LocalizedValueProvider;
import at.tutoringtrain.adminclient.io.network.Communicator;
import at.tutoringtrain.adminclient.io.network.RequestResult;
import at.tutoringtrain.adminclient.io.network.listener.user.RequestRegisterUserListener;
import at.tutoringtrain.adminclient.main.ApplicationManager;
import at.tutoringtrain.adminclient.main.DataStorage;
import at.tutoringtrain.adminclient.main.DefaultValueProvider;
import at.tutoringtrain.adminclient.main.MessageCodes;
import at.tutoringtrain.adminclient.main.MessageContainer;
import at.tutoringtrain.adminclient.ui.TutoringTrainWindow;
import at.tutoringtrain.adminclient.ui.WindowService;
import at.tutoringtrain.adminclient.ui.validators.EmailFieldValidator;
import at.tutoringtrain.adminclient.ui.validators.TextFieldValidator;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXSnackbar;
import com.jfoenix.controls.JFXSpinner;
import com.jfoenix.controls.JFXTextField;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * FXML Controller class
 * Register User
 * @author Marco Wilscher marco.wilscher@edu.htl-villach.at
 */
public class RegisterUserController implements Initializable, TutoringTrainWindow, RequestRegisterUserListener {
    @FXML
    private AnchorPane pane;
    @FXML
    private JFXTextField txtUsername;
    @FXML
    private JFXTextField txtName;
    @FXML
    private JFXTextField txtEmail;
    @FXML
    private JFXTextField txtEducation;
    @FXML
    private JFXComboBox<Gender> comboGender;
    @FXML
    private JFXButton btnRegister;
    @FXML
    private JFXButton btnClose;
    @FXML
    private JFXSpinner spinner;
    
    private JFXSnackbar snackbar;    
    private LocalizedValueProvider localizedValueProvider;
    private Logger logger; 
    private DataStorage dataStorage;
    private Communicator communicator;
    private WindowService windowService;
    private DefaultValueProvider defaultValueProvider; 
    private TextFieldValidator validatorUsernameField;
    private TextFieldValidator validatorNameField;
    private EmailFieldValidator validatorEmailField;
    private TextFieldValidator validatorEducationField;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        logger = LogManager.getLogger(this);
        dataStorage = ApplicationManager.getDataStorage();
        communicator = ApplicationManager.getCommunicator();
        windowService = ApplicationManager.getWindowService();
        defaultValueProvider = ApplicationManager.getDefaultValueProvider();
        localizedValueProvider = ApplicationManager.getLocalizedValueProvider();
        initializeControls();
        initializeControlValidators();
        logger.debug("RegisterUserController initialized");
    }  
    
    private void initializeControls() {
        snackbar = new JFXSnackbar(pane);       
        spinner.setVisible(false);
        comboGender.setItems(FXCollections.observableArrayList(dataStorage.getGenders().values()));
        comboGender.getSelectionModel().selectFirst();
    }
    
    private void initializeControlValidators() {
        validatorNameField = new TextFieldValidator(defaultValueProvider.getDefaultValidationPattern("name"));
        validatorEmailField = new EmailFieldValidator(defaultValueProvider.getDefaultValidationPattern("email"));
        validatorEducationField = new TextFieldValidator(defaultValueProvider.getDefaultValidationPattern("education"));
        validatorUsernameField = new TextFieldValidator(defaultValueProvider.getDefaultValidationPattern("username"));
        txtUsername.getValidators().add(validatorUsernameField);
        txtName.getValidators().add(validatorNameField);
        txtEmail.getValidators().add(validatorEmailField);
        txtEducation.getValidators().add(validatorEducationField);  
    }

    private boolean validateInputControls() {
        boolean isValid;
        txtUsername.validate();
        txtName.validate();
        txtEmail.validate();
        txtEducation.validate();   
        isValid = !(validatorUsernameField.getHasErrors() || validatorNameField.getHasErrors() || validatorEmailField.getHasErrors() || validatorEducationField.getHasErrors());
        return isValid;
    }
    
    private void clearInputControls() {
        Platform.runLater(() -> {
            txtUsername.setText("");
            txtName.setText("");
            txtEmail.setText("");
            txtEducation.setText("");
            comboGender.getSelectionModel().selectFirst();
        });
    }
    
    private void disableControls(boolean disable) {
        Platform.runLater(() -> {
            txtUsername.setDisable(disable);
            txtName.setDisable(disable);
            txtEmail.setDisable(disable);
            txtEducation.setDisable(disable);
            comboGender.setDisable(disable);    
            btnRegister.setDisable(disable);
            btnClose.setDisable(disable);       
            spinner.setVisible(disable);
        });
    }
    
    private String getUsername() {
        return txtUsername.getText();
    }
    
    private String getName() {
        return txtName.getText();
    }
    
    private String getEmail() {
        return txtEmail.getText();
    }
    
    private String getEducation() {
        return txtEducation.getText();
    }
    
    private Character getGender() {
        return comboGender.getSelectionModel().getSelectedItem().getCode();
    }
    
    @FXML
    void onBtnRegister(ActionEvent event) {        
        try {
            if (validateInputControls()) {
                disableControls(true);
                communicator.requestRegisterUser(this, new User(getUsername(), getName(), getGender(), null, getEmail(), getEducation()));            
            }    
        } catch (Exception ex) {
            disableControls(false);
            displayMessage(new MessageContainer(MessageCodes.EXCEPTION, localizedValueProvider.getString("messageUnexpectedFailure")));
            logger.error("onBtnRegister", ex);
        }
    }   
    
    @FXML
    void onBtnClose(ActionEvent event) {
        closeWindow();
    }

    @Override
    public void displayMessage(MessageContainer container) {
        windowService.displayMessage(snackbar, container);
    }
    
    @Override
    public void closeWindow() {
        windowService.closeWindow(pane);
    }

    @Override
    public void requestRegisterUserFinished(RequestResult result) {
        disableControls(false);
        try {
            if (result.isSuccessful()) {
                displayMessage(new MessageContainer(MessageCodes.OK, localizedValueProvider.getString("messageUserSuccessfullyRegistered")));
                clearInputControls();
            } else {
                displayMessage(result.getMessageContainer());
            }
        } catch (Exception ex) {
            displayMessage(new MessageContainer(MessageCodes.EXCEPTION, localizedValueProvider.getString("messageUnexpectedFailure")));
            logger.error("requestRegisterUserFinished", ex);
        }
    }

    @Override
    public void requestFailed(RequestResult result) {
        disableControls(false);
        ApplicationManager.getHostFallbackService().requestCheck();
        displayMessage(new MessageContainer(MessageCodes.REQUEST_FAILED, localizedValueProvider.getString("messageConnectionFailed")));
        logger.error(result.getMessageContainer().toString());
    }
}
