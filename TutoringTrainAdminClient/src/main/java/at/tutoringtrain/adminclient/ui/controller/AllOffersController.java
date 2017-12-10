package at.tutoringtrain.adminclient.ui.controller;

import at.tutoringtrain.adminclient.data.entry.Offer;
import at.tutoringtrain.adminclient.data.mapper.DataMapper;
import at.tutoringtrain.adminclient.data.mapper.DataMappingViews;
import at.tutoringtrain.adminclient.internationalization.LocalizedValueProvider;
import at.tutoringtrain.adminclient.io.network.Communicator;
import at.tutoringtrain.adminclient.io.network.RequestResult;
import at.tutoringtrain.adminclient.io.network.listener.entry.offer.RequestNewestOffersListener;
import at.tutoringtrain.adminclient.io.network.listener.entry.offer.RequestOfferCountListener;
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
public class AllOffersController implements Initializable, TutoringTrainWindow, MessageListener, RequestNewestOffersListener, RequestOfferCountListener {
    @FXML
    private AnchorPane pane;
    @FXML
    private JFXTextField txtSearch;
    @FXML
    private JFXButton btnSearch;
    @FXML
    private JFXButton btnRefresh;
    @FXML
    private JFXListView<AnchorPane> lvOffers;
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
        listItems = lvOffers.getItems();
        initializeControls();
        logger.debug("AllOffersController initialized"); 
        
        try {
            loadOffersFromWebService();
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
            lvOffers.setDisable(disable);
        });
    }
    
    @FXML
    void onBtnClose(ActionEvent event) {
        closeWindow();
    }

    @FXML
    void onBtnRefresh(ActionEvent event) {
        try {
            loadOffersFromWebService();
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
    
    private void loadOffersFromWebService() throws Exception {
        disableControls(true);
        if (!communicator.requestNewestOffers(this, 0, Integer.MAX_VALUE)) {
            //TODO reauth
            disableControls(false);
        }
    }

    @Override
    public void requestGetNewestOffersFinished(RequestResult result) {
       disableControls(false);
        if (result.isSuccessful()) {
            Platform.runLater(() -> {
                try {
                    listItems.clear();
                    for (Offer offer : dataMapper.toOfferArrayList(result.getData(), DataMappingViews.Entry.In.Get.class)) {
                        listItems.add(listItemFactory.generateOfferListItem(offer, this));
                    } 
                } catch (IOException ioex) {
                    disableControls(false);
                    logger.error("requestGetNewestOffersFinished: loading subjects failed", ioex);
                }
            });         
        } else {      
            displayMessage(result.getMessageContainer());
        }   
    }

    @Override
    public void requestOfferCountFinished(RequestResult result) {
        
    }
    
    @Override
    public void requestFailed(RequestResult result) {
        disableControls(false);
        displayMessage(new MessageContainer(MessageCodes.REQUEST_FAILED, localizedValueProvider.getString("messageUnexpectedFailure")));
        logger.error("Request failed with status code:" + result.getStatusCode());
        logger.error(result.getMessageContainer().toString());
    }
}
