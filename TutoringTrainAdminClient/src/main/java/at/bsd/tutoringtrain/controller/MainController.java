package at.bsd.tutoringtrain.controller;

import at.bsd.tutoringtrain.controller.user.UpdateUserController;
import at.bsd.tutoringtrain.data.Database;
import at.bsd.tutoringtrain.data.entities.User;
import at.bsd.tutoringtrain.data.mapper.DataMapper;
import at.bsd.tutoringtrain.data.mapper.views.JsonUserViews;
import at.bsd.tutoringtrain.debugging.MessageLogger;
import at.bsd.tutoringtrain.io.network.Communicator;
import at.bsd.tutoringtrain.io.network.RequestResult;
import at.bsd.tutoringtrain.io.network.listener.user.RequestOwnUserListener;
import at.bsd.tutoringtrain.ui.listener.ReauthenticateListener;
import at.bsd.tutoringtrain.ui.listener.UserDataChangedListner;
import at.bsd.tutoringtrain.ui.search.SearchCategory;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXSnackbar;
import com.jfoenix.controls.JFXSpinner;
import com.jfoenix.controls.JFXTextField;
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
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author Marco Wilscher marco.wilscher@edu.htl-villach.at
 */
public class MainController implements Initializable, RequestOwnUserListener, UserDataChangedListner, ReauthenticateListener {
    @FXML
    private AnchorPane pane;

    @FXML
    private JFXButton btnOwnAccount;

    @FXML
    private JFXButton btnNewAccount;

    @FXML
    private JFXButton btnNewSubject;

    @FXML
    private JFXButton btnAllAccounts;

    @FXML
    private JFXButton btnAllSubjects;

    @FXML
    private JFXButton btnAllOffers;

    @FXML
    private JFXButton btnSettings;

    @FXML
    private JFXButton btnLogout;

    @FXML
    private JFXButton btnExit;

    @FXML
    private Label lblWelcome;

    @FXML
    private JFXListView<Void> lvResult;

    @FXML
    private JFXTextField txtSearch;

    @FXML
    private JFXButton btnSearch;

    @FXML
    private JFXComboBox<SearchCategory> comboCategorie;

    @FXML
    private JFXSpinner spinner;
    
    private JFXSnackbar snackbar; 
    
    private Communicator communicator;
    private Database db;
    private MessageLogger logger;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        logger = MessageLogger.getINSTANCE();
        communicator = Communicator.getInstance();
        db = Database.getInstance();
        db.addUserChangedListener(this);
        snackbar = new JFXSnackbar(pane);
        comboCategorie.getItems().addAll(SearchCategory.ALL, SearchCategory.USER, SearchCategory.SUBJECT, SearchCategory.OFFER);
        comboCategorie.getSelectionModel().select(SearchCategory.ALL);
        
        try {          
            if (communicator.requestOwnUser(this)) {
                
            } else {
                displayMessage("token expired");
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage(), getClass());
        }
    }
    
    @FXML
    void onBtnAllAccounts(ActionEvent event) {
        openWindow("/fxml/AllUsers.fxml", "TutoringTrain - Admin Client");
    }

    @FXML
    void onBtnAllOffers(ActionEvent event) {

    }

    @FXML
    void onBtnAllSubjects(ActionEvent event) {

    }

    @FXML
    void onBtnExit(ActionEvent event) {
        closeWindow();
        Platform.exit();
    }

    @FXML
    void onBtnLogout(ActionEvent event) {
        communicator.closeSession();
        closeWindow();
        openWindow("/fxml/Authentification.fxml", "TutoringTrain - Admin Client");
    }

    @FXML
    void onBtnNewAccount(ActionEvent event) {
        openWindow("/fxml/RegisterUser.fxml", "TutoringTrain - Admin Client");
    }

    @FXML
    void onBtnNewSubject(ActionEvent event) {

    }

    @FXML
    void onBtnOwnAccount(ActionEvent event) {
        if (db.getCurrentUser() != null) {
            openUpdateUserWindow(db.getCurrentUser(), true);
        }
    }

    @FXML
    void onBtnSearch(ActionEvent event) {

    }

    @FXML
    void onBtnSettings(ActionEvent event) {
        
    }
    
    @Override
    public void requestOwnUserFinished(RequestResult result) {
        try {
            if (result.isSuccessful()) {
                db.setCurrentUser(DataMapper.toUser(result.getData(), JsonUserViews.In.Get.class));
            }
            else {
                //TODO 
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage(), getClass());
        }
    }

    @Override
    public void requestFailed(RequestResult result) {
        logger.error(result.toString(), getClass());
    }
    
    private void setWelcomeMessage(User user) {
        Platform.runLater(() -> lblWelcome.setText("Welcome " + user.getName() + "!"));
    }
    
    private void displayMessage(String message) {
        Platform.runLater(() -> snackbar.show(message, 5000));
    }
    
    private <T> T openWindow(String fxml, String title) { 
        Parent root;
        Stage stage;
        FXMLLoader loader;
        T controller;
        controller = null;
        try {
            loader = new FXMLLoader(getClass().getResource(fxml));
            root = loader.load();
            controller = (T)loader.getController();
            stage = new Stage();
            stage.setTitle(title);
            stage.setResizable(false);
            stage.setScene(new Scene(root));

            stage.show();
        } catch (IOException ioex) {
            logger.exception(ioex, getClass());
        }
        return controller;
    }
    
    private void openUpdateUserWindow(User user, boolean ownAccount) {
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
        } catch (IOException ioex) {
            logger.exception(ioex, getClass());
        }
    }
    
    private void closeWindow() {
        ((Stage)pane.getScene().getWindow()).close();
    }

    @Override
    public void userDataChanged(User user) {
        setWelcomeMessage(user);
    }

    @Override
    public void authenticationSuccessful() {
    }

    @Override
    public void authenticationCanceled() {
        onBtnLogout(null);
    }
}