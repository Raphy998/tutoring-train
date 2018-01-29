package at.tutoringtrain.adminclient.ui.controller;

import at.tutoringtrain.adminclient.data.entry.Offer;
import at.tutoringtrain.adminclient.internationalization.Language;
import at.tutoringtrain.adminclient.internationalization.LocalizedValueProvider;
import at.tutoringtrain.adminclient.internationalization.StringPlaceholder;
import at.tutoringtrain.adminclient.io.network.Communicator;
import at.tutoringtrain.adminclient.io.network.RequestResult;
import at.tutoringtrain.adminclient.io.network.listener.entry.offer.RequestDeleteOfferListener;
import at.tutoringtrain.adminclient.main.ApplicationManager;
import at.tutoringtrain.adminclient.main.DataStorage;
import at.tutoringtrain.adminclient.main.MessageCodes;
import at.tutoringtrain.adminclient.main.MessageContainer;
import at.tutoringtrain.adminclient.ui.WindowService;
import at.tutoringtrain.adminclient.ui.listener.MessageListener;
import at.tutoringtrain.adminclient.ui.listener.OfferChangedListener;
import com.jfoenix.controls.JFXButton;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ButtonType;
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
public class OfferListItemController implements Initializable, OfferChangedListener, RequestDeleteOfferListener {
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
    private AllOffersController parentController;
    private MessageListener messageListener;
    private Offer offer;
    
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
            windowService.openUpdateOfferWindow(offer, this);
        } catch (Exception e) {
            displayMessage(new MessageContainer(MessageCodes.SEE_APPLICATION_LOG, localizedValueProvider.getString("messageSeeLogForFurtherInformation")));
            logger.error("onBtnEdit: exception occurred", e);
        }
    }
    
    @FXML
    void onBtnDelete(ActionEvent event) {
        try {
            if (windowService.openConfirmDialog("dialogHeaderDeleteOffer", "dialogContenDeleteOffer", new StringPlaceholder("headline", offer.getHeadline())).get() == ButtonType.OK) {
                btnDelete.setDisable(true);
                if(!communicator.requestDeleteOffer(this, offer)) {
                    btnDelete.setDisable(false);
                    displayMessage(new MessageContainer(MessageCodes.EXCEPTION, localizedValueProvider.getString("messageReauthentication")));
                } 
            }
        } catch (Exception ex) {
            logger.error(ex);
            displayMessage(new MessageContainer(MessageCodes.EXCEPTION, localizedValueProvider.getString("messageUnexpectedFailure")));
        }
    }
    
    public void setMessageListener(MessageListener listener) {
        this.messageListener = listener;
    }

    public void setParentController(AllOffersController parentController) {
        this.parentController = parentController;
    }
    
    private void displayMessage(MessageContainer container) {
        if (messageListener != null) {
            messageListener.displayMessage(container);
        }
    }

    public void setOffer(Offer offer) {
        this.offer = offer;
        displayOffer();
    }
    
    private void displayOffer() {
        if (offer == null) {
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
            lblHeadline.setText(offer.getHeadline());
            lblPostedOn.setText(new SimpleDateFormat("dd.MM.yyyy HH:mm").format(offer.getPostedon()));
            lblActive.setText(localizedValueProvider.getString(offer.getIsactive() ? "active" : "inactive"));
            lblSubject.setText(offer.getSubject().getName());
            lblUsername.setText("@" + offer.getUser().getUsername() + " - " + offer.getUser().getName());
            lblDescription.setText(offer.getDescription());
            ivIcon.setOpacity(offer.getIsactive()? 1 : 0.5);
            btnDelete.setDisable(false);
            btnEdit.setDisable(false);
        }
    }
    
    @Override
    public void offerChanged(Offer offer) {
        this.offer = offer;
        Platform.runLater(() -> displayOffer());
    }
    
    @Override
    public void requestDeleteOfferFinished(RequestResult result) {
        if (result.isSuccessful()) {
            displayMessage(new MessageContainer(MessageCodes.OK, localizedValueProvider.getString("messageOfferSuccessfullyRemoved")));
            Platform.runLater(() -> parentController.removeListItem(pane));            
        } else {
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
