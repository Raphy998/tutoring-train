package at.tutoringtrain.adminclient.ui.controller;

import at.tutoringtrain.adminclient.data.entry.Offer;
import at.tutoringtrain.adminclient.data.mapper.DataMapper;
import at.tutoringtrain.adminclient.data.mapper.DataMappingViews;
import at.tutoringtrain.adminclient.data.subject.Subject;
import at.tutoringtrain.adminclient.exception.RequiredParameterException;
import at.tutoringtrain.adminclient.internationalization.LocalizedValueProvider;
import at.tutoringtrain.adminclient.io.network.Communicator;
import at.tutoringtrain.adminclient.io.network.RequestResult;
import at.tutoringtrain.adminclient.io.network.listener.entry.offer.RequestUpdateOfferListener;
import at.tutoringtrain.adminclient.io.network.listener.subject.RequestAllSubjectsListener;
import at.tutoringtrain.adminclient.main.ApplicationManager;
import at.tutoringtrain.adminclient.main.DataStorage;
import at.tutoringtrain.adminclient.main.DefaultValueProvider;
import at.tutoringtrain.adminclient.main.MessageCodes;
import at.tutoringtrain.adminclient.main.MessageContainer;
import at.tutoringtrain.adminclient.ui.TutoringTrainWindow;
import at.tutoringtrain.adminclient.ui.WindowService;
import at.tutoringtrain.adminclient.ui.listener.OfferChangedListener;
import at.tutoringtrain.adminclient.ui.validators.DescriptionTextFieldValidator;
import at.tutoringtrain.adminclient.ui.validators.TextFieldValidator;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXSnackbar;
import com.jfoenix.controls.JFXSpinner;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXToggleButton;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.collections.FXCollections;
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
public class UpdateOfferController implements Initializable, TutoringTrainWindow, RequestAllSubjectsListener, RequestUpdateOfferListener {
    @FXML
    private AnchorPane pane;
    @FXML
    private Label lblTitle;
    @FXML
    private JFXTextField txtHeadline;
    @FXML
    private JFXTextArea txtDescription;
    @FXML
    private JFXComboBox<Subject> comboSubject;
    @FXML
    private JFXToggleButton active;
    @FXML
    private JFXButton btnUpdate;
    @FXML
    private JFXButton btnClose;
    @FXML
    private JFXSpinner spinner;

    private JFXSnackbar snackbar;    
    private LocalizedValueProvider localizedValueProvider;
    private DefaultValueProvider defaultValueProvider;
    private Logger logger; 
    private DataStorage dataStorage;
    private DataMapper dataMapper;
    private Communicator communicator;
    private WindowService windowService;
    private ApplicationManager applicationManager; 
    private TextFieldValidator validatorHeadlineField;
    private DescriptionTextFieldValidator validatorDescriptionField;
    private OfferChangedListener offerChangedListener;
    private Offer offer, temporaryOffer;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        logger = LogManager.getLogger(this);
        applicationManager = ApplicationManager.getInstance();
        dataStorage = ApplicationManager.getDataStorage();
        communicator = ApplicationManager.getCommunicator();
        dataMapper = ApplicationManager.getDataMapper();
        windowService = ApplicationManager.getWindowService();
        localizedValueProvider = ApplicationManager.getLocalizedValueProvider();
        defaultValueProvider = ApplicationManager.getDefaultValueProvider();
        initializeControls();
        initializeControlValidators();
        logger.debug("UpdateOfferController initialized");     
    }    
    
    private void initializeControls() {
        snackbar = new JFXSnackbar(pane);
        comboSubject.setItems(FXCollections.observableArrayList(dataStorage.getSubjects().values()));
        spinner.setVisible(false);
    }

    private void initializeControlValidators() {
        validatorHeadlineField = new TextFieldValidator(defaultValueProvider.getDefaultValidationPattern("headline"));
        validatorDescriptionField = new DescriptionTextFieldValidator(defaultValueProvider.getDefaultValidationPattern("description"));
        txtHeadline.getValidators().add(validatorHeadlineField);
        txtDescription.getValidators().add(validatorDescriptionField);
    }

    private boolean validateInputControls() {
        boolean isValid;
        txtHeadline.validate();
        txtDescription.validate();
        isValid = !(validatorHeadlineField.getHasErrors() || validatorDescriptionField.getHasErrors());
        return isValid;
    }

    private void disableControls(boolean disable) {
        Platform.runLater(() -> {
            txtHeadline.setDisable(disable);
            txtDescription.setDisable(disable);
            comboSubject.setDisable(disable);    
            active.setDisable(disable);
            btnUpdate.setDisable(disable);
            btnClose.setDisable(disable);        
            spinner.setVisible(disable);
            lblTitle.setDisable(disable);
        });
    }
    
    private String getHeadline() {
        return txtHeadline.getText();
    }

    private String getDescription() {
        return txtDescription.getText();
    }

    private Subject getSubject() {
        return comboSubject.getSelectionModel().getSelectedItem();
    }
    
    private boolean getActive() {
        return active.isSelected();
    }
    
    public void setOffer(Offer offer) throws Exception {
        if (offer == null) {
            closeWindow();
            throw new RequiredParameterException(offer, "must not be null");
        }
        this.offer = offer;
        txtHeadline.setText(offer.getHeadline());
        txtDescription.setText(offer.getDescription());
        active.setSelected(offer.getIsactive());
        Subject s = dataStorage.getSubject(offer.getSubject().getId());
        if (s != null) { 
            comboSubject.getSelectionModel().select(s);    
        } else {
            disableControls(true);
            communicator.requestAllSubjects(this);     
        }
    }
    
    public void setOfferChangedListener(OfferChangedListener listener) {
        this.offerChangedListener = listener;
    }
    
    private void notifyOfferChanged(Offer offer) {
        if (offerChangedListener != null) {
            offerChangedListener.offerChanged(offer);
        }
    }
    
    @Override
    public void displayMessage(MessageContainer container) {
        windowService.displayMessage(snackbar, container);
    }
    
    @Override
    public void closeWindow() {
        windowService.closeWindow(pane);
    }
    
    @FXML
    void onBtnClose(ActionEvent event) {
        closeWindow();
    }

    @FXML
    void onBtnUpdate(ActionEvent event) {
        if (validateInputControls()) {
            disableControls(true);
            try {
                temporaryOffer = new Offer();
                temporaryOffer.setId(offer.getId());
                temporaryOffer.setHeadline(getHeadline());
                temporaryOffer.setDescription(getDescription());
                temporaryOffer.setIsactive(active.isSelected());
                temporaryOffer.setSubject(getSubject());
                if(!communicator.requestUpdateOffer(this, temporaryOffer)) {
                    disableControls(false);
                }
            } catch (Exception ex) {
                disableControls(false);
                logger.error("onBtnUpdate: excpetion occurred", ex);
            }
        }            
    }
    
    @Override
    public void requestUpdateOfferFinished(RequestResult result) {
        disableControls(false);     
        if (result.isSuccessful()) {
            offer.setHeadline(temporaryOffer.getHeadline());
            offer.setDescription(temporaryOffer.getDescription());
            offer.setIsactive(temporaryOffer.getIsactive());
            offer.setSubject(temporaryOffer.getSubject());
            notifyOfferChanged(offer);
            displayMessage(new MessageContainer(MessageCodes.OK, localizedValueProvider.getString("messageOfferSuccessfullyUpdated")));
        } else {
            if (result.getMessageContainer().getCode() == 3 || result.getMessageContainer().getCode() == 4) {
                displayMessage(new MessageContainer(MessageCodes.EXCEPTION, localizedValueProvider.getString("messageReauthentication")));
            } else {
                displayMessage(result.getMessageContainer());
            }
        }
    }
    
    @Override
    public void requestGetAllSubjectsFinished(RequestResult result) {
        disableControls(false);
        if (result.isSuccessful()) {
            Platform.runLater(() -> {
                try {
                    dataStorage.clearSubjects();
                    for (Subject subject : dataMapper.toSubjectArrayList(result.getData(), DataMappingViews.Subject.In.Get.class)) {
                        dataStorage.addSubject(subject);
                    } 
                    comboSubject.setItems(FXCollections.observableArrayList(dataStorage.getSubjects().values()));
                    comboSubject.getSelectionModel().select(dataStorage.getSubject(offer.getSubject().getId()));      
                } catch (IOException ioex) {
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
        ApplicationManager.getHostFallbackService().requestCheck();
        displayMessage(new MessageContainer(MessageCodes.REQUEST_FAILED, localizedValueProvider.getString("messageConnectionFailed")));
        logger.error(result.getMessageContainer().toString());
    }
}
