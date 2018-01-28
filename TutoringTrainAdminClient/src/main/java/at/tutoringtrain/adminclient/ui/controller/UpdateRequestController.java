package at.tutoringtrain.adminclient.ui.controller;

import at.tutoringtrain.adminclient.data.entry.Request;
import at.tutoringtrain.adminclient.data.mapper.DataMapper;
import at.tutoringtrain.adminclient.data.mapper.DataMappingViews;
import at.tutoringtrain.adminclient.data.subject.Subject;
import at.tutoringtrain.adminclient.exception.RequiredParameterException;
import at.tutoringtrain.adminclient.internationalization.LocalizedValueProvider;
import at.tutoringtrain.adminclient.io.network.Communicator;
import at.tutoringtrain.adminclient.io.network.RequestResult;
import at.tutoringtrain.adminclient.io.network.listener.entry.request.RequestUpdateRequestListener;
import at.tutoringtrain.adminclient.io.network.listener.subject.RequestAllSubjectsListener;
import at.tutoringtrain.adminclient.main.ApplicationManager;
import at.tutoringtrain.adminclient.main.DataStorage;
import at.tutoringtrain.adminclient.main.DefaultValueProvider;
import at.tutoringtrain.adminclient.main.MessageCodes;
import at.tutoringtrain.adminclient.main.MessageContainer;
import at.tutoringtrain.adminclient.ui.TutoringTrainWindow;
import at.tutoringtrain.adminclient.ui.WindowService;
import at.tutoringtrain.adminclient.ui.listener.RequestChangedListener;
import at.tutoringtrain.adminclient.ui.validators.TextFieldValidator;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXSnackbar;
import com.jfoenix.controls.JFXSpinner;
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
public class UpdateRequestController implements Initializable, TutoringTrainWindow, RequestAllSubjectsListener, RequestUpdateRequestListener {
    @FXML
    private AnchorPane pane;
    @FXML
    private Label lblTitle;
    @FXML
    private JFXTextField txtHeadline;
    @FXML
    private JFXTextField txtDescription;
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
    private TextFieldValidator validatorHeadlineField, validatorDescriptionField;
    private RequestChangedListener requestChangedListener;
    private Request request, temporaryRequest;
    
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
        logger.debug("UpdateRequestController initialized");     
    }    
    
    private void initializeControls() {
        snackbar = new JFXSnackbar(pane);
        comboSubject.setItems(FXCollections.observableArrayList(dataStorage.getSubjects().values()));
        spinner.setVisible(false);
    }

    private void initializeControlValidators() {
        validatorHeadlineField = new TextFieldValidator(defaultValueProvider.getDefaultValidationPattern("headline"));
        validatorDescriptionField = new TextFieldValidator(defaultValueProvider.getDefaultValidationPattern("description"));
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
    
    public void setRequest(Request request) throws Exception {
        if (request == null) {
            closeWindow();
            throw new RequiredParameterException(request, "must not be null");
        }
        this.request = request;
        txtHeadline.setText(request.getHeadline());
        txtDescription.setText(request.getDescription());
        active.setSelected(request.getIsactive());
        Subject s = dataStorage.getSubject(request.getSubject().getId());
        if (s != null) { 
            comboSubject.getSelectionModel().select(s);    
        } else {
            disableControls(true);
            communicator.requestAllSubjects(this);     
        }
    }
    
    public void setRequestChangedListener(RequestChangedListener listener) {
        this.requestChangedListener = listener;
    }
    
    private void notifyRequestChanged(Request request) {
        if (requestChangedListener != null) {
            requestChangedListener.requestChanged(request);
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
                temporaryRequest = new Request();
                temporaryRequest.setId(request.getId());
                temporaryRequest.setHeadline(getHeadline());
                temporaryRequest.setDescription(getDescription());
                temporaryRequest.setIsactive(active.isSelected());
                temporaryRequest.setSubject(getSubject());
                if(!communicator.requestUpdateRequest(this, temporaryRequest)) {
                    disableControls(false);
                }
            } catch (Exception ex) {
                disableControls(false);
                logger.error("onBtnUpdate: excpetion occurred", ex);
            }
        }            
    }
    
    @Override
    public void requestUpdateRequestFinished(RequestResult result) {
        disableControls(false);     
        if (result.isSuccessful()) {
            request.setHeadline(temporaryRequest.getHeadline());
            request.setDescription(temporaryRequest.getDescription());
            request.setIsactive(temporaryRequest.getIsactive());
            request.setSubject(temporaryRequest.getSubject());
            notifyRequestChanged(request);
            displayMessage(new MessageContainer(MessageCodes.OK, localizedValueProvider.getString("messageRequestSuccessfullyUpdated")));
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
                    comboSubject.getSelectionModel().select(dataStorage.getSubject(request.getSubject().getId()));      
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
