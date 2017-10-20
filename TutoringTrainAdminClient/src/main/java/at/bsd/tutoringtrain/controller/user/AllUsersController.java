package at.bsd.tutoringtrain.controller.user;

import at.bsd.tutoringtrain.data.Database;
import at.bsd.tutoringtrain.data.entities.User;
import at.bsd.tutoringtrain.data.mapper.DataMapper;
import at.bsd.tutoringtrain.data.mapper.views.JsonUserViews;
import at.bsd.tutoringtrain.debugging.MessageLogger;
import at.bsd.tutoringtrain.io.network.Communicator;
import at.bsd.tutoringtrain.io.network.RequestResult;
import at.bsd.tutoringtrain.io.network.listener.user.RequestAllUsersListener;
import at.bsd.tutoringtrain.messages.MessageCode;
import at.bsd.tutoringtrain.messages.MessageContainer;
import at.bsd.tutoringtrain.ui.listener.MessageListener;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXSnackbar;
import com.jfoenix.controls.JFXSpinner;
import com.jfoenix.controls.JFXTextField;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author Marco Wilscher marco.wilscher@edu.htl-villach.at
 */
public class AllUsersController implements Initializable, MessageListener, RequestAllUsersListener {
    @FXML
    private AnchorPane pane;
    @FXML
    private Label lblTitel;
    @FXML
    private JFXButton btnClose;
    @FXML
    private JFXSpinner spinner;
    @FXML
    private JFXListView<AnchorPane> lvUsers;
    @FXML
    private JFXTextField txtSearch;
    @FXML
    private JFXButton btnSearch;

    private JFXSnackbar snackbar;    
    private Communicator communicator;
    private Database db;
    private MessageLogger logger;   
    private ArrayList<User> users;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        logger = MessageLogger.getINSTANCE();
        communicator = Communicator.getInstance();
        db = Database.getInstance();           
        snackbar = new JFXSnackbar(pane);  
        try {
            lockUi(true);
            loadAllUsers();       
        } catch (Exception ex) {
            lockUi(false);
            displayMessage(new MessageContainer(MessageCode.EXCEPTION, ex.getMessage()));
            logger.exception(ex, getClass());
        }      
    }   
    
    private void loadAllUsers() throws Exception {
        if (!communicator.requestAllUsers(this)) {
            displayMessage(new MessageContainer(MessageCode.LOGIN_REQUIRED, "New login required!"));
        }
    }
    
    @FXML
    void onBtnClose(ActionEvent event) {
        closeWindow();
    }

    @FXML
    void onBtnSearch(ActionEvent event) {

    }
    
    private void lockUi(boolean lock) {
        Platform.runLater(() -> {      
            btnClose.setDisable(lock);
            lvUsers.setDisable(lock);
            spinner.setVisible(lock);
        });
    }
    
    private void closeWindow() {
        ((Stage)pane.getScene().getWindow()).close();
    }
    
    @Override
    public void displayMessage(MessageContainer container) {
        Platform.runLater(() -> snackbar.enqueue(new JFXSnackbar.SnackbarEvent(container.toString())));
    }

    @Override
    public void requestAllUsersFinished(RequestResult result) {
        try {
            lockUi(false);
            if (result.isSuccessful()) {
                users = DataMapper.toUsers(result.getData(), JsonUserViews.In.Get.class);
                Platform.runLater(() -> users.forEach((user) -> {
                    lvUsers.getItems().add(generateListItem(user, this));
                }));
            } else {                
                displayMessage(result.getMessageContainer());
            }  
        } catch (IOException ex) {
            lockUi(false);
            logger.exception(ex, getClass());
        }
    }

    @Override
    public void requestFailed(RequestResult result) {
        lockUi(false);
        displayMessage(result.getMessageContainer());
    }
    
    private AnchorPane generateListItem(User user, MessageListener listener) {
        AnchorPane userPane;
        FXMLLoader loader;
        UserListItemController controller;
        userPane = null;
        try {
            loader = new FXMLLoader(getClass().getResource("/fxml/UserListItem.fxml"));
            userPane = loader.<AnchorPane>load();
            controller = (UserListItemController)loader.getController();
            controller.setUser(user);
            controller.setMessageListener(listener);
        } catch (IOException ioex) {
            logger.exception(ioex, getClass());
        }
        return userPane;
    }
}
