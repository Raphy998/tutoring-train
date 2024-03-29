package at.tutoringtrain.adminclient.ui.controller;

import at.tutoringtrain.adminclient.data.user.User;
import at.tutoringtrain.adminclient.internationalization.LocalizedValueProvider;
import at.tutoringtrain.adminclient.internationalization.StringPlaceholder;
import at.tutoringtrain.adminclient.io.network.Communicator;
import at.tutoringtrain.adminclient.main.ApplicationManager;
import at.tutoringtrain.adminclient.main.MessageCodes;
import at.tutoringtrain.adminclient.main.MessageContainer;
import at.tutoringtrain.adminclient.ui.TutoringTrainWindow;
import at.tutoringtrain.adminclient.ui.WindowService;
import at.tutoringtrain.adminclient.ui.listener.ApplicationExitListener;
import at.tutoringtrain.adminclient.ui.listener.UserDataChangedListner;
import at.tutoringtrain.adminclient.ui.search.SearchCategory;
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
public class MainController implements Initializable, ApplicationExitListener, TutoringTrainWindow, UserDataChangedListner {
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
    private JFXListView<AnchorPane> lvResult;
    @FXML
    private JFXTextField txtSearch;
    @FXML
    private JFXButton btnSearch;
    @FXML
    private JFXComboBox<SearchCategory> comboCategorie;
    @FXML
    private JFXSpinner spinner;
    
    private JFXSnackbar snackbar; 
    private ApplicationManager applicationManager;
    private Logger logger;
    private WindowService windowService;
    private LocalizedValueProvider localizedValueProvider;
    private Communicator communicator;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        localizedValueProvider = ApplicationManager.getLocalizedValueProvider();
        windowService = WindowService.getInstance();
        logger = LogManager.getLogger(this);
        communicator = Communicator.getInstance();
        applicationManager = ApplicationManager.getInstance();
        applicationManager.registerMainApplicationExitListener(this);
        applicationManager.addCurrentUserDataChangedListener(this);
        snackbar = new JFXSnackbar(pane);
        comboCategorie.getItems().addAll(SearchCategory.USER, SearchCategory.SUBJECT, SearchCategory.OFFER);
        comboCategorie.getSelectionModel().select(SearchCategory.USER);
        setWelcomeMessage(applicationManager.getCurrentUser());
    }
    
    @FXML
    void onBtnAllAccounts(ActionEvent event) {
        try {
            windowService.openShowAllUsersWindow();
        } catch (Exception ex) {
            logger.error(ex);
            displayMessage(new MessageContainer(MessageCodes.EXCEPTION, localizedValueProvider.getString("messageUnexpectedFailure")));
        }
    }

    @FXML
    void onBtnAllOffers(ActionEvent event) {
        try {
            windowService.openShowAllOffersWindow();
        } catch (Exception ex) {
            logger.error(ex);
            displayMessage(new MessageContainer(MessageCodes.EXCEPTION, localizedValueProvider.getString("messageUnexpectedFailure")));
        }
    }

    @FXML
    void onBtnAllSubjects(ActionEvent event) {
        try {
            windowService.openShowAllSubjectsWindow();  
        } catch (Exception ex) {
            logger.error(ex);
            displayMessage(new MessageContainer(MessageCodes.EXCEPTION, localizedValueProvider.getString("messageUnexpectedFailure")));
        }
    }

    @FXML
    void onBtnExit(ActionEvent event) {
        try {
            closeWindow();
            Platform.exit();
        } catch (Exception ex) {
            logger.error(ex);
            displayMessage(new MessageContainer(MessageCodes.EXCEPTION, localizedValueProvider.getString("messageUnexpectedFailure")));
        }      
    }

    @FXML
    void onBtnLogout(ActionEvent event) {
        try {
            communicator.closeSession();
            closeWindow();
            windowService.openAuthenticationWindow();
        } catch (IOException ioex) {
            logger.error(ioex);
            displayMessage(new MessageContainer(MessageCodes.EXCEPTION, localizedValueProvider.getString("messageUnexpectedFailure")));
        }       
    }

    @FXML
    void onBtnNewAccount(ActionEvent event) {
        try {
            windowService.openRegisterUserWindow();
        } catch (IOException ioex) {
            logger.error(ioex);
            displayMessage(new MessageContainer(MessageCodes.EXCEPTION, localizedValueProvider.getString("messageUnexpectedFailure")));
        }      
    }

    @FXML
    void onBtnNewSubject(ActionEvent event) {
        try {
            windowService.openRegisterSubjectWindow();
        } catch (IOException ioex) {
            logger.error(ioex);
            displayMessage(new MessageContainer(MessageCodes.EXCEPTION, localizedValueProvider.getString("messageUnexpectedFailure")));
        }
    }

    @FXML
    void onBtnOwnAccount(ActionEvent event) {
        try {
            windowService.openUpdateUserWindow(applicationManager.getCurrentUser());
        } catch (Exception ex) {
            logger.error(ex);
            displayMessage(new MessageContainer(MessageCodes.EXCEPTION, localizedValueProvider.getString("messageUnexpectedFailure")));
        }
    }

    @FXML
    void onBtnSearch(ActionEvent event) {
        try {
            
        } catch (Exception ex) {
            logger.error(ex);
            displayMessage(new MessageContainer(MessageCodes.EXCEPTION, localizedValueProvider.getString("messageUnexpectedFailure")));
        }
    }

    @FXML
    void onBtnSettings(ActionEvent event) {
        try {
            windowService.openSettingsWindow();
        } catch (Exception ex) {
            logger.error(ex);
            displayMessage(new MessageContainer(MessageCodes.EXCEPTION, localizedValueProvider.getString("messageUnexpectedFailure")));
        }
    }
    
    @Override
    public void applicationExit() {
        closeWindow();
        ApplicationManager.shutdown();
        Platform.exit();
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
}