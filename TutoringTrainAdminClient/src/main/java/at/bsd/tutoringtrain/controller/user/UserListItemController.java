package at.bsd.tutoringtrain.controller.user;

import at.bsd.tutoringtrain.data.entities.User;
import at.bsd.tutoringtrain.data.enumeration.Gender;
import at.bsd.tutoringtrain.debugging.MessageLogger;
import at.bsd.tutoringtrain.messages.MessageContainer;
import at.bsd.tutoringtrain.ui.listener.MessageListener;
import at.bsd.tutoringtrain.ui.listener.UserDataChangedListner;
import com.jfoenix.controls.JFXButton;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author Marco Wilscher marco.wilscher@edu.htl-villach.at
 */
public class UserListItemController implements Initializable, UserDataChangedListner {
    @FXML
    private ImageView ivAvatar;
    @FXML
    private JFXButton btnEdit;
    @FXML
    private Label lblName;
    @FXML
    private Label lblUsername;
    @FXML
    private Label lblEmail;
    @FXML
    private Label lblEducation;
    @FXML
    private Label lblGender;
    @FXML
    private AnchorPane paneBlocked;
    @FXML
    private Label lblReason;
    @FXML
    private Label lblDue;

    
    private MessageLogger logger;
    private MessageListener messageListener;
    private User user;
    private BufferedImage avatar;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        logger = MessageLogger.getINSTANCE();
    }    
    
    @FXML
    void onBtnEdit(ActionEvent event) {
        if (user != null) {
            openUpdateUserWindow(user, false, this);
        }
    }

    public void setMessageListener(MessageListener messageListener) {
        this.messageListener = messageListener;
    }
    
    private void displayMessage(MessageContainer container) {
        if (messageListener != null) {
            messageListener.displayMessage(container);
        }
    }
    
    public void setUser(User user) {
        this.user = user;
        displayUser();
    }
    
    private void displayUser() {
        if (user != null) {
            lblUsername.setText("@" + user.getUsername());
            lblName.setText(user.getName());
            lblEducation.setText(user.getEducation());
            lblGender.setText(Gender.get(user.getGender()).getName());
            lblEmail.setText(user.getEmail());
            if (user.getBlock() != null) {
                paneBlocked.setVisible(true);
                lblReason.setText(user.getBlock().getReason());
                lblDue.setText(new SimpleDateFormat("dd.MM.yyyy hh:mm").format(user.getBlock().getDuedate()));
            } else {
                paneBlocked.setVisible(false);
            }
        }
    }
    
    private void openUpdateUserWindow(User user, boolean ownAccount, UserDataChangedListner userDataChangedListner) {
        Parent root;
        Stage stage;
        FXMLLoader loader;
        UpdateUserController controller;
        try {
            loader = new FXMLLoader(getClass().getResource("/fxml/UpdateUser.fxml"));
            root = loader.load();
            controller = (UpdateUserController)loader.getController();
            stage = new Stage();
            stage.setTitle("TutoringTrain - Admin Client");
            stage.setResizable(false);
            stage.setScene(new Scene(root));
            stage.show();
            controller.setUser(user);
            controller.setTitle(ownAccount ? "Own Account" : "Update Account");
            controller.setOwnAccout(ownAccount);
            controller.setUserDataChangedListner(userDataChangedListner);
        } catch (IOException ioex) {
            logger.exception(ioex, getClass());
        }
    }

    @Override
    public void userDataChanged(User user) {
        setUser(user);
        displayUser();
    }
}
