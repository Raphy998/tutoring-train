package at.tutoringtrain.adminclient.ui.controller;

import at.tutoringtrain.adminclient.data.entry.Offer;
import at.tutoringtrain.adminclient.data.mapper.DataMapper;
import at.tutoringtrain.adminclient.data.mapper.DataMappingViews;
import at.tutoringtrain.adminclient.internationalization.LocalizedValueProvider;
import at.tutoringtrain.adminclient.io.network.Communicator;
import at.tutoringtrain.adminclient.io.network.RequestResult;
import at.tutoringtrain.adminclient.io.network.listener.entry.offer.RequestNewestOffersListener;
import at.tutoringtrain.adminclient.io.network.listener.entry.offer.RequestOfferSearchListener;
import at.tutoringtrain.adminclient.main.ApplicationManager;
import at.tutoringtrain.adminclient.main.DataStorage;
import at.tutoringtrain.adminclient.main.ListItemFactory;
import at.tutoringtrain.adminclient.main.MessageCodes;
import at.tutoringtrain.adminclient.main.MessageContainer;
import at.tutoringtrain.adminclient.ui.TutoringTrainWindow;
import at.tutoringtrain.adminclient.ui.WindowService;
import at.tutoringtrain.adminclient.ui.listener.MessageListener;
import at.tutoringtrain.adminclient.ui.listener.RemoveItemListener;
import at.tutoringtrain.adminclient.ui.search.BooleanOperation;
import at.tutoringtrain.adminclient.ui.search.BooleanSearchCriteria;
import at.tutoringtrain.adminclient.ui.search.DateOperation;
import at.tutoringtrain.adminclient.ui.search.DateSearchCriteria;
import at.tutoringtrain.adminclient.ui.search.OrderDirection;
import at.tutoringtrain.adminclient.ui.search.OrderElement;
import at.tutoringtrain.adminclient.ui.search.SearchCriteria;
import at.tutoringtrain.adminclient.ui.search.StringOperation;
import at.tutoringtrain.adminclient.ui.search.StringSearchCriteria;
import at.tutoringtrain.adminclient.ui.search.entry.EntryActiveState;
import at.tutoringtrain.adminclient.ui.search.entry.EntryProp;
import at.tutoringtrain.adminclient.ui.search.entry.EntrySearch;
import at.tutoringtrain.adminclient.ui.validators.TextFieldValidator;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXSnackbar;
import com.jfoenix.controls.JFXSpinner;
import com.jfoenix.controls.JFXTextField;
import java.io.IOException;
import java.net.URL;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
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
public class AllOffersController implements Initializable, TutoringTrainWindow, MessageListener, RequestNewestOffersListener, RequestOfferSearchListener, RemoveItemListener {
    @FXML
    private AnchorPane pane;
    @FXML
    private JFXTextField txtSearch;
    @FXML
    private JFXButton btnSearch;
    @FXML
    private JFXComboBox<EntryProp> comboProperty;
    @FXML
    private JFXComboBox<StringOperation> comboOperation;
    @FXML
    private JFXComboBox<OrderDirection> comboOrder;
    @FXML
    private JFXComboBox<EntryActiveState> comboStatus;
    @FXML
    private JFXComboBox<EntryProp> comboPropertyDate;
    @FXML
    private JFXComboBox<DateOperation> comboOperationDate;
    @FXML
    private JFXDatePicker pickerDate;
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
    
    private TextFieldValidator validatorSearchField;
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
        initializeControlValidators();
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
        comboOrder.getItems().addAll(OrderDirection.values());
        comboOrder.getSelectionModel().select(OrderDirection.ASC);
        
        comboOperation.getItems().addAll(StringOperation.values());
        comboOperation.getSelectionModel().select(StringOperation.CONTAINS);
        
        comboProperty.getItems().addAll(EntryProp.DESCRIPTION, EntryProp.HEADLINE, EntryProp.USERNAME);
        comboProperty.getSelectionModel().select(EntryProp.HEADLINE);
        
        comboOperationDate.getItems().addAll(DateOperation.values());
        comboOperationDate.getSelectionModel().select(DateOperation.IGNORE);
        
        comboPropertyDate.getItems().addAll(EntryProp.POSTEDON);
        comboPropertyDate.getSelectionModel().select(EntryProp.POSTEDON);
        
        comboStatus.getItems().addAll(EntryActiveState.values());
        comboStatus.getSelectionModel().select(EntryActiveState.IGNORE);
        
        pickerDate.setValue(LocalDate.now());
    }
    
    private void initializeControlValidators() {
        validatorSearchField = new TextFieldValidator(ApplicationManager.getDefaultValueProvider().getDefaultValidationPattern("search"));
        txtSearch.getValidators().add(validatorSearchField);
    }
    
    private boolean validateInputControls() {
        boolean isValid;
        txtSearch.validate();
        isValid = !(validatorSearchField.getHasErrors());
        return isValid;
    }

    private void disableControls(boolean disable) {
        Platform.runLater(() -> {
            txtSearch.setDisable(disable);
            btnSearch.setDisable(disable);    
            btnClose.setDisable(disable);
            spinner.setVisible(disable);
            lvOffers.setDisable(disable);
            comboOperation.setDisable(disable);
            comboOperationDate.setDisable(disable);
            comboOrder.setDisable(disable);
            comboProperty.setDisable(disable);
            comboPropertyDate.setDisable(disable);
            comboStatus.setDisable(disable);
            pickerDate.setDisable(disable);
        });
    }
    
    @FXML
    void onBtnClose(ActionEvent event) {
        closeWindow();
    }

    @FXML
    void onBtnSearch(ActionEvent event) {
        try {
            if (getSearch().length() == 0 && getEntryState() == EntryActiveState.IGNORE && getDateOperation() == DateOperation.IGNORE) {
                loadOffersFromWebService();
            } else {
                ArrayList<SearchCriteria<EntryProp>> criteria = new ArrayList<>();
                ArrayList<OrderElement<EntryProp>> order = new ArrayList<>();
                if (getSearch().length() > 0) {   
                    criteria.add(new StringSearchCriteria<>(getEntryProp(), getOperation(), getSearch(), true));
                }
                order.add(new OrderElement<>(getEntryProp(), getOrder()));
                if (getDateOperation() != DateOperation.IGNORE) {
                    criteria.add(new DateSearchCriteria<>(getDateEntryProp(), getDateOperation(), Date.valueOf(getDate())));
                }
                if (getEntryState() != EntryActiveState.IGNORE) {
                    criteria.add(new BooleanSearchCriteria<>(EntryProp.ISACTIVE, BooleanOperation.EQ, getEntryState()));
                }  
                EntrySearch search = new EntrySearch(criteria, order);
                disableControls(true);
                if (!communicator.requestOfferSearch(this, search)) {
                    disableControls(false);
                    displayMessage(new MessageContainer(MessageCodes.EXCEPTION, localizedValueProvider.getString("messageReauthentication")));
                }   
            }
        } catch (Exception e) {
            logger.error("onBtnSearch: exception occurred", e);
        }
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
            disableControls(false);
            displayMessage(new MessageContainer(MessageCodes.EXCEPTION, localizedValueProvider.getString("messageReauthentication")));
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
                        listItems.add(listItemFactory.generateOfferListItem(offer, this, this));
                    } 
                    if (listItems.isEmpty()) {
                        listItems.add(listItemFactory.generateMessageListItem("messageNoEntries", true));
                    }
                } catch (IOException ioex) {
                    disableControls(false);
                    logger.error("requestGetNewestOffersFinished: loading offers failed", ioex);
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
    public void requestOfferSearchFinished(RequestResult result) {
        disableControls(false);
        if (result.isSuccessful()) {
            Platform.runLater(() -> {
                try {
                    listItems.clear();
                    for (Offer offer : dataMapper.toOfferArrayList(result.getData(), DataMappingViews.Entry.In.Get.class)) {
                        listItems.add(listItemFactory.generateOfferListItem(offer, this, this));
                    } 
                    if (listItems.isEmpty()) {
                        listItems.add(listItemFactory.generateMessageListItem("messageNoEntries", true));
                    }
                } catch (IOException ioex) {
                    disableControls(false);
                    logger.error("requestOfferSearchFinished: loading offers failed", ioex);
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
        ApplicationManager.getHostFallbackService().requestCheck();
        displayMessage(new MessageContainer(MessageCodes.REQUEST_FAILED, localizedValueProvider.getString("messageConnectionFailed")));
        logger.error(result.getMessageContainer().toString());
    }
    
    private EntryProp getEntryProp() {
        return comboProperty.getSelectionModel().getSelectedItem();
    }

    private EntryProp getDateEntryProp() {
        return comboPropertyDate.getSelectionModel().getSelectedItem();
    }
    
    private StringOperation getOperation() {
        return comboOperation.getSelectionModel().getSelectedItem();
    }

    private DateOperation getDateOperation() {
        return comboOperationDate.getSelectionModel().getSelectedItem();
    }
    
    private OrderDirection getOrder() {
        return comboOrder.getSelectionModel().getSelectedItem();
    }

    private EntryActiveState getEntryState() {
        return comboStatus.getSelectionModel().getSelectedItem();
    }
    
    private String getSearch() {
        return txtSearch.getText();
    }
    
    private LocalDate getDate() {
        return pickerDate.getValue();
    }
    
    @Override
    public boolean removeListItem(AnchorPane listItem) {
        return listItems.remove(listItem);
    }
}
