package at.tutoringtrain.adminclient.ui.controller;

import at.tutoringtrain.adminclient.data.User;
import at.tutoringtrain.adminclient.internationalization.LocalizedValueProvider;
import at.tutoringtrain.adminclient.internationalization.StringPlaceholder;
import at.tutoringtrain.adminclient.io.network.Communicator;
import at.tutoringtrain.adminclient.io.network.WebserviceOperation;
import at.tutoringtrain.adminclient.main.ApplicationManager;
import at.tutoringtrain.adminclient.main.MessageCodes;
import at.tutoringtrain.adminclient.main.MessageContainer;
import at.tutoringtrain.adminclient.ui.TutoringTrainWindowWithReauthentication;
import at.tutoringtrain.adminclient.ui.WindowService;
import at.tutoringtrain.adminclient.ui.listener.ApplicationExitListener;
import at.tutoringtrain.adminclient.ui.listener.ReauthenticationListener;
import at.tutoringtrain.adminclient.ui.listener.UserDataChangedListner;
import at.tutoringtrain.adminclient.ui.search.SearchCategory;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXSnackbar;
import com.jfoenix.controls.JFXSpinner;
import com.jfoenix.controls.JFXTextField;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * FXML Controller class
 *
 * @author Marco Wilscher marco.wilscher@edu.htl-villach.at
 */
public class MainController implements Initializable, ApplicationExitListener, TutoringTrainWindowWithReauthentication, ReauthenticationListener, UserDataChangedListner {
    @FXML
    private AnchorPane pane;
    @FXML
    private JFXButton btnMyAccount;
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
    
    private WindowService windowService;
    private LocalizedValueProvider localizedValueProvider;
    private Communicator communicator;
    private ApplicationManager db;
    private Logger logger;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try { 
            localizedValueProvider = ApplicationManager.getLocalizedValueProvider();
        windowService = WindowService.getInstance();
        logger = LogManager.getLogger(this);
        communicator = Communicator.getInstance();
        db = ApplicationManager.getInstance();
        db.registerMainApplicationExitListener(this);
        db.addCurrentUserDataChangedListener(this);
        snackbar = new JFXSnackbar(pane);
        comboCategorie.getItems().addAll(SearchCategory.ALL, SearchCategory.USER, SearchCategory.SUBJECT, SearchCategory.OFFER);
        comboCategorie.getSelectionModel().select(SearchCategory.ALL);
        
            setWelcomeMessage(db.getCurrentUser());
            
            
        } catch (Exception ex) {
            logger.error("Exception", ex);
            onBtnLogout(null);
        }
    }
    
    @FXML
    void onBtnAllAccounts(ActionEvent event) {
        try {
            windowService.openShowAllUsersWindow();
        } catch (Exception ex) {
            logger.error("Exception", ex);
            displayMessage(new MessageContainer(MessageCodes.EXCEPTION, "Something unexpected happended! (see log for further information)"));
        }
    }

    @FXML
    void onBtnAllOffers(ActionEvent event) {
        try {
            
        } catch (Exception ex) {
            logger.error("Exception", ex);
            displayMessage(new MessageContainer(MessageCodes.EXCEPTION, "Something unexpected happended! (see log for further information)"));
        }
    }

    @FXML
    void onBtnAllSubjects(ActionEvent event) {
        try {
            windowService.openShowAllSubjectsWindow();
            
            
            
            
        } catch (Exception ex) {
            logger.error("Exception", ex);
            displayMessage(new MessageContainer(MessageCodes.EXCEPTION, "Something unexpected happended! (see log for further information)"));
        }
    }

    @FXML
    void onBtnExit(ActionEvent event) {
        try {
            closeWindow();
            Platform.exit();
        } catch (Exception ex) {
            logger.error("Exception", ex);
            displayMessage(new MessageContainer(MessageCodes.EXCEPTION, "Something unexpected happended! (see log for further information)"));
        }      
    }

    @FXML
    void onBtnLogout(ActionEvent event) {
        try {
            communicator.closeSession();
            closeWindow();
            windowService.openAuthenticationWindow();
        } catch (Exception ex) {
            logger.error("Exception", ex);
            displayMessage(new MessageContainer(MessageCodes.EXCEPTION, "Something unexpected happended! (see log for further information)"));
        }       
    }

    @FXML
    void onBtnNewAccount(ActionEvent event) {
        try {
            windowService.openRegisterUserWindow(this);
        } catch (Exception ex) {
            logger.error("Exception", ex);
            displayMessage(new MessageContainer(MessageCodes.EXCEPTION, "Something unexpected happended! (see log for further information)"));
        }      
    }

    @FXML
    void onBtnNewSubject(ActionEvent event) {
        try {
            windowService.openRegisterSubjectWindow();
        } catch (Exception ex) {
            logger.error("Exception", ex);
            displayMessage(new MessageContainer(MessageCodes.EXCEPTION, "Something unexpected happended! (see log for further information)"));
        }
    }

    @FXML
    void onBtnOwnAccount(ActionEvent event) {
        try {
            windowService.openUpdateUserWindow(db.getCurrentUser());
        } catch (Exception ex) {
            logger.error("Exception", ex);
            displayMessage(new MessageContainer(MessageCodes.EXCEPTION, "Something unexpected happended! (see log for further information)"));
        }
    }

    @FXML
    void onBtnSearch(ActionEvent event) {
        try {
            
        } catch (Exception ex) {
            logger.error("Exception", ex);
            displayMessage(new MessageContainer(MessageCodes.EXCEPTION, "Something unexpected happended! (see log for further information)"));
        }
    }

    @FXML
    void onBtnSettings(ActionEvent event) {
        try {
            
        } catch (Exception ex) {
            logger.error("Exception", ex);
            displayMessage(new MessageContainer(MessageCodes.EXCEPTION, "Something unexpected happended! (see log for further information)"));
        }
    }
    
    @Override
    public void applicationExit() {
        closeWindow();
        ApplicationManager.shutdown();
        Platform.exit();
    }
    

    @Override
    public void reauthenticateUser(WebserviceOperation webserviceOperation) {
        
    }

    @Override
    public void reauthenticationSuccessful(WebserviceOperation webserviceOperation) {
        
    }
    
    
    private void setWelcomeMessage(User user) {
        
        Platform.runLater(() -> lblWelcome.setText(localizedValueProvider.getString("messageWelcome", new StringPlaceholder("name", user.getName()))));
    }
    
    @Override
    public void displayMessage(MessageContainer message) {
        windowService.displayMessage(snackbar, message);
    }
    
    @Override
    public void closeWindow() {
        windowService.closeWindow(pane);
    }

    @Override
    public void userDataChanged(User user) {
        setWelcomeMessage(user);
    }

    @Override
    public void reauthenticationCanceled() {
        onBtnLogout(null);
    }
}