package at.bsd.tutoringtrain.controller.user;

import at.bsd.tutoringtrain.data.Database;
import at.bsd.tutoringtrain.data.entities.User;
import at.bsd.tutoringtrain.data.enumeration.Gender;
import at.bsd.tutoringtrain.debugging.MessageLogger;
import at.bsd.tutoringtrain.io.network.Communicator;
import at.bsd.tutoringtrain.io.network.RequestResult;
import at.bsd.tutoringtrain.io.network.listener.user.RequestRegisterUserListener;
import at.bsd.tutoringtrain.messages.MessageCode;
import at.bsd.tutoringtrain.messages.MessageContainer;
import at.bsd.tutoringtrain.security.PasswordGenerator;
import at.bsd.tutoringtrain.ui.validators.EducationFieldValidator;
import at.bsd.tutoringtrain.ui.validators.EmailFieldValidator;
import at.bsd.tutoringtrain.ui.validators.NameFieldValidator;
import at.bsd.tutoringtrain.ui.validators.PasswordFieldValidator;
import at.bsd.tutoringtrain.ui.validators.UsernameFieldValidator;
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
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javax.imageio.ImageIO;

/**
 * FXML Controller class
 *
 * @author Marco Wilscher marco.wilscher@edu.htl-villach.at
 */
public class RegisterUserController implements Initializable, RequestRegisterUserListener {
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
    private JFXPasswordField txtPassword;
    @FXML
    private JFXButton btnGeneratePassword;
    @FXML
    private JFXComboBox<Gender> comboGender;
    @FXML
    private JFXButton btnRegister;
    @FXML
    private JFXButton btnClose;
    @FXML
    private ImageView ivAvatar;
    @FXML
    private JFXButton btnSelectAvatar;
    @FXML
    private JFXButton btnRemoveAvatar;
    @FXML
    private JFXSpinner spinner;
    @FXML
    private Label lblGeneratedPassword;
    
    private JFXSnackbar snackbar;
    private Communicator communicator;
    private Database db;
    private MessageLogger logger; 
    private String generatedPassword;
    private BufferedImage avatar;
    private UsernameFieldValidator validatorUsernameField;
    private PasswordFieldValidator validatorPasswordField;
    private NameFieldValidator validatorNameField;
    private EmailFieldValidator validatorEmailField;
    private EducationFieldValidator validatorEducationField;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        logger = MessageLogger.getINSTANCE();
        communicator = Communicator.getInstance();
        db = Database.getInstance();
        snackbar = new JFXSnackbar(pane);
        validatorUsernameField = new UsernameFieldValidator();
        validatorPasswordField = new PasswordFieldValidator();
        validatorNameField = new NameFieldValidator();
        validatorEmailField = new EmailFieldValidator();
        validatorEducationField = new EducationFieldValidator();
        txtUsername.getValidators().add(validatorUsernameField);
        txtName.getValidators().add(validatorNameField);
        txtPassword.getValidators().add(validatorPasswordField);
        txtEmail.getValidators().add(validatorEmailField);
        txtEducation.getValidators().add(validatorEducationField);   
        comboGender.setItems(FXCollections.observableArrayList(db.getGenders()));
        comboGender.getSelectionModel().selectFirst();
    }    
    
    @FXML
    void onBtnClose(ActionEvent event) {
        closeWindow();
    }

    @FXML
    void onBtnGeneratePassword(ActionEvent event) {
        generatedPassword = PasswordGenerator.generateRandomPassword();
        txtPassword.setText(generatedPassword);
        lblGeneratedPassword.setText(generatedPassword);
    }

    @FXML
    void onBtnRegister(ActionEvent event) {
        txtUsername.validate();
        txtName.validate();
        txtPassword.validate();
        txtEmail.validate();
        txtEducation.validate();      
        if (!validatorUsernameField.hasErrorsProperty().get() && !validatorNameField.hasErrorsProperty().get() && !validatorPasswordField.hasErrorsProperty().get() && !validatorEmailField.hasErrorsProperty().get() && !validatorEducationField.hasErrorsProperty().get()) {
            lockUi(true);
            try {
                User user;
                user = new User(getUsername(), getName(), getGender(), getPassword(), getEmail(), getEducation());
                if (!communicator.requestRegisterUser(this, user)) {
                    lockUi(false);
                    displayMessage(new MessageContainer(MessageCode.LOGIN_REQUIRED, "Session expired! New login required!"));
                }
            } catch (Exception ex) {
                lockUi(false);
                displayMessage(new MessageContainer(MessageCode.EXCEPTION, "Register user failed!"));
                logger.exception(ex, getClass());
            }
        }
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
            btnRegister.setDisable(lock);
            btnClose.setDisable(lock);
            btnGeneratePassword.setDisable(lock);
            btnSelectAvatar.setDisable(lock);           
            spinner.setVisible(lock);
        });
    }
    
    private void clearInput() {
        Platform.runLater(() -> {
            txtUsername.setText("");
            txtName.setText("");
            txtEmail.setText("");
            txtEducation.setText("");
            txtPassword.setText("");
            comboGender.getSelectionModel().selectFirst();
        });
    }
    
    private void displayMessage(MessageContainer container) {
        Platform.runLater(() -> snackbar.enqueue(new JFXSnackbar.SnackbarEvent(container.toString())));
    }
    
    private void closeWindow() {
        ((Stage)pane.getScene().getWindow()).close();
    }

    @Override
    public void requestRegisterUserFinished(RequestResult result) {
        //StringBuilder emailMessage;
        //try {
            if (result.isSuccessful()) {
                /*emailMessage = new StringBuilder();
                emailMessage.append("Dear ");
                emailMessage.append(getName());
                emailMessage.append(",\n\nYour account has been created with the following credentials:\n\nUsername: ");
                emailMessage.append(getUsername());
                emailMessage.append("\n Password: ");
                emailMessage.append(getPassword());
                emailMessage.append("\n\nKind regards,\n");
                emailMessage.append(db.getCurrentUser().getName());*/
                displayMessage(new MessageContainer(0, "User sucessfully registered!"));
                clearInput();
                lockUi(false);
                //EmailService.sendMail("TutoringTrain Account", emailMessage.toString(), txtEmail.getText());
            } else {
                lockUi(false);
                displayMessage(result.getMessageContainer());
            }
        //} catch (EmailException ex) {
        //    lockUi(false);
        //    displayMessage(new MessageContainer(MessageCode.EXCEPTION, ex.getMessage()));
        //    logger.exception(ex, getClass());
        //}
    }

    @Override
    public void requestFailed(RequestResult result) {
        lockUi(false);
        displayMessage(result.getMessageContainer());
    }
}
