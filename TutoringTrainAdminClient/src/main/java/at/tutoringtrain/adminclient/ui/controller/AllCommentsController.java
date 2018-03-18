package at.tutoringtrain.adminclient.ui.controller;

import at.tutoringtrain.adminclient.data.entry.Comment;
import at.tutoringtrain.adminclient.data.entry.Entry;
import at.tutoringtrain.adminclient.data.entry.EntryType;
import at.tutoringtrain.adminclient.data.mapper.DataMapper;
import at.tutoringtrain.adminclient.data.mapper.DataMappingViews;
import at.tutoringtrain.adminclient.internationalization.LocalizedValueProvider;
import at.tutoringtrain.adminclient.io.network.Communicator;
import at.tutoringtrain.adminclient.io.network.RequestResult;
import at.tutoringtrain.adminclient.io.network.listener.entry.comment.RequestCommentsOfEntryListener;
import at.tutoringtrain.adminclient.main.ApplicationManager;
import at.tutoringtrain.adminclient.main.ListItemFactory;
import at.tutoringtrain.adminclient.main.MessageCodes;
import at.tutoringtrain.adminclient.main.MessageContainer;
import at.tutoringtrain.adminclient.ui.TutoringTrainWindow;
import at.tutoringtrain.adminclient.ui.WindowService;
import at.tutoringtrain.adminclient.ui.listener.MessageListener;
import at.tutoringtrain.adminclient.ui.listener.RemoveItemListener;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXSnackbar;
import com.jfoenix.controls.JFXSpinner;
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
public class AllCommentsController implements Initializable, TutoringTrainWindow, MessageListener, RemoveItemListener, RequestCommentsOfEntryListener  {
    @FXML
    private AnchorPane pane;
    @FXML
    private JFXButton btnRefresh;
    @FXML
    private JFXListView<AnchorPane> lvComments;
    @FXML
    private JFXSpinner spinner;
    @FXML
    private JFXButton btnClose;

    private JFXSnackbar snackbar;    
    
    private Logger logger; 
    private DataMapper dataMapper;
    private Communicator communicator;
    private LocalizedValueProvider localizedValueProvider;
    private WindowService windowService;
    private ListItemFactory listItemFactory;
    
    private ObservableList<AnchorPane> listItems;

    private Entry entry;
    private EntryType entryType;
        
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        logger = LogManager.getLogger(this);
        communicator = ApplicationManager.getCommunicator();
        windowService = ApplicationManager.getWindowService();
        dataMapper = ApplicationManager.getDataMapper();
        localizedValueProvider = ApplicationManager.getLocalizedValueProvider();
        listItemFactory = ApplicationManager.getListItemFactory();
        listItems = lvComments.getItems();
        initializeControls();
        logger.debug("AllCommentsController initialized"); 
    }   
    
    private void initializeControls() {
        snackbar = new JFXSnackbar(pane);
        spinner.setVisible(false);
    }
    
    private void disableControls(boolean disable) {
        Platform.runLater(() -> { 
            btnRefresh.setDisable(disable);
            btnClose.setDisable(disable);
            spinner.setVisible(disable);
            lvComments.setDisable(disable);
        });
    }
    
    @FXML
    void onBtnRefresh(ActionEvent event) {
        try {
            loadCommentsFromWebService();
        } catch (Exception e) {
            logger.error("onBtnRefresh: exception occurred", e);
        }
    }
    
    @FXML
    void onBtnClose(ActionEvent event) {
        closeWindow();
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

    public void setEntry(Entry entry, EntryType entryType) {
        this.entry = entry;
        this.entryType = entryType;
        try {
            loadCommentsFromWebService();
        } catch (Exception e) {
            logger.error("initialize: exception occurred", e);
        }
    }
    
    private void loadCommentsFromWebService() throws Exception {
        disableControls(true);
        if (!communicator.requestCommentsOfEntry(this, entry.getId(), entryType)) {
            disableControls(false);
            displayMessage(new MessageContainer(MessageCodes.EXCEPTION, localizedValueProvider.getString("messageReauthentication")));
        }
    }
    
    @Override
    public boolean removeListItem(AnchorPane listItem) {
        return listItems.remove(listItem);
    }
    
    @Override
    public void requestGetCommentsOfEntryFinished(RequestResult result) {
        disableControls(false);
        if (result.isSuccessful()) {
            Platform.runLater(() -> {
                try {
                    listItems.clear();              
                    for (Comment comment : dataMapper.toCommentArrayList(result.getData(), DataMappingViews.Entry.Comment.In.Get.class)) {
                        listItems.add(listItemFactory.generateCommentListItem(entry, entryType, comment, this, this));
                    } 
                    if (listItems.isEmpty()) {
                        listItems.add(listItemFactory.generateMessageListItem("messageNoComments", true));
                    }
                } catch (IOException ioex) {
                    disableControls(false);
                    logger.error("requestGetAllSubjectsFinished: loading subjects failed", ioex);
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
}
