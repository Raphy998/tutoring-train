package at.bsd.tutoringtrain.controller.authentication;

import at.bsd.tutoringtrain.data.Database;
import at.bsd.tutoringtrain.debugging.MessageLogger;
import at.bsd.tutoringtrain.io.network.Communicator;
import at.bsd.tutoringtrain.messages.MessageContainer;
import at.bsd.tutoringtrain.ui.listener.ReauthenticateListener;
import at.bsd.tutoringtrain.ui.validators.PasswordFieldValidator;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXSnackbar;
import com.jfoenix.controls.JFXSpinner;
import com.jfoenix.controls.JFXTextField;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

/**
 * FXML Controller class
 *
 * @author Marco Wilscher marco.wilscher@edu.htl-villach.at
 */
public class RenewTokenController implements Initializable {
    @FXML
    private AnchorPane pane;

    @FXML
    private Label lblTitel;
    
    @FXML
    private JFXTextField txtUsername;

    @FXML
    private JFXPasswordField txtPassword;

    @FXML
    private JFXButton btnAuthenticate;

    @FXML
    private JFXButton btnCancel;

    @FXML
    private JFXSpinner spinner;

    private JFXSnackbar snackbar; 
    private Communicator communicator;
    private MessageLogger logger;
    private Database db;
    private PasswordFieldValidator validatorPassword;
    private ArrayList<ReauthenticateListener> reauthenticateListeners;
            
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        logger = MessageLogger.getINSTANCE();
        db = Database.getInstance();
        communicator = Communicator.getInstance();
        reauthenticateListeners = new ArrayList<>();
        snackbar = new JFXSnackbar(pane);
        validatorPassword = new PasswordFieldValidator();
        txtPassword.getValidators().add(validatorPassword);
    }    
    
    @FXML
    void onBtnAuthenticate(ActionEvent event) {
        txtPassword.validate();
        if (!validatorPassword.getHasErrors()) {
            
        }
    }

    @FXML
    void onBtnCancel(ActionEvent event) {
        reauthenticateListeners.forEach((l) -> l.authenticationCanceled());
    }
    
    private void displayMessage(MessageContainer message) {
        Platform.runLater(() -> snackbar.show(message.toString(), 5000));
    }
    
}
