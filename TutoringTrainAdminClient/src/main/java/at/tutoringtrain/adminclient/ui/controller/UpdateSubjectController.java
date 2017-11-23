package at.tutoringtrain.adminclient.ui.controller;

import at.tutoringtrain.adminclient.data.Subject;
import at.tutoringtrain.adminclient.exception.RequiredParameterException;
import at.tutoringtrain.adminclient.internationalization.Language;
import at.tutoringtrain.adminclient.internationalization.LocalizedValueProvider;
import at.tutoringtrain.adminclient.io.network.Communicator;
import at.tutoringtrain.adminclient.io.network.RequestResult;
import at.tutoringtrain.adminclient.io.network.listener.subject.RequestUpdateSubjectListener;
import at.tutoringtrain.adminclient.main.ApplicationManager;
import at.tutoringtrain.adminclient.main.DataStorage;
import at.tutoringtrain.adminclient.main.DefaultValueProvider;
import at.tutoringtrain.adminclient.main.MessageCodes;
import at.tutoringtrain.adminclient.main.MessageContainer;
import at.tutoringtrain.adminclient.ui.TutoringTrainWindow;
import at.tutoringtrain.adminclient.ui.WindowService;
import at.tutoringtrain.adminclient.ui.listener.SubjectChangedListener;
import at.tutoringtrain.adminclient.ui.validators.TextFieldValidator;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXSnackbar;
import com.jfoenix.controls.JFXSpinner;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXToggleButton;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * FXML Controller class
 *
 * @author Marco Wilscher marco.wilscher@edu.htl-villach.at
 */
public class UpdateSubjectController implements Initializable, TutoringTrainWindow, RequestUpdateSubjectListener {
    @FXML
    private AnchorPane pane;
    @FXML
    private Label txtName;
    @FXML
    private JFXTextField txtEName;
    @FXML
    private JFXTextField txtDName;
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
    private Communicator communicator;
    private WindowService windowService;
    private ApplicationManager applicationManager;
    
    private TextFieldValidator validatorEName;
    private TextFieldValidator validatorDEame;
    
    private SubjectChangedListener subjectStateListener;
   
    private Subject subject, temporarySubject;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        logger = LogManager.getLogger(this);
        applicationManager = ApplicationManager.getInstance();
        dataStorage = ApplicationManager.getDataStorage();
        communicator = ApplicationManager.getCommunicator();
        windowService = ApplicationManager.getWindowService();
        localizedValueProvider = ApplicationManager.getLocalizedValueProvider();
        defaultValueProvider = ApplicationManager.getDefaultValueProvider();
        initializeControls();
        initializeControlValidators();
        logger.debug("UpdateSubjectController initialized"); 
    }    
    
    @FXML
    void onBtnClose(ActionEvent event) {
        closeWindow();
    }

    @FXML
    void onBtnUpdate(ActionEvent event)  {
        if (!StringUtils.isBlank(getDEName()) || !StringUtils.isBlank(getENName())) {
            if (validateInputControls()) {
                disableControls(true);
                try {
                    temporarySubject = new Subject(getENName(), getDEName());
                    temporarySubject.setId(subject.getId());
                    temporarySubject.setName(applicationManager.getLanguage() == Language.EN ? temporarySubject.getEnname() : temporarySubject.getDename());
                    temporarySubject.setIsactive(active.isSelected());
                    if(!communicator.requestUpdateSubject(this, temporarySubject)) {
                        disableControls(false);
                    }
                } catch (Exception ex) {
                    disableControls(false);
                    logger.error("onBtnUpdate: excpetion occurred", ex);
                }
            }            
        } else {
            disableControls(true);
            try {
                temporarySubject = new Subject(subject.getId());
                temporarySubject.setName(subject.getName());
                temporarySubject.setIsactive(active.isSelected());
                if(!communicator.requestUpdateSubjectState(this, temporarySubject)) {
                    disableControls(false);
                }
            } catch (Exception ex) {
                disableControls(false);
                logger.error("onBtnUpdate: excpetion occurred", ex);
            }
        }
    }


    private void initializeControls() {
        snackbar = new JFXSnackbar(pane);
        spinner.setVisible(false);
    }

    private void initializeControlValidators() {
        validatorDEame = new TextFieldValidator(defaultValueProvider.getDefaultValidationPattern("subjectname"));  
        validatorEName = new TextFieldValidator(defaultValueProvider.getDefaultValidationPattern("subjectname"));
        txtDName.getValidators().add(validatorDEame);
        txtEName.getValidators().add(validatorEName);
    }

    

    private boolean validateInputControls() {
        boolean isValid;
        txtDName.validate();
        txtEName.validate();
        isValid = !(validatorDEame.getHasErrors() || validatorEName.getHasErrors());
        return isValid;
    }

    private void disableControls(boolean disable) {
        Platform.runLater(() -> {
            txtDName.setDisable(disable);
            txtEName.setDisable(disable);
            active.setDisable(disable);
            btnUpdate.setDisable(disable);
            btnClose.setDisable(disable);
            spinner.setVisible(disable);
        });
    }

    private String getDEName() {
        return txtDName.getText();
    }
    
    private String getENName() {
        return txtEName.getText();
    }
    
    private boolean getIsActive() {
        return active.isSelected();
    }
               
    public void setSubject(Subject subject) throws Exception {
        if (subject == null) {
            closeWindow();
            throw new RequiredParameterException(subject, "must not be null");
        }
        this.subject = subject;
        txtDName.setText(this.subject.getDename());
        txtEName.setText(this.subject.getEnname());
        txtName.setText(this.subject.getName());
        active.setSelected(this.subject.isIsactive());
    }

    public void setSubjectStateListener(SubjectChangedListener subjectStateListener) {
        this.subjectStateListener = subjectStateListener;
    }
    
    private void notifySubjectChangedListener() {
        if (subjectStateListener != null) {
            Platform.runLater(() -> subjectStateListener.subjectChanged(subject));
        }
    }

    @Override
    public void requestUpdateSubjectFinished(RequestResult result) {
        disableControls(false);     
        if (result.isSuccessful()) {
            subject.setName(temporarySubject.getName());
            subject.setIsactive(temporarySubject.isIsactive());
            Platform.runLater(() -> txtName.setText(temporarySubject.getName()));
            notifySubjectChangedListener();
            displayMessage(new MessageContainer(MessageCodes.OK, localizedValueProvider.getString("messageSubjectSuccessfullyUpdated")));
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
    
    @Override
    public void displayMessage(MessageContainer container) {
        logger.debug(container.toString());
        windowService.displayMessage(snackbar, container);
    }
    
    @Override
    public void closeWindow() {
        windowService.closeWindow(pane);
    } 
}
