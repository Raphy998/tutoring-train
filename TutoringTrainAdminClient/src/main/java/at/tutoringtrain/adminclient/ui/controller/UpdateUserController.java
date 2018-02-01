package at.tutoringtrain.adminclient.ui.controller;

import at.tutoringtrain.adminclient.data.user.Gender;
import at.tutoringtrain.adminclient.data.user.User;
import at.tutoringtrain.adminclient.data.user.UserRole;
import at.tutoringtrain.adminclient.exception.RequiredParameterException;
import at.tutoringtrain.adminclient.internationalization.LocalizedValueProvider;
import at.tutoringtrain.adminclient.io.network.Communicator;
import at.tutoringtrain.adminclient.io.network.RequestResult;
import at.tutoringtrain.adminclient.io.network.listener.user.RequestGetAvatarListener;
import at.tutoringtrain.adminclient.io.network.listener.user.RequestResetAvatarListener;
import at.tutoringtrain.adminclient.io.network.listener.user.RequestSetUserRoleListener;
import at.tutoringtrain.adminclient.io.network.listener.user.RequestUpdateAvatarListener;
import at.tutoringtrain.adminclient.io.network.listener.user.RequestUpdateOwnUserListener;
import at.tutoringtrain.adminclient.io.network.listener.user.RequestUpdateUserListener;
import at.tutoringtrain.adminclient.main.ApplicationManager;
import at.tutoringtrain.adminclient.main.DataStorage;
import at.tutoringtrain.adminclient.main.DefaultValueProvider;
import at.tutoringtrain.adminclient.main.MessageCodes;
import at.tutoringtrain.adminclient.main.MessageContainer;
import at.tutoringtrain.adminclient.security.PasswordGenerator;
import at.tutoringtrain.adminclient.ui.TutoringTrainWindow;
import at.tutoringtrain.adminclient.ui.WindowService;
import at.tutoringtrain.adminclient.ui.listener.UserAvatarChangedListner;
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
import javafx.event.EventHandler;
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
public class UpdateUserController implements Initializable, TutoringTrainWindow, RequestUpdateUserListener, RequestUpdateOwnUserListener, RequestGetAvatarListener, RequestUpdateAvatarListener, RequestResetAvatarListener, RequestSetUserRoleListener {
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
    private ImageView ivAvatar;
    @FXML
    private JFXSpinner spinnerAvatar;
    @FXML
    private JFXButton btnSelectAvatar;
    @FXML
    private JFXButton btnRemoveAvatar;
    @FXML
    private JFXComboBox<UserRole> comboRole;

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
    private UserDataChangedListner userDataChangedListner;
    private UserAvatarChangedListner userAvatarChangedListner;
    private User user, temporaryUser;

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
        comboRole.setItems(FXCollections.observableArrayList(UserRole.values()));
        initializeControls(false);
    }

    private void initializeControls(boolean isOwnUser) {
        spinner.setVisible(false);
        spinnerAvatar.setVisible(false);
        comboRole.setDisable(isOwnUser);
        txtPassword.setVisible(isOwnUser);
        btnRandomPassword.setVisible(isOwnUser);
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
            btnSelectAvatar.setDisable(disable);
            btnRemoveAvatar.setDisable(disable);
            comboRole.setDisable(user.isCurrentUser() || disable);
            ivAvatar.setDisable(disable);
            spinner.setVisible(disable);
            lblRandomPassword.setDisable(disable);
            ivAvatar.setOpacity(disable ? 0.5 : 1);
            lblTitle.setDisable(disable);
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
        if (!UserRole.valueOf(user.getRole()).isHighestPriority()) {
            comboRole.getItems().remove(UserRole.ROOT);
        }
        selectUserRole(user.getRole());
        initializeControls(user.isCurrentUser());
        if (user.getAvatar() != null) {
            displayAvatar(user.getAvatar());
        } else {
            loadAvatarFromWebService();
        }    
    }
    
    private void selectUserRole(Character role) {
        EventHandler<ActionEvent> eventHandler = comboRole.getOnAction();
        comboRole.setOnAction(null);
        comboRole.getSelectionModel().select(UserRole.valueOf(role));
        comboRole.setOnAction(eventHandler);
    }
    
    private void loadAvatarFromWebService() {
        try {
            spinnerAvatar.setVisible(true);
            if (!communicator.requestUserAvatar(this, user.getUsername())) {
                displayMessage(new MessageContainer(MessageCodes.EXCEPTION, localizedValueProvider.getString("messageReauthentication")));
            }
        } catch (Exception ex) {
            spinnerAvatar.setVisible(false);
            displayDefaultAvatar();
            logger.error("loadAvatarFromWebService", ex);
        }
    }

    public void setUserDataChangedListner(UserDataChangedListner userDataChangedListner) {
        this.userDataChangedListner = userDataChangedListner;
    }

    public void setUserAvatarChangedListner(UserAvatarChangedListner userAvatarChangedListner) {
        this.userAvatarChangedListner = userAvatarChangedListner;
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

    @FXML
    void onBtnSelectAvatar(ActionEvent event) {
        File file;
        FileChooser fileChooser;
        try {
            fileChooser = new FileChooser();
            fileChooser.setTitle(localizedValueProvider.getString("messageChooseAvatar"));
            fileChooser.getExtensionFilters().add(defaultValueProvider.getDefaultImageFileExtensionFilter());
            file = fileChooser.showOpenDialog(ivAvatar.getScene().getWindow());
            if (file != null) {
                spinnerAvatar.setVisible(true);
                if (!communicator.requestUpdateUserAvatar(this, user, file)) {
                    spinnerAvatar.setVisible(false);
                    displayMessage(new MessageContainer(MessageCodes.EXCEPTION, localizedValueProvider.getString("messageReauthentication")));
                }
            }
        } catch (Exception ex) {
            spinnerAvatar.setVisible(false);
            user.setAvatar(null);
            displayDefaultAvatar();
            displayMessage(new MessageContainer(MessageCodes.EXCEPTION, localizedValueProvider.getString("messageUnexpectedFailure")));
            logger.error("onBtnSelectAvatar", ex);
        }
    }

    @FXML
    void onBtnRemoveAvatar(ActionEvent event) {
        try {
            spinnerAvatar.setVisible(true);
            if (!communicator.requestResetUserAvatar(this, user)) {
                spinnerAvatar.setVisible(false);
                displayMessage(new MessageContainer(MessageCodes.EXCEPTION, localizedValueProvider.getString("messageReauthentication")));
            } 
        } catch (Exception ex) {
            spinnerAvatar.setVisible(false);
            displayMessage(new MessageContainer(MessageCodes.EXCEPTION, localizedValueProvider.getString("messageUnexpectedFailure")));
            logger.error("onBtnRemoveAvatar", ex);
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
                temporaryUser = new User(getUsername(), getName(), getGender(), StringUtils.isEmpty(getPassword()) ? null : getPassword(), getEmail(), getEducation());            
                temporaryUser.setRole(comboRole.getSelectionModel().getSelectedItem().getValue());
                if (user.isCurrentUser()) {
                    applicationManager.setCurrentUser(temporaryUser);
                    if (!communicator.requestUpdateOwnUser(this)) {
                        disableControls(false);
                        applicationManager.restoreToPreviousCurrentUser();
                        displayMessage(new MessageContainer(MessageCodes.EXCEPTION, localizedValueProvider.getString("messageReauthentication")));
                    }
                } else {
                    if (!communicator.requestUpdateUser(this, temporaryUser)) {
                        disableControls(false);
                        displayMessage(new MessageContainer(MessageCodes.EXCEPTION, localizedValueProvider.getString("messageReauthentication")));
                    }
                }
            } catch (Exception ex) {
                disableControls(false);
                logger.error("onBtnUpdate", ex);
                displayMessage(new MessageContainer(MessageCodes.EXCEPTION, localizedValueProvider.getString("messageUnexpectedFailure")));
            }
        }
    }
    
    @FXML
    void onRoleSelected(ActionEvent event) {
        UserRole selectedRole = comboRole.getSelectionModel().getSelectedItem();    
        if (selectedRole.isHighestPriority()) {
            selectUserRole(user.getRole());
            displayMessage(new MessageContainer(MessageCodes.INFO, localizedValueProvider.getString("messageUserRoleRoot")));
        } else {
            disableControls(true);
            try {          
                temporaryUser = new User();
                temporaryUser.setUsername(user.getUsername());
                temporaryUser.setRole(selectedRole.getValue());
                if (!communicator.requestSetUserRole(this, temporaryUser)) {
                    disableControls(false);
                    displayMessage(new MessageContainer(MessageCodes.EXCEPTION, localizedValueProvider.getString("messageReauthentication")));
                }
            } catch (Exception ex) {
                disableControls(false);
                logger.error("onRoleSelected", ex);
                displayMessage(new MessageContainer(MessageCodes.EXCEPTION, localizedValueProvider.getString("messageUnexpectedFailure")));
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
            user.setName(temporaryUser.getName());
            user.setEducation(temporaryUser.getEducation());
            user.setEmail(temporaryUser.getEmail());
            user.setGender(temporaryUser.getGender());
            user.setRole(temporaryUser.getRole());
            notifyUserDataChangedListener(user);
            displayMessage(new MessageContainer(MessageCodes.OK, localizedValueProvider.getString("messageUserSuccessfullyUpdated")));
        } else {
            if (result.getMessageContainer().getCode() == 3 || result.getMessageContainer().getCode() == 4) {
                displayMessage(new MessageContainer(MessageCodes.EXCEPTION, localizedValueProvider.getString("messageReauthentication")));
            } else {
                displayMessage(result.getMessageContainer());
            }
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
            if (result.getMessageContainer().getCode() == 3 || result.getMessageContainer().getCode() == 4) {
                displayMessage(new MessageContainer(MessageCodes.EXCEPTION, localizedValueProvider.getString("messageReauthentication")));
            } else {
                displayMessage(result.getMessageContainer());
            }
            applicationManager.restoreToPreviousCurrentUser();
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
            } else {
                if (result.getMessageContainer().getCode() == 3 || result.getMessageContainer().getCode() == 4) {
                    displayMessage(new MessageContainer(MessageCodes.EXCEPTION, localizedValueProvider.getString("messageReauthentication")));
                } else {
                    MessageContainer messageContainer = result.getMessageContainer();
                    if (messageContainer.getCode() == MessageCodes.MAPPING_FAILED) {
                        logger.error(messageContainer.toString());
                    } else {
                        displayMessage(result.getMessageContainer());
                    }
                }
            }
        } catch (IOException ioex) {
            displayMessage(new MessageContainer(MessageCodes.LOADING_FAILED, localizedValueProvider.getString("messageUserAvatarLoadingFailed")));
            logger.error("requestAvatarFinished", ioex);
        }
    }

    @Override
    public void requestUpdateAvatarFinished(RequestResult result, BufferedImage avatar) {
        Platform.runLater(() -> spinnerAvatar.setVisible(false));
        if (result.isSuccessful()) {
            user.setAvatar(avatar);
            displayAvatar(user.getAvatar());
            notifyUserAvatarChangedListener(user);
        } else {
            if (result.getMessageContainer().getCode() == 3 || result.getMessageContainer().getCode() == 4) {
                displayMessage(new MessageContainer(MessageCodes.EXCEPTION, localizedValueProvider.getString("messageReauthentication")));
            } else {
                displayMessage(result.getMessageContainer());
            }
        }
    }
    
    @Override
    public void requestResetAvatarFinished(RequestResult result) {
        Platform.runLater(() -> spinnerAvatar.setVisible(false));
        if (result.isSuccessful()) {
            user.setAvatar(null);
            displayDefaultAvatar();
            notifyUserAvatarChangedListener(user);
        } else {
            if (result.getMessageContainer().getCode() == 3 || result.getMessageContainer().getCode() == 4) {
                displayMessage(new MessageContainer(MessageCodes.EXCEPTION, localizedValueProvider.getString("messageReauthentication")));
            } else {
                displayMessage(result.getMessageContainer());
            }
        }
    }

    
    @Override
    public void requestSetUserRoleFinished(RequestResult result) {
        disableControls(false);
        if (result.isSuccessful()) {   
            user.setRole(temporaryUser.getRole());
            temporaryUser = null;
            notifyUserDataChangedListener(user);
            displayMessage(new MessageContainer(MessageCodes.OK, localizedValueProvider.getString("messageUserRoleSuccessfullySet")));
        } else {
            if (result.getMessageContainer().getCode() == 3 || result.getMessageContainer().getCode() == 4) {
                displayMessage(new MessageContainer(MessageCodes.EXCEPTION, localizedValueProvider.getString("messageReauthentication")));
            } else {
                displayMessage(result.getMessageContainer());
            }
            Platform.runLater(() -> selectUserRole(user.getRole()));
        }
    }

    @Override
    public void requestFailed(RequestResult result) {
        disableControls(false);
        Platform.runLater(() -> spinnerAvatar.setVisible(false));
        ApplicationManager.getHostFallbackService().requestCheck();
        displayMessage(new MessageContainer(MessageCodes.REQUEST_FAILED, localizedValueProvider.getString("messageConnectionFailed")));
        logger.error(result.getMessageContainer().toString());
    }

    @Override
    public void displayMessage(MessageContainer container) {
        windowService.displayMessage(snackbar, container);
    }
    
    @Override
    public void closeWindow() {
        windowService.closeWindow(pane);
    }
}
