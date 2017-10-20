package at.bsd.tutoringtrain.controller.authentication;

import at.bsd.tutoringtrain.data.Database;
import at.bsd.tutoringtrain.debugging.MessageLogger;
import at.bsd.tutoringtrain.io.network.Communicator;
import at.bsd.tutoringtrain.io.network.Credentials;
import at.bsd.tutoringtrain.io.network.RequestResult;
import at.bsd.tutoringtrain.io.network.listener.user.RequestAuthenticateListner;
import at.bsd.tutoringtrain.io.network.listener.user.RequestTokenValidationListener;
import at.bsd.tutoringtrain.security.PasswordHash;
import at.bsd.tutoringtrain.ui.validators.PasswordFieldValidator;
import at.bsd.tutoringtrain.ui.validators.UsernameFieldValidator;
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
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author Marco Wilscher marco.wilscher@edu.htl-villach.at
 */
public class AuthentificationController implements Initializable, RequestAuthenticateListner, RequestTokenValidationListener {
    
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
    private JFXToggleButton stayLoggedIn;
    
    private JFXSnackbar snackbar; 
    private Communicator communicator;
    private MessageLogger logger;
    private Database db;
    private UsernameFieldValidator validatorUsername;
    private PasswordFieldValidator validatorPassword;
   
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            logger = MessageLogger.getINSTANCE();
            db = Database.getInstance();
            communicator = Communicator.getInstance();
            snackbar = new JFXSnackbar(pane);
            validatorUsername = new UsernameFieldValidator();
            validatorPassword = new PasswordFieldValidator();
            txtUsername.getValidators().add(validatorUsername);
            txtPassword.getValidators().add(validatorPassword);
            lockUi(true);
            if (db.isTokenFileAvailable()) {
                try {
                    communicator.requestTokenValidation(this, db.readTokenFile());
                } catch (Exception ex) {
                    logger.exception(ex, getClass());
                    lockUi(false);
                }
            } else {
                lockUi(false);
            }
        } catch (Exception ex) {
            lockUi(false);
            logger.exception(ex, getClass());
        }
    }    

    @FXML
    void onBtnLogin(ActionEvent event) {
        try {
            txtUsername.validate();
            txtPassword.validate();          
            if (!validatorUsername.hasErrorsProperty().get() && !validatorPassword.hasErrorsProperty().get()) {
                lockUi(true);
                communicator.requestAuthenticate(this, new Credentials(getUsername(), getHashedPassword()), stayLoggedIn.isSelected());
            }
        } catch (Exception ex) {
            lockUi(false);
            logger.exception(ex, getClass());
        }
    }
    
    private void lockUi(boolean lock) {
        Platform.runLater(() -> {
            txtUsername.setDisable(lock);
            txtPassword.setDisable(lock);   
            btnLogin.setDisable(lock);
            stayLoggedIn.setDisable(lock);         
            spinner.setVisible(lock);
        });
    }

    private String getHashedPassword() {
        return PasswordHash.generate(getPassword());
    }
    
    private String getPassword() {
        return txtPassword.getText();
    }
    
    private String getUsername() {
        return txtUsername.getText();
    }
    
    private void displayMessage(String message) {
        Platform.runLater(() -> snackbar.show(message, 5000));    
    }
    
    private void closeWindow() {
        ((Stage)btnLogin.getScene().getWindow()).close();
    }

    public void openMainWindow() {
        Platform.runLater(() -> {
            try {
                Parent root;
                Stage stage;
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Main.fxml"));
                root = loader.load();
                db.setMainController(loader.getController());
                stage = new Stage();
                stage.setTitle("TutoringTrain - Administrator Client");
                stage.setResizable(false);
                stage.setScene(new Scene(root));
                stage.show();
                closeWindow();
            } catch (IOException ioex) {
                logger.exception(ioex, getClass());
            }
        });
    }

    @Override
    public void requestAuthenticateFinished(RequestResult result) {
        try {
            if (result.isSuccessful()) {
                openMainWindow();
            } else {
                lockUi(false);
                displayMessage(result.getMessageContainer().toString());           
                logger.error(result.getMessageContainer().toString(), getClass());
            }
        } catch (Exception ex) {
            lockUi(false);
            logger.exception(ex, getClass());
        }
    }

    @Override
    public void requestTokenValidationFinished(RequestResult result) {
        if (result.isSuccessful()) {    
            openMainWindow();
        } else {
            lockUi(false);
        }
    }
    
    @Override
    public void requestFailed(RequestResult result) {
        lockUi(false);
        displayMessage(result.getMessageContainer().toString());
        logger.error(result.getMessageContainer().toString(), getClass());
    }
}
