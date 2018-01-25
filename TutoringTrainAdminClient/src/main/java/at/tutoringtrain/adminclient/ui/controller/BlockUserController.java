package at.tutoringtrain.adminclient.ui.controller;

import at.tutoringtrain.adminclient.data.user.BlockRequest;
import at.tutoringtrain.adminclient.data.user.Blocked;
import at.tutoringtrain.adminclient.data.user.User;
import at.tutoringtrain.adminclient.exception.RequiredParameterException;
import at.tutoringtrain.adminclient.internationalization.LocalizedValueProvider;
import at.tutoringtrain.adminclient.io.network.Communicator;
import at.tutoringtrain.adminclient.io.network.RequestResult;
import at.tutoringtrain.adminclient.io.network.listener.user.RequestBlockUserListener;
import at.tutoringtrain.adminclient.main.ApplicationManager;
import at.tutoringtrain.adminclient.main.DataStorage;
import at.tutoringtrain.adminclient.main.DefaultValueProvider;
import at.tutoringtrain.adminclient.main.MessageCodes;
import at.tutoringtrain.adminclient.main.MessageContainer;
import at.tutoringtrain.adminclient.ui.TutoringTrainWindow;
import at.tutoringtrain.adminclient.ui.WindowService;
import at.tutoringtrain.adminclient.ui.listener.UserBlockListner;
import at.tutoringtrain.adminclient.ui.validators.TextFieldValidator;
import at.tutoringtrain.adminclient.user.BlockDuration;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXSnackbar;
import com.jfoenix.controls.JFXSpinner;
import com.jfoenix.controls.JFXTextField;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * FXML Controller class
 *
 * @author Marco Wilscher marco.wilscher@edu.htl-villach.at
 */
public class BlockUserController implements Initializable, TutoringTrainWindow, RequestBlockUserListener {
    @FXML
    private AnchorPane pane;
    @FXML
    private Label lblTitle;
    @FXML
    private JFXTextField txtUsername;
    @FXML
    private JFXTextField txtName;
    @FXML
    private JFXTextField txtReason;
    @FXML
    private JFXButton btnBlock;
    @FXML
    private JFXButton btnClose;
    @FXML
    private ImageView ivAvatar;
    @FXML
    private JFXSpinner spinner;
    @FXML
    private JFXComboBox<BlockDuration> comboDuration;

    private JFXSnackbar snackbar;    
    
    private LocalizedValueProvider localizedValueProvider;
    private DefaultValueProvider defaultValueProvider;
    private Logger logger; 
    private DataStorage dataStorage;
    private Communicator communicator;
    private WindowService windowService;
    private ApplicationManager applicationManager;
    
    private TextFieldValidator validatorReasonField;
    
    private UserBlockListner userBlockListner;
    
    private User user;
    private Blocked blocked;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        logger = LogManager.getLogger(this);
        applicationManager = ApplicationManager.getInstance();
        dataStorage = ApplicationManager.getDataStorage();
        communicator = ApplicationManager.getCommunicator();
        windowService = ApplicationManager.getWindowService();
        localizedValueProvider = ApplicationManager.getLocalizedValueProvider();
        defaultValueProvider = ApplicationManager.getDefaultValueProvider();
        initializeControls();
        initializeControlValidators();
        logger.debug("BlockUserController initialized");           
    } 
    
    private void initializeControls() {
        snackbar = new JFXSnackbar(pane);
        spinner.setVisible(false);
        comboDuration.getItems().addAll(ApplicationManager.getDefaultValueProvider().getDefaultBlockDurations());
        comboDuration.getSelectionModel().selectFirst();
    }
    
    private void initializeControlValidators() {
        validatorReasonField = new TextFieldValidator(defaultValueProvider.getDefaultValidationPattern("reason"));
        txtReason.getValidators().add(validatorReasonField);
    }
    
    private boolean validateInputControls() {
        boolean isValid;
        txtReason.validate(); 
        isValid = !(validatorReasonField.getHasErrors());
        return isValid;
    }
    
    private void disableControls(boolean disable) {
        Platform.runLater(() -> {
            txtUsername.setDisable(disable);
            txtName.setDisable(disable);
            txtReason.setDisable(disable);
            btnClose.setDisable(disable);
            btnBlock.setDisable(disable);
            comboDuration.setDisable(disable);
            spinner.setVisible(disable);
        });
    }
    
    @FXML
    void onBtnBlock(ActionEvent event) {
         try {
            if (validateInputControls()) {
                disableControls(true);
                BlockRequest blockRequest = new BlockRequest(getUsername(), getReason(), getDuration());
                if (!communicator.requestBlockUser(this, blockRequest)) {
                    disableControls(false);
                    displayMessage(new MessageContainer(MessageCodes.EXCEPTION, localizedValueProvider.getString("messageReauthentication")));
                }
                blocked = new Blocked();
                blocked.setDuedate(blockRequest.getDuedate());
                blocked.setReason(blockRequest.getReason());
            }
        } catch (Exception ex) {
            disableControls(false);
            logger.error("onBtnBlock", ex);
            displayMessage(new MessageContainer(MessageCodes.EXCEPTION, localizedValueProvider.getString("messageUnexpectedFailure")));
        }
    }

    @FXML
    void onBtnClose(ActionEvent event) {
        closeWindow();
    }
    
    @Override
    public void displayMessage(MessageContainer container) {
        windowService.displayMessage(snackbar, container);
    }
    
    @Override
    public void closeWindow() {
        windowService.closeWindow(pane);
    }
    
    public void setUserBlockListner(UserBlockListner userBlockListner) {
        this.userBlockListner = userBlockListner;
    }
    
    public void setUser(User user) throws RequiredParameterException {
        if (user == null) {
            closeWindow();
            throw new RequiredParameterException(user, "must not be null");
        }
        this.user = user;
        if (user != null) {
            txtUsername.setText(user.getUsername());
            txtName.setText(user.getName());
            comboDuration.getSelectionModel().selectFirst();
        } 
    }
    
    private String getUsername() {
        return user.getUsername();
    }

    private String getReason() {
        return txtReason.getText();
    }

    private BlockDuration getDuration() {
        return comboDuration.getSelectionModel().getSelectedItem();
    }
    
    private void notifyBlockListener() {
        if (userBlockListner != null) {
            userBlockListner.userBlocked(user);
        }
    }
    
    @Override
    public void requestBlockUserFinished(RequestResult result) {
        disableControls(false);
        if (result.isSuccessful()) {
            user.setBlock(blocked);
            notifyBlockListener();
            displayMessage(new MessageContainer(MessageCodes.OK, localizedValueProvider.getString("messageUserSuccessfullyBlocked")));
        } else {     
            displayMessage(result.getMessageContainer());
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
