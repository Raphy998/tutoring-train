package at.tutoringtrain.adminclient.ui.controller;

import at.tutoringtrain.adminclient.data.entry.Offer;
import at.tutoringtrain.adminclient.data.entry.Request;
import at.tutoringtrain.adminclient.data.mapper.DataMapper;
import at.tutoringtrain.adminclient.data.mapper.DataMappingViews;
import at.tutoringtrain.adminclient.data.user.User;
import at.tutoringtrain.adminclient.data.user.UserRole;
import at.tutoringtrain.adminclient.internationalization.LocalizedValueProvider;
import at.tutoringtrain.adminclient.internationalization.StringPlaceholder;
import at.tutoringtrain.adminclient.io.network.Communicator;
import at.tutoringtrain.adminclient.io.network.RequestResult;
import at.tutoringtrain.adminclient.io.network.listener.entry.offer.RequestOfferSimpleSearchListener;
import at.tutoringtrain.adminclient.io.network.listener.entry.request.RequestRequestSimpleSearchListener;
import at.tutoringtrain.adminclient.io.network.listener.user.newsletter.RequestNewsletterListener;
import at.tutoringtrain.adminclient.main.ApplicationManager;
import at.tutoringtrain.adminclient.main.ListItemFactory;
import at.tutoringtrain.adminclient.main.MessageCodes;
import at.tutoringtrain.adminclient.main.MessageContainer;
import at.tutoringtrain.adminclient.ui.TutoringTrainWindow;
import at.tutoringtrain.adminclient.ui.WindowService;
import at.tutoringtrain.adminclient.ui.listener.ApplicationExitListener;
import at.tutoringtrain.adminclient.ui.listener.MessageListener;
import at.tutoringtrain.adminclient.ui.listener.RemoveItemListener;
import at.tutoringtrain.adminclient.ui.listener.UserDataChangedListner;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXSnackbar;
import com.jfoenix.controls.JFXSpinner;
import com.jfoenix.controls.JFXTextField;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * FXML Controller class
 *
 * @author Marco Wilscher marco.wilscher@edu.htl-villach.at
 */
public class MainController implements Initializable, ApplicationExitListener, TutoringTrainWindow, UserDataChangedListner, RequestOfferSimpleSearchListener, RequestRequestSimpleSearchListener, MessageListener, RemoveItemListener, RequestNewsletterListener {
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
    private JFXButton btnNewsletter;
    @FXML
    private JFXButton btnLogout;
    @FXML
    private JFXButton btnExit;
    @FXML
    private JFXTextField txtSearch;
    @FXML
    private JFXButton btnSearch;
    @FXML
    private JFXButton btnMapSearch;
    @FXML
    private JFXButton btnClear;
    @FXML
    private JFXSpinner spinner;
    @FXML
    private JFXListView<AnchorPane> lvEntries;
    @FXML
    private Label lblWelcome;
    
    private JFXSnackbar snackbar; 
    private ApplicationManager applicationManager;
    private DataMapper dataMapper;
    private Logger logger;
    private WindowService windowService;
    private LocalizedValueProvider localizedValueProvider;
    private ListItemFactory listItemFactory;
    private Communicator communicator;
    private boolean loadedReq, loadedOff;
    
    private ObservableList<AnchorPane> listItems;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        localizedValueProvider = ApplicationManager.getLocalizedValueProvider();
        windowService = WindowService.getInstance();
        logger = LogManager.getLogger(this);
        communicator = Communicator.getInstance();
        dataMapper = DataMapper.getINSTANCE();
        applicationManager = ApplicationManager.getInstance();
        applicationManager.registerMainApplicationExitListener(this);
        listItemFactory = ListItemFactory.getINSTANCE();
        applicationManager.addCurrentUserDataChangedListener(this);
        listItems = lvEntries.getItems();
        snackbar = new JFXSnackbar(pane);
        if (UserRole.valueOf(applicationManager.getCurrentUser().getRole()) == UserRole.ROOT) {
            btnMyAccount.setDisable(true);
        }
        setWelcomeMessage(applicationManager.getCurrentUser());
        
        txtSearch.setOnKeyReleased((KeyEvent event) -> {
            if (event.getCode().equals(KeyCode.ENTER)) {
                btnSearch.fire();
            } else if (event.getCode().equals(KeyCode.DELETE)) {
                btnClear.fire();
            }
        });
    }
    
    private void disableControls(boolean disable) {
        Platform.runLater(() -> { 
            btnSearch.setDisable(disable);
            btnClear.setDisable(disable);
            spinner.setVisible(disable);
            lvEntries.setDisable(disable);
        });
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
            btnClear.fire();
            windowService.openShowAllOffersWindow();
        } catch (Exception ex) {
            logger.error(ex);
            displayMessage(new MessageContainer(MessageCodes.EXCEPTION, localizedValueProvider.getString("messageUnexpectedFailure")));
        }
    }
    
    @FXML
    void onBtnAllRequests(ActionEvent event) {
        try {
            btnClear.fire();
            windowService.openShowAllRequestsWindow();
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
    void onBtnMapSearch(ActionEvent event) {
        try {
            windowService.openLocationMapSearchWindow();
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
    void onBtnSettings(ActionEvent event) {
        try {
            windowService.openSettingsWindow();
        } catch (Exception ex) {
            logger.error(ex);
            displayMessage(new MessageContainer(MessageCodes.EXCEPTION, localizedValueProvider.getString("messageUnexpectedFailure")));
        }
    }
    
    @FXML
    void onBtnNewsletter(ActionEvent event) {
        try {
            windowService.openNewsletterWindow();
        } catch (Exception ex) {
            logger.error(ex);
            displayMessage(new MessageContainer(MessageCodes.EXCEPTION, localizedValueProvider.getString("messageUnexpectedFailure")));
        }
    }
    
    @FXML
    void onBtnClear(ActionEvent event) {
        try {
            txtSearch.setText("");
            listItems.clear();
        } catch (Exception ex) {
            logger.error(ex);
            displayMessage(new MessageContainer(MessageCodes.EXCEPTION, localizedValueProvider.getString("messageUnexpectedFailure")));
        }
    }
    
    @FXML
    void onBtnSearch(ActionEvent event) {
        try {
            disableControls(true);
            loadedOff = false;
            loadedReq = false;
            listItems.clear();
            if (!communicator.requestRequestSimpleSearch(this, txtSearch.getText()) || !communicator.requestOfferSimpleSearch(this, txtSearch.getText())) {
                disableControls(false);
                displayMessage(new MessageContainer(MessageCodes.EXCEPTION, localizedValueProvider.getString("messageReauthentication")));
            }
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
    public void requestNewsletterFinished(RequestResult result) {
        displayMessage(result.getMessageContainer());
    }

    @Override
    public void userDataChanged(User user) {
        setWelcomeMessage(user);
    }

    @Override
    public void requestOfferSimpleSearchFinished(RequestResult result) {
        if (result.isSuccessful()) {
            Platform.runLater(() -> {
                try {          
                    for (Offer offer : dataMapper.toOfferArrayList(result.getData(), DataMappingViews.Entry.In.Get.class)) {
                        listItems.add(listItemFactory.generateOfferListItem(offer, this, this));
                    }
                    loadedOff = true;
                    if (loadedReq) {
                        disableControls(false);
                        if (listItems.isEmpty()) {
                            listItems.add(listItemFactory.generateMessageListItem("messageNoEntries", true));
                        }
                    }
                } catch (IOException ioex) {
                    disableControls(false);
                    logger.error("requestRequestSimpleSearchFinished: loading requests failed", ioex);
                }
            });         
        } else {     
            if (result.getMessageContainer().getCode() == 3 || result.getMessageContainer().getCode() == 4) {
                displayMessage(new MessageContainer(MessageCodes.EXCEPTION, localizedValueProvider.getString("messageReauthentication")));
            } else {
                displayMessage(result.getMessageContainer());
            }
        } 
    }

    @Override
    public void requestRequestSimpleSearchFinished(RequestResult result) {
        if (result.isSuccessful()) {
            Platform.runLater(() -> {
                try {          
                    for (Request request : dataMapper.toRequestArrayList(result.getData(), DataMappingViews.Entry.In.Get.class)) {
                        listItems.add(listItemFactory.generateRequestListItem(request, this, this));
                    }
                    loadedReq = true;
                    if (loadedOff) {
                        disableControls(false);
                        if (listItems.isEmpty()) {
                            listItems.add(listItemFactory.generateMessageListItem("messageNoEntries", true));
                        }
                    }
                } catch (IOException ioex) {
                    disableControls(false);
                    logger.error("requestRequestSimpleSearchFinished: loading requests failed", ioex);
                }
            });         
        } else {     
            if (result.getMessageContainer().getCode() == 3 || result.getMessageContainer().getCode() == 4) {
                displayMessage(new MessageContainer(MessageCodes.EXCEPTION, localizedValueProvider.getString("messageReauthentication")));
            } else {
                displayMessage(result.getMessageContainer());
            }
        }  
    }
    
    @Override
    public void requestFailed(RequestResult result) {
        disableControls(false);
        displayMessage(new MessageContainer(MessageCodes.REQUEST_FAILED, localizedValueProvider.getString("messageUnexpectedFailure")));
        logger.error("Request failed with status code:" + result.getStatusCode());
        logger.error(result.getMessageContainer().toString());
    }
    
    @Override
    public boolean removeListItem(AnchorPane item) {
        return listItems.remove(item);
    }
}