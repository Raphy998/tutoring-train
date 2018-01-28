package at.tutoringtrain.adminclient.ui.controller;

import at.tutoringtrain.adminclient.data.entry.Request;
import at.tutoringtrain.adminclient.internationalization.Language;
import at.tutoringtrain.adminclient.internationalization.LocalizedValueProvider;
import at.tutoringtrain.adminclient.io.network.Communicator;
import at.tutoringtrain.adminclient.main.ApplicationManager;
import at.tutoringtrain.adminclient.main.DataStorage;
import at.tutoringtrain.adminclient.main.MessageCodes;
import at.tutoringtrain.adminclient.main.MessageContainer;
import at.tutoringtrain.adminclient.ui.WindowService;
import at.tutoringtrain.adminclient.ui.listener.MessageListener;
import at.tutoringtrain.adminclient.ui.listener.RequestChangedListener;
import com.jfoenix.controls.JFXButton;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * FXML Controller class
 *
 * @author Marco Wilscher marco.wilscher@edu.htl-villach.at
 */
public class RequestListItemController implements Initializable, RequestChangedListener {
    @FXML
    private AnchorPane pane;
    @FXML
    private HBox boxImage;
    @FXML
    private ImageView ivIcon;
    @FXML
    private VBox boxEntryDetails;
    @FXML
    private Label lblHeadline;
    @FXML
    private Label lblActive;
    @FXML
    private Label lblPostedOn;
    @FXML
    private Label lblSubject;
    @FXML
    private Label lblUsername;
    @FXML
    private Label lblDescription;
    @FXML
    private VBox boxButtons;
    @FXML
    private JFXButton btnEdit;
    @FXML
    private JFXButton btnDelete;

    private Logger logger;
    private Language language;
    private LocalizedValueProvider localizedValueProvider;
    private WindowService windowService;
    private Communicator communicator;
    private DataStorage dataStorage;
    
    private MessageListener messageListener;
    private Request request;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        logger = LogManager.getLogger(this);
        localizedValueProvider = ApplicationManager.getLocalizedValueProvider();
        language = ApplicationManager.getInstance().getLanguage();
        dataStorage = ApplicationManager.getDataStorage();
        windowService = ApplicationManager.getWindowService();
        communicator = ApplicationManager.getCommunicator();
        logger.debug("OfferListItemController initialized"); 
    }   

    @FXML
    void onBtnEdit(ActionEvent event) {
        try {
            windowService.openUpdateRequestWindow(request, this);
        } catch (Exception e) {
            displayMessage(new MessageContainer(MessageCodes.SEE_APPLICATION_LOG, localizedValueProvider.getString("messageSeeLogForFurtherInformation")));
            logger.error("onBtnEdit: exception occurred", e);
        }
    }
    
    @FXML
    void onBtnDelete(ActionEvent event) {
        try {
            displayMessage(new MessageContainer(MessageCodes.NOT_IMPLEMENTED_YET, "NOT IMPLEMENTED YET"));
            //if (windowService.openConfirmDialog("dialogHeaderDeleteOffer", "dialogContenDeleteOffer", new StringPlaceholder("headline", offer.getHeadline())).get() == ButtonType.OK) {
                //TODO     
            //}
        } catch (Exception ex) {
            logger.error(ex);
            displayMessage(new MessageContainer(MessageCodes.EXCEPTION, localizedValueProvider.getString("messageUnexpectedFailure")));
        }
    }
    
    public void setMessageListener(MessageListener listener) {
        this.messageListener = listener;
    }
    
    private void displayMessage(MessageContainer container) {
        if (messageListener != null) {
            messageListener.displayMessage(container);
        }
    }

    public void setRequest(Request request) {
        this.request = request;
        displayRequest();
    }
    
    private void displayRequest() {
        if (request == null) {
            lblHeadline.setText("NULL");
            lblPostedOn.setText("NULL");
            lblActive.setText("NULL");
            lblSubject.setText("NULL");
            lblUsername.setText("NULL");
            lblDescription.setText("NULL");
            ivIcon.setOpacity(0);
            btnDelete.setDisable(true);
            btnEdit.setDisable(true);
        } else {
            lblHeadline.setText(request.getHeadline());
            lblPostedOn.setText(new SimpleDateFormat("dd.MM.yyyy HH:mm").format(request.getPostedon()));
            lblActive.setText(localizedValueProvider.getString(request.getIsactive() ? "active" : "inactive"));
            lblSubject.setText(request.getSubject().getName());
            lblUsername.setText("@" + request.getUser().getUsername() + " - " + request.getUser().getName());
            lblDescription.setText(request.getDescription());
            ivIcon.setOpacity(request.getIsactive()? 1 : 0.5);
            btnDelete.setDisable(false);
            btnEdit.setDisable(false);
        }
    }
    
    @Override
    public void requestChanged(Request request) {
        this.request = request;
        Platform.runLater(()-> displayRequest());
    }
}
