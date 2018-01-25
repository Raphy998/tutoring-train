package at.tutoringtrain.adminclient.ui.controller;

import at.tutoringtrain.adminclient.data.Subject;
import at.tutoringtrain.adminclient.datamapper.DataMapper;
import at.tutoringtrain.adminclient.datamapper.JsonSubjectViews;
import at.tutoringtrain.adminclient.internationalization.LocalizedValueProvider;
import at.tutoringtrain.adminclient.io.network.Communicator;
import at.tutoringtrain.adminclient.io.network.RequestResult;
import at.tutoringtrain.adminclient.io.network.listener.subject.RequestAllSubjectsListener;
import at.tutoringtrain.adminclient.main.ApplicationManager;
import at.tutoringtrain.adminclient.main.DataStorage;
import at.tutoringtrain.adminclient.main.ListItemFactory;
import at.tutoringtrain.adminclient.main.MessageCodes;
import at.tutoringtrain.adminclient.main.MessageContainer;
import at.tutoringtrain.adminclient.ui.TutoringTrainWindow;
import at.tutoringtrain.adminclient.ui.WindowService;
import at.tutoringtrain.adminclient.ui.listener.MessageListener;
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
import javafx.scene.layout.AnchorPane;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * FXML Controller class
 *
 * @author Marco Wilscher marco.wilscher@edu.htl-villach.at
 */
public class AllSubjectsController implements Initializable, TutoringTrainWindow, MessageListener, RequestAllSubjectsListener {
    @FXML
    private AnchorPane pane;
    @FXML
    private JFXTextField txtSearch;
    @FXML
    private JFXButton btnSearch;
    @FXML
    private JFXButton btnRefresh;
    @FXML
    private JFXListView<AnchorPane> lvSubjects;
    @FXML
    private JFXSpinner spinner;
    @FXML
    private JFXButton btnClose;

    private JFXSnackbar snackbar;    
    
    private Logger logger; 
    private DataStorage dataStorage;
    private DataMapper dataMapper;
    private Communicator communicator;
    private LocalizedValueProvider localizedValueProvider;
    private WindowService windowService;
    private ListItemFactory listItemFactory;
    
    private ObservableList<AnchorPane> listItems;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        logger = LogManager.getLogger(this);
        communicator = ApplicationManager.getCommunicator();
        dataStorage = ApplicationManager.getDataStorage();
        windowService = ApplicationManager.getWindowService();
        dataMapper = ApplicationManager.getDataMapper();
        localizedValueProvider = ApplicationManager.getLocalizedValueProvider();
        listItemFactory = ApplicationManager.getListItemFactory();
        listItems = lvSubjects.getItems();
        initializeControls();
        logger.debug("AllSubjectsController initialized"); 
        
        try {
            loadSubjectsFromWebService();
        } catch (Exception e) {
            logger.error("initialize: exception occurred", e);
        }
    }    
    
    private void initializeControls() {
        snackbar = new JFXSnackbar(pane);
        spinner.setVisible(false);
    }

    private void disableControls(boolean disable) {
        Platform.runLater(() -> {
            txtSearch.setDisable(true || disable);
            btnSearch.setDisable(true || disable);        
            btnRefresh.setDisable(disable);
            btnClose.setDisable(disable);
            spinner.setVisible(disable);
            lvSubjects.setDisable(disable);
        });
    }
    
    @FXML
    void onBtnClose(ActionEvent event) {
        closeWindow();
    }

    @FXML
    void onBtnRefresh(ActionEvent event) {
        try {
            loadSubjectsFromWebService();
        } catch (Exception e) {
            logger.error("onBtnRefresh: exception occurred", e);
        }
    }

    @FXML
    void onBtnSearch(ActionEvent event) {
        //TODO search
    }
    
    @Override
    public void displayMessage(MessageContainer container) {
        logger.debug(container.toString());
        windowService.displayMessage(snackbar, container);
    }
    
    @Override
    public void closeWindow() {
        windowService.closeWindow(pane);
    }
    
    private void loadSubjectsFromWebService() throws Exception {
        disableControls(true);
        if (!communicator.requestAllSubjects(this)) {
            //TODO reauth
            disableControls(false);
        }
    }
    
    public boolean removeListItem(AnchorPane listItem) {
        return listItems.remove(listItem);
    }

    @Override
    public void requestGetAllSubjectsFinished(RequestResult result) {
        disableControls(false);
        if (result.isSuccessful()) {
            Platform.runLater(() -> {
                try {
                    dataStorage.clearSubjects();
                    listItems.clear();
                    for (Subject subject : dataMapper.toSubjectArrayList(result.getData(), JsonSubjectViews.In.Get.class)) {
                        dataStorage.addSubject(subject);
                        listItems.add(listItemFactory.generateSubjectListItem(subject, this, this));
                    } 
                } catch (IOException ioex) {
                    disableControls(false);
                    logger.error("requestGetAllSubjectsFinished: loading subjects failed", ioex);
                }
            });         
        } else {      
            displayMessage(result.getMessageContainer());
        }    
    }
    
    @Override
    public void requestFailed(RequestResult result) {
        disableControls(false);
        displayMessage(new MessageContainer(MessageCodes.REQUEST_FAILED, localizedValueProvider.getString("messageUnexpectedFailure")));
        logger.error("Request failed with status code:" + result.getStatusCode());
        logger.error(result.getMessageContainer().toString());
    }
}
