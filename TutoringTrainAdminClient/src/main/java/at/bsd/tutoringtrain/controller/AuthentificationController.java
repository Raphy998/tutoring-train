package at.bsd.tutoringtrain.controller;

import at.bsd.tutoringtrain.network.AuthenticationResult;
import at.bsd.tutoringtrain.network.Communicator;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
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
   
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        communicator = Communicator.getInstance();
        
    }    

    @FXML
    void onBtnLogin(ActionEvent event) {
        try {
            AuthenticationResult authresult;
            if ((authresult = communicator.authenticate(txtUsername.getText(), txtPassword.getText())) == AuthenticationResult.ALLOWED) {
                Parent root;
                Stage stage;
                root = FXMLLoader.load(getClass().getResource("/fxml/Main.fxml"));
                stage = new Stage();
                stage.setTitle("TutoringTrain - Admin Client");
                stage.setScene(new Scene(root));
                stage.show();
                
                ((Stage)btnLogin.getScene().getWindow()).close();
            } else {
                switch (authresult) {
                    case FORBIDDEN:
                        lblMessage.setText("Access forbidden! username or password incorrect.");
                        break;
                    case UNAUTHORIZED:
                        lblMessage.setText("Access forbidden! user not allowed to use admin client.");
                        break;
                    default:
                        lblMessage.setText("Unknown authentification result.");
                        break;
                }
            }
        } catch (Exception ex) {
            lblMessage.setText(ex.getMessage());
        }
    }
}
