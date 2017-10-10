package at.bsd.tutoringtrain.controller;

import at.bsd.tutoringtrain.data.Database;
import at.bsd.tutoringtrain.debugging.MessageLogger;
import at.bsd.tutoringtrain.network.Communicator;
import at.bsd.tutoringtrain.network.Result;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author Marco Wilscher <marco.wilscher@edu.htl-villach.at>
 */
public class AuthentificationController implements Initializable {
    @FXML
    private PasswordField txtPassword;

    @FXML
    private Button btnLogin;

    @FXML
    private TextField txtUsername;

    @FXML
    private Label lblMessage;
    
    private Communicator communicator;
    private Database db;
    private MessageLogger logger;
   
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        logger = MessageLogger.getINSTANCE();
        communicator = Communicator.getInstance();
        logger.objectInitialized(this, getClass());
    }    

    @FXML
    void onBtnLogin(ActionEvent event) {
        Parent root;
        Stage stage;
        Result result;
        try {
            disableInput();
            clearMessage();
            result = communicator.authenticate(txtUsername.getText(), txtPassword.getText());
            if (!result.isError()) {
                logger.info("Authentification successful", getClass());                
                root = FXMLLoader.load(getClass().getResource("/fxml/Main.fxml"));
                stage = new Stage();
                stage.setTitle("TutoringTrain - Admin Client");
                stage.setScene(new Scene(root));
                stage.show();

                closeWindow();
            } else {
                enableInput();
                displayMessage(result.toString());
                logger.error(result.toString(), getClass());
            }
        } catch (Exception ex) {
            lblMessage.setText(ex.getMessage());
            logger.error(ex.getMessage(), getClass());
        }
    }
    
    /**
     * 
     * @param message 
     */
    private void displayMessage(String message) {
        lblMessage.setText(message);
    }
    
    /**
     * 
     */
    private void clearMessage() {
        lblMessage.setText("");
    }
    
    /**
     * 
     */
    private void disableInput() {
        txtPassword.setDisable(true);
        txtUsername.setDisable(true);
        btnLogin.setDisable(true);
    }
    
    /**
     * 
     */
    private void enableInput() {
        txtPassword.setDisable(false);
        txtUsername.setDisable(false);
        btnLogin.setDisable(false);
    }
    
    /**
     * 
     */
    private void closeWindow() {
        ((Stage)btnLogin.getScene().getWindow()).close();
    }
}
