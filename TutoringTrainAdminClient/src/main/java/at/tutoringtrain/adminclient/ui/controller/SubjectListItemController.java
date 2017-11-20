package at.tutoringtrain.adminclient.ui.controller;

import at.tutoringtrain.adminclient.data.Subject;
import at.tutoringtrain.adminclient.internationalization.LocalizedValueProvider;
import at.tutoringtrain.adminclient.io.network.Communicator;
import at.tutoringtrain.adminclient.io.network.RequestResult;
import at.tutoringtrain.adminclient.io.network.listener.subject.RequestDeleteSubjectListener;
import at.tutoringtrain.adminclient.main.ApplicationManager;
import at.tutoringtrain.adminclient.main.DataStorage;
import at.tutoringtrain.adminclient.main.MessageCodes;
import at.tutoringtrain.adminclient.main.MessageContainer;
import at.tutoringtrain.adminclient.ui.WindowService;
import at.tutoringtrain.adminclient.ui.listener.MessageListener;
import com.jfoenix.controls.JFXButton;
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
public class SubjectListItemController implements Initializable, RequestDeleteSubjectListener {
    @FXML
    private AnchorPane pane;
    @FXML
    private Label lblName;
    @FXML
    private Label lblActive;
    @FXML
    private JFXButton btnEdit;
    @FXML
    private JFXButton btnRemove;

    private Logger logger;
    private LocalizedValueProvider localizedValueProvider;
    private WindowService windowService;
    private Communicator communicator;
    private DataStorage dataStorage;
    
    private AllSubjectsController parentController;
    private MessageListener messageListener;
    
    private Subject subject;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        logger = LogManager.getLogger(this);
        localizedValueProvider = ApplicationManager.getLocalizedValueProvider();
        dataStorage = ApplicationManager.getDataStorage();
        windowService = ApplicationManager.getWindowService();
        communicator = ApplicationManager.getCommunicator();
        logger.debug("SubjectListItemController initialized"); 
    }   

    @FXML
    void onBtnEdit(ActionEvent event) {
        windowService.openUpdateSubjectWindow(subject, this);
    }

    @FXML
    void onBtnRemove(ActionEvent event) {
        if (subject != null) {
            if (parentController != null) {
                try {
                    communicator.requestDeleteSubject(this, subject);
                } catch (Exception ex) {
                    logger.error("onBtnRemove: exception occurred", ex);
                    displayMessage(new MessageContainer(MessageCodes.EXCEPTION, localizedValueProvider.getString("messageSeeLogForFurtherInformation")));
                }   
            } else {
                displayMessage(new MessageContainer(MessageCodes.SEE_APPLICATION_LOG, localizedValueProvider.getString("messageSeeLogForFurtherInformation")));
                logger.error("parent-controller required to remove subject");
            }
        } else {
            displayMessage(new MessageContainer(MessageCodes.SEE_APPLICATION_LOG, localizedValueProvider.getString("messageSeeLogForFurtherInformation")));
            logger.error("subject is null");
        }
    }
    
    public void setMessageListener(MessageListener listener) {
        this.messageListener = listener;
    }
    
    private void displayMessage(MessageContainer container) {
        if (messageListener != null) {
            messageListener.displayMessage(container);
        }
        logger.debug(container.toString());
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
        displaySubject();
    }

    public void setParentController(AllSubjectsController parentController) {
        this.parentController = parentController;
    }
    
    private void displaySubject() {
        if (subject == null) {
            lblName.setText("NULL");
            lblActive.setText("NULL");
        } else {
            lblName.setText(subject.getName());
            lblActive.setText(localizedValueProvider.getString(subject.isIsactive() ? "active" : "inactive"));
        }
    }

    @Override
    public void requestDeleteSubjectFinished(RequestResult result) {
        if (result.isSuccessful()) {
            displayMessage(new MessageContainer(MessageCodes.OK, localizedValueProvider.getString("messageSubjectSuccessfullyRemoved")));
            dataStorage.removeSubject(subject);
            Platform.runLater(() -> parentController.removeListItem(pane));            
        } else {
            displayMessage(result.getMessageContainer());
            logger.debug(result.getMessageContainer().toString());
        }
    }

    @Override
    public void requestFailed(RequestResult result) {
        displayMessage(new MessageContainer(MessageCodes.REQUEST_FAILED, localizedValueProvider.getString("messageUnexpectedFailure")));
        logger.error("Request failed with status code:" + result.getStatusCode());
        logger.error(result.getMessageContainer().toString());
    }
}
