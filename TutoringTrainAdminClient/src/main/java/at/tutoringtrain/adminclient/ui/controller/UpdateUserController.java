package at.tutoringtrain.adminclient.ui.controller;

import at.tutoringtrain.adminclient.data.Gender;
import at.tutoringtrain.adminclient.data.User;
import at.tutoringtrain.adminclient.exception.RequiredParameterException;
import at.tutoringtrain.adminclient.internationalization.LocalizedValueProvider;
import at.tutoringtrain.adminclient.io.network.Communicator;
import at.tutoringtrain.adminclient.io.network.RequestResult;
import at.tutoringtrain.adminclient.io.network.WebserviceOperation;
import at.tutoringtrain.adminclient.io.network.listener.user.RequestGetAvatarListener;
import at.tutoringtrain.adminclient.io.network.listener.user.RequestResetAvatarListener;
import at.tutoringtrain.adminclient.io.network.listener.user.RequestUnblockUserListener;
import at.tutoringtrain.adminclient.io.network.listener.user.RequestUpdateAvatarListener;
import at.tutoringtrain.adminclient.io.network.listener.user.RequestUpdateOwnUserListener;
import at.tutoringtrain.adminclient.io.network.listener.user.RequestUpdateUserListener;
import at.tutoringtrain.adminclient.main.ApplicationManager;
import at.tutoringtrain.adminclient.main.DataStorage;
import at.tutoringtrain.adminclient.main.DefaultValueProvider;
import at.tutoringtrain.adminclient.main.MessageCodes;
import at.tutoringtrain.adminclient.main.MessageContainer;
import at.tutoringtrain.adminclient.security.PasswordGenerator;
import at.tutoringtrain.adminclient.ui.TutoringTrainWindowWithReauthentication;
import at.tutoringtrain.adminclient.ui.WindowService;
import at.tutoringtrain.adminclient.ui.listener.ReauthenticationListener;
import at.tutoringtrain.adminclient.ui.listener.UserAvatarChangedListner;
import at.tutoringtrain.adminclient.ui.listener.UserBlockListner;
import at.tutoringtrain.adminclient.ui.listener.UserDataChangedListner;
import at.tutoringtrain.adminclient.ui.validators.EmailFieldValidator;
import at.tutoringtrain.adminclient.ui.validators.TextFieldValidator;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXSnackbar;
import com.jfoenix.controls.JFXSpinner;
import com.jfoenix.controls.JFXTextField;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javax.imageio.ImageIO;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * FXML Controller class
 *
 * @author Marco Wilscher marco.wilscher@edu.htl-villach.at
 */
public class UpdateUserController implements Initializable, TutoringTrainWindowWithReauthentication, ReauthenticationListener, UserBlockListner, RequestUpdateUserListener, RequestUpdateOwnUserListener, RequestGetAvatarListener, RequestUpdateAvatarListener, RequestUnblockUserListener, RequestResetAvatarListener {
    @FXML
    private AnchorPane pane;
    @FXML
    private JFXSpinner spinner;
    @FXML
    private Label lblTitle;
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
    private JFXPasswordField txtPassword;
    @FXML
    private JFXButton btnRandomPassword;
    @FXML
    private Label lblRandomPassword;
    @FXML
    private JFXButton btnUpdate;
    @FXML
    private JFXButton btnClose;
    @FXML
    private JFXButton btnBlock;
    @FXML
    private JFXButton btnUnblock;
    @FXML
    private ImageView ivAvatar;
    @FXML
    private JFXSpinner spinnerAvatar;
    @FXML
    private JFXButton btnSelectAvatar;
    @FXML
    private JFXButton btnRemoveAvatar;

    private JFXSnackbar snackbar;
    
    private LocalizedValueProvider localizedValueProvider;
    private DefaultValueProvider defaultValueProvider;
    private Logger logger; 
    private DataStorage dataStorage;
    private Communicator communicator;
    private WindowService windowService;
    private ApplicationManager applicationManager;
    
    private TextFieldValidator validatorNameField;
    private EmailFieldValidator validatorEmailField;
    private TextFieldValidator validatorEducationField;
    
    private ReauthenticationListener reauthenticationListener;
    private UserDataChangedListner userDataChangedListner;
    private UserAvatarChangedListner userAvatarChangedListner;
    private UserBlockListner userBlockListner;
   
    private User user;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        logger = LogManager.getLogger(this);
        applicationManager = ApplicationManager.getInstance();
        dataStorage = ApplicationManager.getDataStorage();
        communicator = ApplicationManager.getCommunicator();
        windowService = ApplicationManager.getWindowService();
        localizedValueProvider = ApplicationManager.getLocalizedValueProvider();
        defaultValueProvider = ApplicationManager.getDefaultValueProvider();
        initializeControls();
        initializeControlValidators();
        displayDefaultAvatar();
        logger.debug("UpdateUserController initialized");       
    }

    private void initializeControls() {
        snackbar = new JFXSnackbar(pane);
        comboGender.setItems(FXCollections.observableArrayList(dataStorage.getGenders().values()));
        initializeControls(false);
    }

    private void initializeControls(boolean isOwnUser) {
        spinner.setVisible(false);
        spinnerAvatar.setVisible(false);
        txtPassword.setVisible(isOwnUser);
        btnRandomPassword.setVisible(isOwnUser);
        btnBlock.setVisible(!isOwnUser);
        btnUnblock.setVisible(!isOwnUser);
        btnSelectAvatar.setVisible(isOwnUser);
        if (user != null && user.isCurrentUser()) {
            lblTitle.setText(localizedValueProvider.getString("myAccount"));
        }
    }

    private void initializeControlValidators() {
        validatorNameField = new TextFieldValidator(defaultValueProvider.getDefaultValidationPattern("name"));
        validatorEmailField = new EmailFieldValidator(defaultValueProvider.getDefaultValidationPattern("email"));
        validatorEducationField = new TextFieldValidator(defaultValueProvider.getDefaultValidationPattern("education"));
        txtName.getValidators().add(validatorNameField);
        txtEmail.getValidators().add(validatorEmailField);
        txtEducation.getValidators().add(validatorEducationField);
    }

    private boolean validateInputControls() {
        boolean isValid;
        txtName.validate();
        txtEmail.validate();
        txtEducation.validate();  
        isValid = !(validatorNameField.getHasErrors() || validatorEmailField.getHasErrors() || validatorEducationField.getHasErrors());
        return isValid;
    }

    private void disableControls(boolean disable) {
        Platform.runLater(() -> {
            txtUsername.setDisable(disable);
            txtName.setDisable(disable);
            txtEmail.setDisable(disable);
            txtEducation.setDisable(disable);
            txtPassword.setDisable(disable);
            comboGender.setDisable(disable);
            btnUpdate.setDisable(disable);
            btnClose.setDisable(disable);
            btnRandomPassword.setDisable(disable);
            btnBlock.setDisable(disable);
            btnUnblock.setDisable(disable);
            btnSelectAvatar.setDisable(disable);
            btnRemoveAvatar.setDisable(disable);
            ivAvatar.setDisable(disable);
            spinner.setVisible(disable);
        });
    }

    private void displayAvatar(BufferedImage avatar) {
        Platform.runLater(() -> ivAvatar.setImage(SwingFXUtils.toFXImage(avatar, null)));
    }

    private void displayDefaultAvatar() {
        displayAvatar(defaultValueProvider.getDefaultAvatar());
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

    private String getPassword() {
        return txtPassword.getText();
    }

    private Character getGender() {
        return comboGender.getSelectionModel().getSelectedItem().getCode();
    }
               
    public void setUser(User user) throws Exception {
        if (user == null) {
            closeWindow();
            throw new RequiredParameterException(user, "must not be null");
        }
        this.user = user;
        txtUsername.setText(user.getUsername());
        txtName.setText(user.getName());
        txtEmail.setText(user.getEmail());
        txtEducation.setText(user.getEducation());
        comboGender.getSelectionModel().select(dataStorage.getGender(user.getGender()));
        initializeControls(user.isCurrentUser());
        if (user.getAvatar() != null) {
            displayAvatar(user.getAvatar());
        } else {
            loadAvatarFromWebService();
        }
    }
    
    private void loadAvatarFromWebService() {
        try {
            spinnerAvatar.setVisible(true);
            communicator.requestUserAvatar(this, user.getUsername());
        } catch (Exception ex) {
            spinnerAvatar.setVisible(false);
            displayDefaultAvatar();
            logger.error("loading avatar for " + user.getUsername() + " failed", ex);
        }
    }

    public void setReauthenticationListener(ReauthenticationListener reauthenticationListener) {
        this.reauthenticationListener = reauthenticationListener;
    }

    public void setUserDataChangedListner(UserDataChangedListner userDataChangedListner) {
        this.userDataChangedListner = userDataChangedListner;
    }

    public void setUserAvatarChangedListner(UserAvatarChangedListner userAvatarChangedListner) {
        this.userAvatarChangedListner = userAvatarChangedListner;
    }

    public void setUserBlockListner(UserBlockListner userBlockListner) {
        this.userBlockListner = userBlockListner;
    }
    
    private void notifyUserDataChangedListener(User user) {
        if (userDataChangedListner != null) {
            Platform.runLater(() -> userDataChangedListner.userDataChanged(user));
        }
    }

    private void notifyUserAvatarChangedListener(User user) {
        if (userAvatarChangedListner != null) {
            Platform.runLater(() -> userAvatarChangedListner.userAvatarChanged(user));
        }
    }
    
    private void notifyUserBlockedListener(User user) {
        if (userBlockListner != null) {
            Platform.runLater(() -> userBlockListner.userBlocked(user));
        }
    }

    private void notifyUserUnblockedListener() {
        if (userBlockListner != null) {
            Platform.runLater(() -> userBlockListner.userUnblocked());
        }
    }

    @FXML
    void onBtnSelectAvatar(ActionEvent event) {
        File file;
        FileChooser fileChooser;
        try {
            fileChooser = new FileChooser();
            fileChooser.setTitle("Choose an avatar for this account");
            fileChooser.getExtensionFilters().add(defaultValueProvider.getDefaultImageFileExtensionFilter());
            file = fileChooser.showOpenDialog(ivAvatar.getScene().getWindow());
            if (file != null) {
                spinnerAvatar.setVisible(true);
                if (!communicator.requestUpdateUserAvatar(this, user, file)) {
                    spinnerAvatar.setVisible(false);
                    reauthenticateUser(WebserviceOperation.UPDATE_USER_AVATAR);
                }
            }
        } catch (Exception ex) {
            spinnerAvatar.setVisible(false);
            user.setAvatar(null);
            displayDefaultAvatar();
            displayMessage(new MessageContainer(MessageCodes.EXCEPTION, localizedValueProvider.getString("messageUnexpectedFailure")));
            logger.error("onBtnSelectAvatar: exception occurred", ex);
        }
    }

    @FXML
    void onBtnRemoveAvatar(ActionEvent event) {
        try {
            spinnerAvatar.setVisible(true);
            if (!communicator.requestResetUserAvatar(this, user)) {
                spinnerAvatar.setVisible(false);
                reauthenticateUser(WebserviceOperation.DELETE_USER_AVATAR);
            } 
        } catch (Exception ex) {
            spinnerAvatar.setVisible(false);
            displayMessage(new MessageContainer(MessageCodes.EXCEPTION, localizedValueProvider.getString("messageUnexpectedFailure")));
            logger.error("onBtnRemoveAvatar: exception occurred", ex);
        }
    }

    @FXML
    void onBtnBlock(ActionEvent event) {
        try {
            windowService.openBlockUserWindow(this, user);
        } catch (IOException ioex) {
            logger.error("onBtnBlock: exception occurred", ioex);
        }
    }

    @FXML
    void onBtnUnblock(ActionEvent event) {
        try {
            communicator.requestUnblockUser(this, getUsername());
        } catch (Exception ex) {
            logger.error("onBtnUnblock: exception occurred", ex);
        }
    }

    @FXML
    void onBtnGeneratePassword(ActionEvent event) {
        String generatedPassword;
        generatedPassword = PasswordGenerator.generateRandomAlphanumericPassword();
        txtPassword.setText(generatedPassword);
        lblRandomPassword.setText(generatedPassword);
    }

    @FXML
    void onBtnUpdate(ActionEvent event) {
        if (validateInputControls()) {
            disableControls(true);
            try {
                User temporaryUser = new User(getUsername(), getName(), getGender(), StringUtils.isEmpty(getPassword()) ? null : getPassword(), getEmail(), getEducation());
                temporaryUser.setAvatar(user.getAvatar());              
                if (user.isCurrentUser()) {
                    applicationManager.setCurrentUser(temporaryUser);
                    if (!communicator.requestUpdateOwnUser(this)) {
                        disableControls(false);
                        applicationManager.restoreToPreviousCurrentUser();
                        reauthenticateUser(WebserviceOperation.UPDATE_OWN_USER);
                    }
                } else {
                    if (!communicator.requestUpdateUser(this, temporaryUser)) {
                        disableControls(false);
                        reauthenticateUser(WebserviceOperation.UPDATE_USER);
                    }
                }
            } catch (Exception ex) {
                disableControls(false);
                logger.error("onBtnUpdate: excpetion occurred", ex);
            }
        }
    }

    @FXML
    void onBtnClose(ActionEvent event) {
        closeWindow();
    }

    @Override
    public void requestUpdateUserFinished(RequestResult result) {
        disableControls(false);     
        if (result.isSuccessful()) {
            notifyUserDataChangedListener(user);
            displayMessage(new MessageContainer(MessageCodes.OK, localizedValueProvider.getString("messageUserSuccessfullyUpdated")));
        } else {
            displayMessage(result.getMessageContainer());
        }
    }

    @Override
    public void requestUpdateOwnUserFinished(RequestResult result) {
        disableControls(false);
        if (result.isSuccessful()) {   
            notifyUserDataChangedListener(applicationManager.getCurrentUser());
            displayMessage(new MessageContainer(MessageCodes.OK, localizedValueProvider.getString("messageUserSuccessfullyUpdated")));
            if (!StringUtils.isBlank(getPassword()) && applicationManager.isTokenFileAvailable()) {
                applicationManager.deleteTokenFile();
            }
        } else {
            applicationManager.restoreToPreviousCurrentUser();
            displayMessage(result.getMessageContainer());
        }
    }

    @Override
    public void requestAvatarFinished(RequestResult result, InputStream iStream) {
        Platform.runLater(() -> spinnerAvatar.setVisible(false));
        try {
            if (result.isSuccessful()) {
                user.setAvatar(ImageIO.read(iStream));
                iStream.close();
                displayAvatar(user.getAvatar());
            }
        } catch (IOException ioex) {
            displayMessage(new MessageContainer(MessageCodes.LOADING_FAILED, localizedValueProvider.getString("messageUserAvatarLoadingFailed")));
            logger.error("requestAvatarFinished: exception ocurred", ioex);
        }
    }

    @Override
    public void requestUpdateAvatarFinished(RequestResult result, BufferedImage avatar) {
        Platform.runLater(() -> spinnerAvatar.setVisible(false));
        if (result.isSuccessful()) {
            user.setAvatar(avatar);
            displayAvatar(user.getAvatar());
            notifyUserAvatarChangedListener(user);
        }
    }
    
    @Override
    public void requestResetAvatarFinished(RequestResult result) {
        Platform.runLater(() -> spinnerAvatar.setVisible(false));
        if (result.isSuccessful()) {
            user.setAvatar(null);
            displayDefaultAvatar();
            notifyUserAvatarChangedListener(user);
        }
    }

    @Override
    public void requestUnblockUserFinished(RequestResult result) {
        if (result.isSuccessful()) {
            userUnblocked();
        }
    }

    @Override
    public void requestFailed(RequestResult result) {
        disableControls(false);
        displayMessage(new MessageContainer(MessageCodes.REQUEST_FAILED, localizedValueProvider.getString("messageUnexpectedFailure")));
        logger.error("Request failed with status code:" + result.getStatusCode());
        logger.error(result.getMessageContainer().toString());
    }

    @Override
    public void userBlocked(User user) {
        displayMessage(new MessageContainer(MessageCodes.INFO, localizedValueProvider.getString("messageUserBlocked")));
        notifyUserBlockedListener(user);
    }

    @Override
    public void userUnblocked() {
        displayMessage(new MessageContainer(MessageCodes.INFO, localizedValueProvider.getString("messageUserUnblocked")));
        notifyUserUnblockedListener();
    }
    
    @Override
    public void displayMessage(MessageContainer container) {
        logger.debug(container.toString());
        windowService.displayMessage(snackbar, container);
    }
    
    @Override
    public void closeWindow() {
        windowService.closeWindow(pane);
    }
    
    @Override
    public void reauthenticateUser(WebserviceOperation webserviceOperation) throws Exception {
        //windowService.openReauthenticationWindow(webserviceOperation, this);
    }
    
    @Override
    public void reauthenticationCanceled() {
        closeWindow();
        reauthenticationListener.reauthenticationCanceled();
    }

    @Override
    public void reauthenticationSuccessful(WebserviceOperation webserviceOperation) {
        if (null != webserviceOperation) {
            switch (webserviceOperation) {
                case UPDATE_USER:
                    
                    break;
                case UPDATE_OWN_USER:
                    
                    break;
                case GET_USER_AVATAR:
                    
                    break;
                case UPDATE_USER_AVATAR:
                    
                    break;
                case DELETE_USER_AVATAR:
                    
                    break;
                default:
                    break;
            }
        } 
    }
}
