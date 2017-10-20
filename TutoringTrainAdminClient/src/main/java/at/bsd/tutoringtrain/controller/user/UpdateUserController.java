package at.bsd.tutoringtrain.controller.user;

import at.bsd.tutoringtrain.data.Database;
import at.bsd.tutoringtrain.data.entities.User;
import at.bsd.tutoringtrain.data.enumeration.Gender;
import at.bsd.tutoringtrain.debugging.MessageLogger;
import at.bsd.tutoringtrain.io.network.Communicator;
import at.bsd.tutoringtrain.io.network.RequestResult;
import at.bsd.tutoringtrain.io.network.listener.user.RequestUpdateOwnUserListener;
import at.bsd.tutoringtrain.io.network.listener.user.RequestUpdateUserListener;
import at.bsd.tutoringtrain.messages.MessageCode;
import at.bsd.tutoringtrain.messages.MessageContainer;
import at.bsd.tutoringtrain.security.PasswordGenerator;
import at.bsd.tutoringtrain.ui.listener.UserDataChangedListner;
import at.bsd.tutoringtrain.ui.validators.EducationFieldValidator;
import at.bsd.tutoringtrain.ui.validators.EmailFieldValidator;
import at.bsd.tutoringtrain.ui.validators.NameFieldValidator;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXSnackbar;
import com.jfoenix.controls.JFXSpinner;
import com.jfoenix.controls.JFXTextField;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javax.imageio.ImageIO;
import org.apache.commons.lang.StringUtils;

/**
 * FXML Controller class
 *
 * @author Marco Wilscher marco.wilscher@edu.htl-villach.at
 */
public class UpdateUserController implements Initializable, RequestUpdateUserListener, RequestUpdateOwnUserListener {
    @FXML
    private AnchorPane pane;   
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
    private JFXPasswordField txtPassword;
    @FXML
    private JFXButton btnGeneratePassword;
    @FXML
    private JFXComboBox<Gender> comboGender;
    @FXML
    private JFXButton btnUpdate;
    @FXML
    private JFXButton btnClose;
    @FXML
    private ImageView ivAvatar;
    @FXML
    private JFXButton btnSelectAvatar;
    @FXML
    private JFXButton btnRemoveAvatar;
    @FXML
    private JFXButton btnPromote;
    @FXML
    private JFXButton btnDemote;
    @FXML
    private JFXButton btnBlock;
    @FXML
    private JFXButton btnUnblock;
    @FXML
    private Label lblGeneratedPassword;
    @FXML
    private JFXSpinner spinner;
    
    private JFXSnackbar snackbar;    
    private Communicator communicator;
    private Database db;
    private MessageLogger logger;    
    private String generatedPassword;
    private boolean ownAccount;   
    private BufferedImage avatar;
    private NameFieldValidator validatorNameField;
    private EmailFieldValidator validatorEmailField;
    private EducationFieldValidator validatorEducationField;
    private UserDataChangedListner userDataChangedListner;
    private User user;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        logger = MessageLogger.getINSTANCE();
        communicator = Communicator.getInstance();
        db = Database.getInstance();           
        snackbar = new JFXSnackbar(pane);
        validatorNameField = new NameFieldValidator();
        validatorEmailField = new EmailFieldValidator();
        validatorEducationField = new EducationFieldValidator();
        txtName.getValidators().add(validatorNameField);
        txtEmail.getValidators().add(validatorEmailField);
        txtEducation.getValidators().add(validatorEducationField);   
        ownAccount = false;
        comboGender.setItems(FXCollections.observableArrayList(db.getGenders()));
        comboGender.getSelectionModel().selectFirst();
    }    

    @FXML
    void onBtnSelectAvatar(ActionEvent event) {
        File file;
        FileChooser fileChooser;
        FileChooser.ExtensionFilter fileExtensionFilter;
        try {
            fileChooser = new FileChooser();
            fileChooser.setTitle("Select avatar!");
            fileExtensionFilter = new FileChooser.ExtensionFilter("Image", "*.jpg", "*.png");
            fileChooser.getExtensionFilters().add(fileExtensionFilter);    
            file = fileChooser.showOpenDialog(ivAvatar.getScene().getWindow());
            if (file != null) {
                avatar = ImageIO.read(file);
                ivAvatar.setImage(SwingFXUtils.toFXImage(avatar, null));
            }
        } catch (IOException ioex) {
            displayMessage(new MessageContainer(MessageCode.EXCEPTION, ioex.getMessage()));
        }
    }
    
    @FXML
    void onBtnRemoveAvatar(ActionEvent event) {
        try {
            avatar = null;
            ivAvatar.setImage(SwingFXUtils.toFXImage(ImageIO.read(getClass().getResource("/images/default_avatar.png")), null));
        } catch (IOException ioex) {
            displayMessage(new MessageContainer(MessageCode.EXCEPTION, ioex.getMessage()));
        }
    }
    
    @FXML
    void onBtnBlock(ActionEvent event) {
        openBlockUserWindow(user);
    }
    
    @FXML
    void onBtnUnblock(ActionEvent event) {
        //TODO next sprint
    }

    @FXML
    void onBtnDemote(ActionEvent event) {
        //TODO next sprint
    }

    @FXML
    void onBtnPromote(ActionEvent event) {
        //TODO next sprint
    }
    
    @FXML
    void onBtnGeneratePassword(ActionEvent event) {
        generatedPassword = PasswordGenerator.generateRandomPassword();
        txtPassword.setText(generatedPassword);
        lblGeneratedPassword.setText(generatedPassword);
    }    

    @FXML
    void onBtnUpdate(ActionEvent event) {
        txtName.validate();
        txtEmail.validate();
        txtEducation.validate();
        if (!validatorNameField.hasErrorsProperty().get()  && !validatorEmailField.hasErrorsProperty().get() && !validatorEducationField.hasErrorsProperty().get()) {
            lockUi(true);
            try {
                user = new User(getUsername(), getName(), getGender(), StringUtils.isEmpty(getPassword()) ? null : getPassword(), getEmail(), getEducation());
                if (ownAccount) {
                    db.backupCurrentUser();
                    db.setCurrentUser(user);
                    if (!communicator.requestUpdateOwnUser(this)) {
                        db.restoreCurrentUser();
                        displayMessage(new MessageContainer(MessageCode.LOGIN_REQUIRED, "New login required!"));
                    } 
                } else {
                    if (!communicator.requestUpdateUser(this, user)) {
                        displayMessage(new MessageContainer(MessageCode.LOGIN_REQUIRED, "New login required!"));
                    } 
                }
            } catch (Exception ex) {
                lockUi(false);
                logger.exception(ex, getClass());
            }
        }
    }

    @FXML
    void onBtnClose(ActionEvent event) {
        closeWindow();
    }
    
    public void setUser(User user) {
        this.user = user;
        if (user != null) {
            txtUsername.setText(user.getUsername());
            txtName.setText(user.getName());
            txtEmail.setText(user.getEmail());
            txtEducation.setText(user.getEducation());
            comboGender.getSelectionModel().select(Gender.get(user.getGender()));
        }
    }

    public void setUserDataChangedListner(UserDataChangedListner userDataChangedListner) {
        this.userDataChangedListner = userDataChangedListner;
    }
    
    private void notifyListener(User user) {
        if (userDataChangedListner != null) {
            Platform.runLater(() -> userDataChangedListner.userDataChanged(user));
        }
    }
    
    public void setTitle(String title) {
        lblTitle.setText(title);
    }

    public void setOwnAccout(boolean own) {
        this.ownAccount = own;
        lockUi(false);
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
        return comboGender.getSelectionModel().getSelectedItem().getId();
    }
    
    private void lockUi(boolean lock) {
        Platform.runLater(() -> {
            txtUsername.setDisable(lock);
            txtName.setDisable(lock);
            txtEmail.setDisable(lock);
            txtEducation.setDisable(lock);
            txtPassword.setDisable(lock);
            comboGender.setDisable(lock);           
            btnUpdate.setDisable(lock);
            btnClose.setDisable(lock);
            btnGeneratePassword.setDisable(lock);
            btnBlock.setDisable(ownAccount || lock);
            btnUnblock.setDisable(ownAccount || lock);
            btnDemote.setDisable(ownAccount || lock);
            btnPromote.setDisable(ownAccount || lock);
            btnSelectAvatar.setDisable(lock);    
            btnRemoveAvatar.setDisable(lock);
            spinner.setVisible(lock);
        });
    }
    
    private void displayMessage(MessageContainer container) {
        Platform.runLater(() -> snackbar.enqueue(new JFXSnackbar.SnackbarEvent(container.toString())));
    }
    
    private void closeWindow() {
        ((Stage)pane.getScene().getWindow()).close();
    }
     
    private void openBlockUserWindow(User user) {
        Parent root;
        Stage stage;
        FXMLLoader loader;
        BlockUserController controller;
        try {
            loader = new FXMLLoader(getClass().getResource("/fxml/BlockUser.fxml"));
            root = loader.load();
            controller = (BlockUserController)loader.getController();
            stage = new Stage();
            stage.setTitle("TutoringTrain - Admin Client");
            stage.setResizable(false);
            stage.setScene(new Scene(root));
            stage.show();
            controller.setUser(user);
        } catch (IOException ioex) {
            logger.exception(ioex, getClass());
        }
    }
    
    @Override
    public void requestUpdateUserFinished(RequestResult result) {
        StringBuilder emailMessage;
        try {
            lockUi(false);
            if (result.isSuccessful()) {
                notifyListener(user);
                displayMessage(new MessageContainer(0, "User has been sucessfully updated!"));
                /*if (!StringUtils.isBlank(getPassword())) {               
                    emailMessage = new StringBuilder();
                    emailMessage.append("Dear ");
                    emailMessage.append(getName());
                    emailMessage.append(",\n\nYour account password has been updated to: ");
                    emailMessage.append(getPassword());
                    emailMessage.append("\n\nKind regards,\n");
                    emailMessage.append(db.getCurrentUser().getName());
                    EmailService.sendMail("TutoringTrain Account", emailMessage.toString(), txtEmail.getText());
                } */
            } else {
                displayMessage(result.getMessageContainer());
            }
        } catch (Exception ex) {
            lockUi(false);
            displayMessage(new MessageContainer(MessageCode.EXCEPTION, ex.getMessage()));
            logger.exception(ex, getClass());
        }
    }

    @Override
    public void requestUpdateOwnUserFinished(RequestResult result) {
        lockUi(false);
        if (result.isSuccessful()) {
            notifyListener(user);
            displayMessage(new MessageContainer(MessageCode.OK, "Your account has been sucessfully updated!"));
            if (!StringUtils.isBlank(getPassword()) && db.isTokenFileAvailable()) {
                db.deleteTokenFile();
                displayMessage(new MessageContainer(MessageCode.OK, "Your lokal session key has been deleted!"));
            }
        } else {     
            db.restoreCurrentUser();
            displayMessage(result.getMessageContainer());
        }  
    }
    
    @Override
    public void requestFailed(RequestResult result) {
        lockUi(false);
        displayMessage(result.getMessageContainer());
    }
}
