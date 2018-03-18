package at.tutoringtrain.adminclient.ui.controller;

import at.tutoringtrain.adminclient.data.entry.Comment;
import at.tutoringtrain.adminclient.data.entry.Entry;
import at.tutoringtrain.adminclient.data.entry.EntryType;
import at.tutoringtrain.adminclient.internationalization.LocalizedValueProvider;
import at.tutoringtrain.adminclient.io.network.Communicator;
import at.tutoringtrain.adminclient.io.network.RequestResult;
import at.tutoringtrain.adminclient.io.network.listener.entry.comment.RequestDeleteCommentListener;
import at.tutoringtrain.adminclient.main.ApplicationManager;
import at.tutoringtrain.adminclient.main.MessageCodes;
import at.tutoringtrain.adminclient.main.MessageContainer;
import at.tutoringtrain.adminclient.ui.WindowService;
import at.tutoringtrain.adminclient.ui.listener.MessageListener;
import at.tutoringtrain.adminclient.ui.listener.RemoveItemListener;
import com.jfoenix.controls.JFXButton;
import java.net.URL;
import java.text.SimpleDateFormat;
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
public class CommentListItemController implements Initializable, RequestDeleteCommentListener {
    @FXML
    private AnchorPane pane;
    @FXML
    private Label lblUsername;
    @FXML
    private Label lblPostedOn;
    @FXML
    private Label lblText;
    @FXML
    private JFXButton btnDelete;    
    
    private Logger logger;
    private WindowService windowService;
    private Communicator communicator;
    private LocalizedValueProvider localizedValueProvider;
    private RemoveItemListener removeItemListener;
    private MessageListener messageListener;
    private Entry entry;
    private EntryType entryType;
    private Comment comment;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        logger = LogManager.getLogger(this);
        localizedValueProvider = ApplicationManager.getLocalizedValueProvider();
        windowService = ApplicationManager.getWindowService();
        communicator = ApplicationManager.getCommunicator();
        logger.debug("CommentListItemController initialized"); 
    }    
    
    @FXML
    void onBtnDelete(ActionEvent event) {
        try {
            btnDelete.setDisable(true);
            if(!communicator.requestDeleteComment(this, entry.getId(), entryType, comment.getId())) {
                btnDelete.setDisable(false);
                displayMessage(new MessageContainer(MessageCodes.EXCEPTION, localizedValueProvider.getString("messageReauthentication")));
            }     
        } catch (Exception ex) {
            logger.error(ex);
            displayMessage(new MessageContainer(MessageCodes.EXCEPTION, localizedValueProvider.getString("messageUnexpectedFailure")));
        }
    }

    public void setMessageListener(MessageListener messageListener) {
        this.messageListener = messageListener;
    }

    public void setRemoveItemListener(RemoveItemListener removeItemListener) {
        this.removeItemListener = removeItemListener;
    }

    public void setEntry(Entry entry, EntryType entryType) {
        this.entry = entry;
        this.entryType = entryType;
    }
    
    public void setComment(Comment comment) {
        this.comment = comment;
        displayComment();
    }
    
    private void displayMessage(MessageContainer container) {
        if (messageListener != null) {
            messageListener.displayMessage(container);
        }
    }
    
    private void displayComment() {
        if (comment == null) {
            lblPostedOn.setText("NULL");
            lblUsername.setText("NULL");
            lblText.setText("NULL");
            btnDelete.setDisable(true);
        } else {
            lblPostedOn.setText(new SimpleDateFormat("dd.MM.yyyy HH:mm").format(comment.getPostedon()));
            lblUsername.setText("@" + comment.getUser().getUsername());
            lblText.setText(comment.getText());
            btnDelete.setDisable(false);
        }
    }

    @Override
    public void requestDeleteCommentFinished(RequestResult result) {
        if (result.isSuccessful()) {
            displayMessage(new MessageContainer(MessageCodes.OK, localizedValueProvider.getString("messageCommentSuccessfullyRemoved")));
            Platform.runLater(() -> removeItemListener.removeListItem(pane));            
        } else {
            btnDelete.setDisable(false);
            displayMessage(result.getMessageContainer());
            logger.debug(result.getMessageContainer().toString());
        }
    }

    @Override
    public void requestFailed(RequestResult result) {
        btnDelete.setDisable(false);
        ApplicationManager.getHostFallbackService().requestCheck();
        displayMessage(new MessageContainer(MessageCodes.REQUEST_FAILED, localizedValueProvider.getString("messageConnectionFailed")));
        logger.error(result.getMessageContainer().toString());
    }
}
